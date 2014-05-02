package me.gladwell.eclipse.m2e.android.configuration.classpath;

import static me.gladwell.eclipse.m2e.android.Log.warn;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import me.gladwell.eclipse.m2e.android.AndroidMavenPlugin;
import me.gladwell.eclipse.m2e.android.configuration.ClasspathLoader;
import me.gladwell.eclipse.m2e.android.configuration.ClasspathPersister;
import me.gladwell.eclipse.m2e.android.configuration.NonRuntimeDependenciesClasspathContainer;
import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;
import me.gladwell.eclipse.m2e.android.configuration.PrunePlatformProvidedDependencies;
import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProjectFactory;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.m2e.core.embedder.ArtifactKey;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.embedder.IMavenConfiguration;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.jdt.IClasspathEntryDescriptor;
import org.eclipse.m2e.jdt.IClasspathManager;
import org.eclipse.m2e.jdt.MavenJdtPlugin;
import org.eclipse.m2e.jdt.internal.ClasspathEntryDescriptor;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Adapted from: http://git.eclipse.org/c/m2e/m2e-core.git/tree/org.eclipse.m2e.jdt/src/org/eclipse/m2e/jdt/internal/
 * BuildPathManager.java
 */
@Singleton
@SuppressWarnings("restriction")
public class BuildPathManager implements IResourceChangeListener {

    private static final String PROPERTY_SRC_ROOT = ".srcRoot"; //$NON-NLS-1$
    private static final String PROPERTY_SRC_PATH = ".srcPath"; //$NON-NLS-1$
    private static final String PROPERTY_JAVADOC_URL = ".javadoc"; //$NON-NLS-1$

    public static final String CLASSIFIER_JAVADOC = "javadoc";
    public static final String CLASSIFIER_TESTS = "tests";
    public static final String CLASSIFIER_TESTSOURCES = "test-sources";
    public static final String CLASSIFIER_SOURCES = "sources";

    @Inject private IMavenProjectRegistry registry;

    @Inject private ClasspathPersister persister;

    @Inject private File stateLocation;

    @Inject private IMaven maven;

    @Inject private DownloadSourcesJob downloadSourcesJob;

    @Inject @PrunePlatformProvidedDependencies private ClasspathLoader loader;

    @Inject private EclipseAndroidProjectFactory factory;

    @Inject private IMavenConfiguration mavenConfiguration;

    private void scheduleDownload(IProject project, MavenProject mavenProject, ArtifactKey artifact,
            boolean downloadSources, boolean downloadJavadoc) throws CoreException {
        ArtifactKey[] attached = getAttachedSourcesAndJavadoc(artifact, mavenProject.getRemoteArtifactRepositories(),
                downloadSources, downloadJavadoc);

        if (attached[0] != null || attached[1] != null) {
            downloadSourcesJob.scheduleDownload(project, artifact, downloadSources, downloadJavadoc);
        }
    }

    public void persistAttachedSourcesAndJavadoc(IJavaProject project, IClasspathContainer containerSuggestion,
            IProgressMonitor monitor) throws CoreException {
        // collect all source/javadoc attachement
        Properties props = new Properties();
        IClasspathEntry[] entries = containerSuggestion.getClasspathEntries();
        for (IClasspathEntry entry : entries) {
            if (IClasspathEntry.CPE_LIBRARY == entry.getEntryKind()) {
                String path = entry.getPath().toPortableString();
                if (entry.getSourceAttachmentPath() != null) {
                    props.put(path + PROPERTY_SRC_PATH, entry.getSourceAttachmentPath().toPortableString());
                }
                if (entry.getSourceAttachmentRootPath() != null) {
                    props.put(path + PROPERTY_SRC_ROOT, entry.getSourceAttachmentRootPath().toPortableString());
                }
                String javadocUrl = getJavadocLocation(entry);
                if (javadocUrl != null) {
                    props.put(path + PROPERTY_JAVADOC_URL, javadocUrl);
                }
            }
        }

        IClasspathEntry[] classpath = getClasspath(project, monitor);

        // eliminate all "standard" source/javadoc attachement we get from local repo
        for (IClasspathEntry entry : classpath) {
            if (IClasspathEntry.CPE_LIBRARY == entry.getEntryKind()) {

                ArtifactKey artifactKey = findArtifactByArtifactKey(entry);

                String path = entry.getPath().toPortableString();
                String value = (String) props.get(path + PROPERTY_SRC_PATH);
                if (value != null && entry.getSourceAttachmentPath() != null
                        && value.equals(getSourcePath(artifactKey).toPortableString())) {
                    props.remove(path + PROPERTY_SRC_PATH);
                }
                value = (String) props.get(path + PROPERTY_SRC_ROOT);
                if (value != null && entry.getSourceAttachmentRootPath() != null
                        && value.equals(entry.getSourceAttachmentRootPath().toPortableString())) {
                    props.remove(path + PROPERTY_SRC_ROOT);
                }
            }
        }

        // persist custom source/javadoc attachement info
        File file = getSourceAttachmentPropertiesFile(project.getProject());
        try {
            OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
            try {
                props.store(os, null);
            } finally {
                os.close();
            }
        } catch (IOException e) {
            throw new CoreException(new Status(IStatus.ERROR, MavenJdtPlugin.PLUGIN_ID, -1,
                    "Can't save classpath container changes", e));
        }

        persister.save(project.getProject(), Arrays.asList(entries));

        JavaCore.setClasspathContainer(containerSuggestion.getPath(), new IJavaProject[] { project },
                new IClasspathContainer[] { containerSuggestion }, monitor);
    }

    public void updateClasspath(IProject project, IProgressMonitor monitor) {
        IJavaProject javaProject = JavaCore.create(project);
        if (javaProject != null) {
            try {

                IClasspathEntry[] classpath = getClasspath(javaProject, monitor);

                persister.save(project, Arrays.asList(classpath));

                EclipseAndroidProject androidProject = factory.createAndroidProject(project);

                NonRuntimeDependenciesClasspathContainer container = new NonRuntimeDependenciesClasspathContainer(
                        loader, androidProject);

                JavaCore.setClasspathContainer(container.getPath(), new IJavaProject[] { javaProject },
                        new IClasspathContainer[] { container }, monitor);

            } catch (CoreException ex) {
                warn(ex.getMessage());
            }
        }
    }

    public IClasspathEntry[] getClasspath(IJavaProject project, IProgressMonitor monitor) {
        Properties props = new Properties();
        File file = getSourceAttachmentPropertiesFile(project.getProject());

        try {
            if (file.canRead()) {
                InputStream is = new BufferedInputStream(new FileInputStream(file));
                try {
                    props.load(is);
                } finally {
                    is.close();
                }
            }

            IClasspathContainer classpathContainer = getNonRuntimeDependenciesContainer(project);

            return configureAttachedSourcesAndJavadoc(project, props, classpathContainer, monitor);
        } catch (IOException e) {
            throw new ProjectConfigurationException("Can't save classpath container changes", e);
        } catch (CoreException e) {
            throw new ProjectConfigurationException("Can't save classpath container changes", e);
        }
    }

    public String getJavadocLocation(IClasspathEntry entry) {
        IClasspathAttribute[] attributes = entry.getExtraAttributes();
        for (int j = 0; j < attributes.length; j++) {
            IClasspathAttribute attribute = attributes[j];
            if (IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME.equals(attribute.getName())) {
                return attribute.getValue();
            }
        }
        return null;
    }

    public File getSourceAttachmentPropertiesFile(IProject project) {
        return new File(stateLocation, project.getName() + ".sources"); //$NON-NLS-1$
    }

    public File getContainerStateFile(IProject project) {
        return new File(stateLocation, project.getName()); //$NON-NLS-1$
    }

    private IClasspathContainer getNonRuntimeDependenciesContainer(IJavaProject project) {
        try {
            return JavaCore.getClasspathContainer(new Path(AndroidMavenPlugin.CONTAINER_NONRUNTIME_DEPENDENCIES),
                    project);
        } catch (JavaModelException e) {
            throw new ProjectConfigurationException(e.getMessage(), e);
        }
    }

    private IClasspathEntry[] configureAttachedSourcesAndJavadoc(IJavaProject project, Properties sourceAttachment,
            IClasspathContainer classpath, IProgressMonitor monitor) throws CoreException {

        IMavenProjectFacade facade = registry.getProject(project.getProject());
        
        if (facade == null) {
            return null;
        }
        
        IClasspathEntry[] entries = classpath.getClasspathEntries();
        IClasspathEntry[] configuredEntries = new IClasspathEntry[entries.length];

        int i = 0;
        for (IClasspathEntry entry : entries) {
            if (IClasspathEntry.CPE_LIBRARY == entry.getEntryKind() && entry.getSourceAttachmentPath() == null) {

                IClasspathEntryDescriptor descriptor = new ClasspathEntryDescriptor(entry);

                ArtifactKey artifactKey = findArtifactByArtifactKey(entry);

                String key = entry.getPath().toPortableString();

                IPath srcPath = entry.getSourceAttachmentPath();
                IPath srcRoot = entry.getSourceAttachmentRootPath();
                if (srcPath == null && sourceAttachment != null
                        && sourceAttachment.containsKey(key + PROPERTY_SRC_PATH)) {
                    srcPath = Path.fromPortableString((String) sourceAttachment.get(key + PROPERTY_SRC_PATH));
                    if (sourceAttachment.containsKey(key + PROPERTY_SRC_ROOT)) {
                        srcRoot = Path.fromPortableString((String) sourceAttachment.get(key + PROPERTY_SRC_ROOT));
                    }
                }
                if (srcPath == null && artifactKey != null) {
                    srcPath = getSourcePath(artifactKey);
                }

                // configure javadocs if available
                String javaDocUrl = descriptor.getJavadocUrl();
                if (javaDocUrl == null && sourceAttachment != null
                        && sourceAttachment.containsKey(key + PROPERTY_JAVADOC_URL)) {
                    javaDocUrl = (String) sourceAttachment.get(key + PROPERTY_JAVADOC_URL);
                }
                if (javaDocUrl == null && artifactKey != null) {
                    javaDocUrl = getJavaDocUrl(artifactKey);
                }

                descriptor.setSourceAttachment(srcPath, srcRoot);
                descriptor.setJavadocUrl(javaDocUrl);

                if (javaDocUrl == null || srcPath == null) {
                    scheduleDownload(project.getProject(), facade.getMavenProject(), artifactKey,
                            mavenConfiguration.isDownloadSources() && srcPath == null,
                            mavenConfiguration.isDownloadJavaDoc() && javaDocUrl == null);
                }

                configuredEntries[i] = descriptor.toClasspathEntry();
            } else {
                configuredEntries[i] = entry;
            }

            ++i;
        }

        return configuredEntries;
    }

    public static ArtifactKey findArtifactByArtifactKey(IClasspathEntry entry) {
        String groupId = null;
        String artifactId = null;
        String version = null;
        String classifier = null;
        for (IClasspathAttribute attribute : entry.getExtraAttributes()) {
            if (IClasspathManager.GROUP_ID_ATTRIBUTE.equals(attribute.getName())) {
                groupId = attribute.getValue();
            } else if (IClasspathManager.ARTIFACT_ID_ATTRIBUTE.equals(attribute.getName())) {
                artifactId = attribute.getValue();
            } else if (IClasspathManager.VERSION_ATTRIBUTE.equals(attribute.getName())) {
                version = attribute.getValue();
            } else if (IClasspathManager.CLASSIFIER_ATTRIBUTE.equals(attribute.getName())) {
                classifier = attribute.getValue();
            }
        }

        if (groupId != null && artifactId != null && version != null) {
            return new ArtifactKey(groupId, artifactId, version, classifier);
        }
        return null;
    }

    private File getAttachedArtifactFile(ArtifactKey a, String classifier) {
        // can't use Maven resolve methods since they mark artifacts as not-found even if they could be resolved
        // remotely
        try {

            ArtifactRepository localRepository = maven.getLocalRepository();
            String relPath = maven.getArtifactPath(localRepository, a.getGroupId(), a.getArtifactId(), a.getVersion(),
                    "jar", classifier); //$NON-NLS-1$
            File file = new File(localRepository.getBasedir(), relPath).getCanonicalFile();
            if (file.canRead()) {
                return file;
            }
        } catch (CoreException ex) {
            // fall through
        } catch (IOException ex) {
            // fall through
        }
        return null;
    }

    private String getJavaDocUrl(ArtifactKey artifactKey) {
        File file = getAttachedArtifactFile(artifactKey, CLASSIFIER_JAVADOC);

        try {
            if (file != null) {
                URL fileUrl = file.toURI().toURL();
                return "jar:" + fileUrl.toExternalForm() + "!/" + getJavaDocPathInArchive(file); //$NON-NLS-1$ //$NON-NLS-2$
            }
        } catch (MalformedURLException ex) {
            // fall through
        }

        return null;
    }

    private static String getJavaDocPathInArchive(File file) {
        ZipFile jarFile = null;
        try {
            jarFile = new ZipFile(file);
            String marker = "package-list"; //$NON-NLS-1$
            for (Enumeration<? extends ZipEntry> en = jarFile.entries(); en.hasMoreElements();) {
                ZipEntry entry = en.nextElement();
                String entryName = entry.getName();
                if (entryName.endsWith(marker)) {
                    return entry.getName().substring(0, entryName.length() - marker.length());
                }
            }
        } catch (IOException ex) {
            // ignore
        } finally {
            try {
                if (jarFile != null)
                    jarFile.close();
            } catch (IOException ex) {
                //
            }
        }

        return ""; //$NON-NLS-1$
    }

    private IPath getSourcePath(ArtifactKey a) {
        File file = getAttachedArtifactFile(a, getSourcesClassifier(a.getClassifier()));

        if (file != null) {
            return Path.fromOSString(file.getAbsolutePath());
        }

        return null;
    }

    public static String getSourcesClassifier(String baseClassifier) {
        return CLASSIFIER_TESTS.equals(baseClassifier) ? CLASSIFIER_TESTSOURCES : CLASSIFIER_SOURCES;
    }

    /* package */ArtifactKey[] getAttachedSourcesAndJavadoc(ArtifactKey a, List<ArtifactRepository> repositories,
            boolean downloadSources, boolean downloadJavaDoc) throws CoreException {
        ArtifactKey sourcesArtifact = new ArtifactKey(a.getGroupId(), a.getArtifactId(), a.getVersion(),
                BuildPathManager.getSourcesClassifier(a.getClassifier()));
        ArtifactKey javadocArtifact = new ArtifactKey(a.getGroupId(), a.getArtifactId(), a.getVersion(),
                BuildPathManager.CLASSIFIER_JAVADOC);

        if (repositories != null) {
            downloadSources = downloadSources && !isUnavailable(sourcesArtifact, repositories);
            downloadJavaDoc = downloadJavaDoc && !isUnavailable(javadocArtifact, repositories);
        }

        ArtifactKey[] result = new ArtifactKey[2];

        if (downloadSources) {
            result[0] = sourcesArtifact;
        }

        if (downloadJavaDoc) {
            result[1] = javadocArtifact;
        }

        return result;
    }

    private boolean isUnavailable(ArtifactKey a, List<ArtifactRepository> repositories) throws CoreException {
        return maven.isUnavailable(a.getGroupId(), a.getArtifactId(), a.getVersion(),
                "jar" /* type */, a.getClassifier(), repositories); //$NON-NLS-1$
    }

    public void resourceChanged(IResourceChangeEvent event) {
        int type = event.getType();
        if (IResourceChangeEvent.PRE_DELETE == type) {
            // remove custom source and javadoc configuration
            File attachmentProperties = getSourceAttachmentPropertiesFile((IProject) event.getResource());
            if (attachmentProperties.exists() && !attachmentProperties.delete()) {
                warn("Can't delete " + attachmentProperties.getAbsolutePath()); //$NON-NLS-1$
            }

            // remove classpath container state
            File containerState = getContainerStateFile((IProject) event.getResource());
            if (containerState.exists() && !containerState.delete()) {
                warn("Can't delete " + containerState.getAbsolutePath()); //$NON-NLS-1$
            }
        }
    }

}
