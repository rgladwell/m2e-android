package me.gladwell.eclipse.m2e.android.test;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.m2e.jdt.IClasspathManager;

import static me.gladwell.eclipse.m2e.android.AndroidMavenPlugin.CONTAINER_NONRUNTIME_DEPENDENCIES;

@SuppressWarnings("restriction")
public class ProvidedWorkspaceProjectTest extends AndroidMavenPluginTestCase {

    public void testConfigureRemovesProjectFromMavenDependencies() throws Exception {
        IJavaProject dependency = JavaCore.create(importAndroidProject("fake-commons-lang"));
        IJavaProject app = JavaCore.create(importAndroidProject("android-application"));

        assertFalse(classpathContainerContains(app, IClasspathManager.CONTAINER_ID, dependency.getPath().toOSString()));
    }

    public void testConfigureAddsProjectToNonRuntimeDependencies() throws Exception {
        IJavaProject dependency = JavaCore.create(importAndroidProject("fake-commons-lang"));
        IJavaProject app = JavaCore.create(importAndroidProject("android-application"));

        IClasspathEntry entry = getClasspathEntry(app, CONTAINER_NONRUNTIME_DEPENDENCIES, dependency.getPath().toOSString());

        assertTrue(entry.getEntryKind() == IClasspathEntry.CPE_PROJECT);
    }

    public void testConfigureAddsProvidedJarIfDependencyClosed() throws Exception {
        IJavaProject dependency = JavaCore.create(importAndroidProject("fake-commons-lang"));
        IJavaProject app = JavaCore.create(importAndroidProject("android-application"));
        dependency.getProject().close(null);
        waitForJobsToComplete();

        IClasspathEntry entry = getClasspathEntry(app, CONTAINER_NONRUNTIME_DEPENDENCIES, dependency.getPath().toOSString());

        assertTrue(entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY);
    }

    public void testConfigureAddsProvidedJarIfDependencyResolutionDisabled() throws Exception {
        IJavaProject dependency = JavaCore.create(importAndroidProject("fake-commons-lang"));
        ResolverConfiguration config = new ResolverConfiguration();
        config.setResolveWorkspaceProjects(false);
        IJavaProject app = JavaCore.create(importAndroidProject("android-application", config));

        IClasspathEntry entry = getClasspathEntry(app, CONTAINER_NONRUNTIME_DEPENDENCIES, dependency.getPath().toOSString());

        assertTrue(entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY);
    }

}
