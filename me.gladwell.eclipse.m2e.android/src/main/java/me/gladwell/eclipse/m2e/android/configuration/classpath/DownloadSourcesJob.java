package me.gladwell.eclipse.m2e.android.configuration.classpath;

import static me.gladwell.eclipse.m2e.android.Log.warn;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import me.gladwell.eclipse.m2e.android.AndroidMavenPlugin;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.m2e.core.embedder.ArtifactKey;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;

import com.google.inject.Inject;

/**
 * Adapted from:
 * http://git.eclipse.org/c/m2e/m2e-core.git/tree/org.eclipse.m2e.jdt/src/org/eclipse/m2e/jdt/internal/DownloadSourcesJob.java
 */
public class DownloadSourcesJob extends Job {

    private static final String JOB_NAME = "Downloading sources and JavaDoc";

    private static class DownloadRequest {
        final IProject project;

        final IPackageFragmentRoot fragment;

        final ArtifactKey artifact;

        final boolean downloadSources;

        final boolean downloadJavaDoc;

        public DownloadRequest(IProject project, IPackageFragmentRoot fragment, ArtifactKey artifact,
                boolean downloadSources, boolean downloadJavaDoc) {
            this.project = project;
            this.fragment = fragment;
            this.artifact = artifact;
            this.downloadSources = downloadSources;
            this.downloadJavaDoc = downloadJavaDoc;
        }

        public int hashCode() {
            int hash = 17;
            hash = hash * 31 + project.hashCode();
            hash = hash * 31 + (fragment != null ? fragment.hashCode() : 0);
            hash = hash * 31 + (artifact != null ? artifact.hashCode() : 0);
            hash = hash * 31 + (downloadSources ? 1 : 0);
            hash = hash * 31 + (downloadJavaDoc ? 1 : 0);
            return hash;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof DownloadRequest)) {
                return false;
            }
            DownloadRequest other = (DownloadRequest) o;

            return project.equals(other.project)
                    && (fragment != null ? fragment.equals(other.fragment) : other.fragment == null)
                    && (artifact != null ? artifact.equals(other.artifact) : other.artifact == null)
                    && downloadSources == other.downloadSources && downloadJavaDoc == other.downloadJavaDoc;
        }
    }

    private static final long SCHEDULE_INTERVAL = 1000L;

    private final IMavenProjectRegistry registry;
    private final IMaven maven;
    private final BuildPathManager buildPathManager;

    private List<DownloadRequest> queue = new ArrayList<DownloadRequest>();

    @Inject
    public DownloadSourcesJob(IMavenProjectRegistry registry, IMaven maven, BuildPathManager buildPathManager) {
        super(JOB_NAME);
        this.registry = registry;
        this.maven = maven;
        this.buildPathManager = buildPathManager;
    }

    public IStatus run(IProgressMonitor monitor) {
        List<DownloadRequest> downloadRequests;

        synchronized (this.queue) {
            downloadRequests = new ArrayList<DownloadRequest>(this.queue);
            this.queue.clear();
        }

        List<IStatus> exceptions = new ArrayList<IStatus>();
        Set<IProject> mavenProjects = new LinkedHashSet<IProject>();

        for (DownloadRequest request : downloadRequests) {
            if (request.artifact != null) {

                try {
                    IMavenProjectFacade projectFacade = registry.create(request.project, monitor);

                    if (projectFacade != null) {
                        downloadMaven(projectFacade, request.artifact, request.downloadSources,
                                request.downloadJavaDoc, monitor);
                        mavenProjects.add(request.project);
                    }
                } catch (CoreException e) {
                    exceptions.add(e.getStatus());
                }
            }
        }

        if (!mavenProjects.isEmpty()) {
            ISchedulingRule schedulingRule = ResourcesPlugin.getWorkspace().getRuleFactory().buildRule();
                getJobManager().beginRule(schedulingRule, monitor);
            try {
                for (IProject mavenProject : mavenProjects) {
                    buildPathManager.updateClasspath(mavenProject, monitor);
                }

            } finally {
                getJobManager().endRule(schedulingRule);
            }
        }

        if (!exceptions.isEmpty()) {
            IStatus[] problems = exceptions.toArray(new IStatus[exceptions.size()]);
            return new MultiStatus(AndroidMavenPlugin.PLUGIN_ID, -1, problems, "Could not download sources or javadoc",
                    null);
        }

        return Status.OK_STATUS;
    }

    public boolean isEmpty() {
        synchronized (queue) {
            return queue.isEmpty();
        }
    }

    private void downloadMaven(IMavenProjectFacade projectFacade, ArtifactKey artifact, boolean downloadSources,
            boolean downloadJavadoc, IProgressMonitor monitor) throws CoreException {
        MavenProject mavenProject = projectFacade.getMavenProject(monitor);
        List<ArtifactRepository> repositories = mavenProject.getRemoteArtifactRepositories();

        if (artifact != null) {
            downloadAttachments(artifact, repositories, downloadSources, downloadJavadoc, monitor);
        } else {
            for (Artifact a : mavenProject.getArtifacts()) {
                ArtifactKey aKey = new ArtifactKey(a.getGroupId(), a.getArtifactId(), a.getBaseVersion(),
                        a.getClassifier());
                downloadAttachments(aKey, repositories, downloadSources, downloadJavadoc, monitor);
            }
        }
    }

    private File[] downloadAttachments(ArtifactKey artifact, List<ArtifactRepository> repositories,
            boolean downloadSources, boolean downloadJavadoc, IProgressMonitor monitor) throws CoreException {
        if (monitor != null && monitor.isCanceled()) {
            String message = "Downloading of sources/javadocs was canceled"; //$NON-NLS-1$
            synchronized (queue) {
                queue.clear();
            }
            throw new OperationCanceledException(message);
        }
        ArtifactKey[] attached = buildPathManager.getAttachedSourcesAndJavadoc(artifact, repositories, downloadSources,
                downloadJavadoc);
        File[] files = new File[2];

        if (attached[0] != null) {
            try {
                files[0] = download(attached[0], repositories, monitor);
            } catch (CoreException e) {
                warn("Could not download sources for " + artifact.toString());
            }
        }

        if (attached[1] != null) {
            try {
                files[1] = download(attached[1], repositories, monitor);
            } catch (CoreException e) {
                warn("Could not download sources for " + artifact.toString());
            }
        }

        return files;
    }

    private File download(ArtifactKey artifact, List<ArtifactRepository> repositories, IProgressMonitor monitor)
            throws CoreException {
        Artifact resolved = maven.resolve(artifact.getGroupId(), //
                artifact.getArtifactId(), //
                artifact.getVersion(), //
                "jar" /* type */, // //$NON-NLS-1$
                artifact.getClassifier(), //
                repositories, //
                monitor);
        return resolved.getFile();
    }

    private void scheduleDownload(IProject project, IPackageFragmentRoot fragment, ArtifactKey artifact,
            boolean downloadSources, boolean downloadJavadoc) {
        if (project == null || !project.isAccessible()) {
            return;
        }

        synchronized (this.queue) {
            queue.add(new DownloadRequest(project, fragment, artifact, downloadSources, downloadJavadoc));
        }

        schedule(SCHEDULE_INTERVAL);
    }

    public void scheduleDownload(IProject project, ArtifactKey artifact, boolean downloadSources,
            boolean downloadJavadoc) {
        scheduleDownload(project, null, artifact, downloadSources, downloadJavadoc);
    }

}
