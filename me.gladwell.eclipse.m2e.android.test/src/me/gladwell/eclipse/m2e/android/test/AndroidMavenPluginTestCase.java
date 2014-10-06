/*******************************************************************************
 * Copyright (c) 2009-2014 Ricardo Gladwell and Hugo Josefson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import static java.io.File.separator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.gladwell.eclipse.m2e.android.AndroidMavenPlugin;
import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.MavenModelManager;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectImportResult;
import org.eclipse.m2e.core.project.MavenProjectInfo;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;
import org.eclipse.m2e.tests.common.JobHelpers;
import org.eclipse.m2e.tests.common.WorkspaceHelpers;
import org.eclipse.m2e.tests.common.JobHelpers.IJobMatcher;

import com.android.ide.eclipse.adt.internal.sdk.Sdk;

@SuppressWarnings("restriction")
public abstract class AndroidMavenPluginTestCase extends AbstractMavenProjectTestCase {

    static final int MAXIMUM_SECONDS_TO_LOAD_ADT = 120;

    protected AndroidMavenPlugin plugin;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        plugin = AndroidMavenPlugin.getDefault();
        plugin.getInjector().injectMembers(this);

        waitForAdtToLoad();
    }

    protected void waitForAdtToLoad() throws InterruptedException, Exception {
        try {
            JobHelpers.waitForJobs(new IJobMatcher() {
                public boolean matches(Job job) {
                    return job.getClass().getName().contains(Sdk.class.getName());
                }

            }, MAXIMUM_SECONDS_TO_LOAD_ADT * 1000);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * @deprecated
     * Method replaced by the fluent {@link ProjectImporter#importAndroidTestProject(String)}
     * project builder.
     */
    @Deprecated
    protected IProject importAndroidProject(String name) throws Exception {
        IProject project = importProject("projects" + separator + name + separator + "pom.xml");
        waitForJobsToComplete();
        waitForAdtToLoad();
        return project;
    }

    protected IProject importAndroidProject(String name, File folder) throws Exception {
        return importProject(name, folder, new ResolverConfiguration());
    }

    protected IProject importAndroidProject(String name, ResolverConfiguration configuration) throws Exception {
        IProject project = importProject("projects" + separator + name + separator + "pom.xml", configuration);
        waitForJobsToComplete();
        waitForAdtToLoad();
        return project;
    }

    private IProject importProject(String name, File parent, ResolverConfiguration configuration) throws Exception {
        MavenModelManager mavenModelManager = MavenPlugin.getMavenModelManager();
        String pomLocation = "projects" + separator + name + separator + "pom.xml";

        File pomFile = new File(pomLocation);
        File src = new File(pomFile.getParentFile().getCanonicalPath());
        File dst = new File(parent, pomFile.getParentFile().getName());

        copyDir(src, dst);

        String pomName = pomFile.getName();
        pomFile = new File(dst, pomName);
        Model model = mavenModelManager.readMavenModel(pomFile);
        MavenProjectInfo projectInfo = new MavenProjectInfo(pomName, pomFile, model, null);
        File workspaceRoot = workspace.getRoot().getLocation().toFile();
        File basedir = projectInfo.getPomFile().getParentFile().getCanonicalFile();

        projectInfo.setBasedirRename(basedir.getParentFile().equals(workspaceRoot) ? MavenProjectInfo.RENAME_REQUIRED
                : MavenProjectInfo.RENAME_NO);

        final ArrayList<MavenProjectInfo> projectInfos = new ArrayList<MavenProjectInfo>();
        projectInfos.add(projectInfo);

        final ProjectImportConfiguration importConfiguration = new ProjectImportConfiguration(configuration);

        final ArrayList<IMavenProjectImportResult> importResults = new ArrayList<IMavenProjectImportResult>();

        workspace.run(new IWorkspaceRunnable() {
            public void run(IProgressMonitor monitor) throws CoreException {
                importResults.addAll(MavenPlugin.getProjectConfigurationManager().importProjects(projectInfos,
                        importConfiguration, monitor));
            }
        }, MavenPlugin.getProjectConfigurationManager().getRule(), IWorkspace.AVOID_UPDATE, monitor);

        IProject[] projects = new IProject[projectInfos.size()];
        for (int i = 0; i < projectInfos.size(); i++) {
            IMavenProjectImportResult importResult = importResults.get(i);
            assertSame(projectInfos.get(i), importResult.getMavenProjectInfo());
            projects[i] = importResult.getProject();
            assertNotNull("Failed to import project " + projectInfos, projects[i]);

            Model model1 = projectInfos.get(0).getModel();
            IMavenProjectFacade facade = MavenPlugin.getMavenProjectRegistry().create(projects[i], monitor);
            if (facade == null) {
                fail("Project " + model1.getGroupId() + "-" + model1.getArtifactId() + "-" + model1.getVersion()
                        + " was not imported. Errors: "
                        + WorkspaceHelpers.toString(WorkspaceHelpers.findErrorMarkers(projects[i])));
            }
        }

        return projects[0];
    }

    public IProject[] importAndroidProjects(String basedir, String[] pomNames) throws Exception {
        IProject[] projects = importProjects(basedir, pomNames, new ResolverConfiguration());
        waitForJobsToComplete();
        waitForAdtToLoad();
        return projects;
    }

    protected void buildAndroidProject(IProject project, int kind) throws CoreException, InterruptedException {
        ResourcesPlugin.getWorkspace().build(project.getBuildConfigs(), kind, true, monitor);
        project.build(kind, "org.eclipse.jdt.core.javabuilder", null, monitor);
    
        waitForJobsToComplete();
    }

    protected void assertClasspathContains(IJavaProject javaProject, String path) throws JavaModelException {
        for (IClasspathEntry entry : javaProject.getRawClasspath()) {
            if (entry.getPath().toOSString().contains(path)) {
                return;
            } else if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
                IClasspathContainer container = JavaCore.getClasspathContainer(entry.getPath(), javaProject);
                for (IClasspathEntry e : container.getClasspathEntries()) {
                    if (e.getPath().toOSString().contains(path)) {
                        return;
                    }
                }
            }
        }
        fail(path + " should be in classpath");
    }

    protected void assertClasspathDoesNotContain(IJavaProject javaProject, String path) throws JavaModelException {
        for (IClasspathEntry entry : javaProject.getRawClasspath()) {
            assertFalse(entry.getPath().toOSString().contains(path));
            if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
                IClasspathContainer container = JavaCore.getClasspathContainer(entry.getPath(), javaProject);
                for (IClasspathEntry e : container.getClasspathEntries()) {
                    assertFalse(path + " should not be in classpath", e.getPath().toOSString().contains(path));
                }
            }
        }
    }

    protected void assertClasspathDoesNotContainExported(IJavaProject javaProject, String path)
            throws JavaModelException {
        for (IClasspathEntry entry : javaProject.getRawClasspath()) {
            assertFalse(entry.getPath().toOSString().contains(path));
            if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER && entry.isExported()) {
                IClasspathContainer container = JavaCore.getClasspathContainer(entry.getPath(), javaProject);
                for (IClasspathEntry e : container.getClasspathEntries()) {
                    assertFalse(path + " should not be in classpath", e.getPath().toOSString().contains(path));
                }
            }
        }
    }

    protected IClasspathEntry getClasspathContainer(IJavaProject javaProject, String id) throws JavaModelException {
        IClasspathEntry entry = findClasspathContainer(javaProject, id);
        if (entry == null)
            throw new RuntimeException("classpath container=[" + id + "] not found");
        return entry;
    }

    private IClasspathEntry findClasspathContainer(IJavaProject javaProject, String id) throws JavaModelException {
        for (IClasspathEntry entry : javaProject.getRawClasspath()) {
            if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
                if (entry.getPath().toOSString().equals(id)) {
                    return entry;
                }
            }
        }
        return null;
    }

    protected boolean hasClasspathContainer(IJavaProject javaProject, String id) throws JavaModelException {
        return findClasspathContainer(javaProject, id) != null;
    }

    protected boolean classpathContainerContains(IJavaProject project, String id, String path)
            throws JavaModelException {
        try {
            return getClasspathEntry(project, id, path) != null;
        } catch (ProjectConfigurationException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    protected IClasspathEntry getClasspathEntry(IJavaProject project, String id, String path) throws JavaModelException {
        IClasspathContainer container = JavaCore.getClasspathContainer(new Path(id), project);
        
        for (IClasspathEntry entry : container.getClasspathEntries()) {
            if (entry.getPath().toOSString().contains(path)) {
                return entry;
            }
        }
        throw new ProjectConfigurationException("ClasspathEntry [" + path + "] nt found in container [" + id + "]");
    }

    protected void assertErrorMarker(IProject project, String type) throws CoreException {
        List<IMarker> markers = findMarkers(project, IMarker.SEVERITY_ERROR);
        for (IMarker marker : markers) {
            if (type.equals(marker.getType())) {
                assertTrue("Marker type " + type + " is not a subtype of " + IMarker.PROBLEM,
                        marker.isSubtypeOf(IMarker.PROBLEM));
                return;
            }
        }

        fail("Marker not found. Found markers:" + toString(markers));
    }

    protected File createTempFolder() throws IOException {
        File temp = File.createTempFile("temp", "");
        temp.delete();
        temp.mkdirs();
        return temp;
    }

    static boolean booleanAttribute(String attributeName, IClasspathEntry entry) {
        for (IClasspathAttribute attribute : entry.getExtraAttributes()) {
            if (attribute.getName().equals(attributeName) && attribute.getValue().equals("true")) {
                return true;
            }
        }
        return false;
    }

}
