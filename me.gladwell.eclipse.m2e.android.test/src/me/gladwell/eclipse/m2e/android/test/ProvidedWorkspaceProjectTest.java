package me.gladwell.eclipse.m2e.android.test;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.jdt.IClasspathManager;

import static java.io.File.separator;
import static me.gladwell.eclipse.m2e.android.AndroidMavenPlugin.CONTAINER_NONRUNTIME_DEPENDENCIES;

@SuppressWarnings("restriction")
public class ProvidedWorkspaceProjectTest extends AndroidMavenPluginTestCase {

    private IJavaProject app;
    private IJavaProject test;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        IProject[] projects = importAndroidProjects("projects" + separator + "issue-204", new String[] {
                "test" + separator + "pom.xml", "test-it" + separator + "pom.xml" });

        app = JavaCore.create(projects[0]);
        test = JavaCore.create(projects[1]);
    }

    public void testConfigureRemovesProjectFromMavenDependencies() throws Exception {
        assertFalse(classpathContainerContains(test, IClasspathManager.CONTAINER_ID, app.getPath().toOSString()));
    }

    public void testConfigureAddsProjectToNonRuntimeDependencies() throws Exception {
        IClasspathEntry entry = getClasspathEntry(test, CONTAINER_NONRUNTIME_DEPENDENCIES, app.getPath().toOSString());
        
        assertTrue(entry.getEntryKind() == IClasspathEntry.CPE_PROJECT);
    }

    public void testConfigureAddsProvidedJarIfDependencyClosed() throws Exception {
        app.getProject().close(null);
        waitForJobsToComplete();
        
        IClasspathEntry entry = getClasspathEntry(test, CONTAINER_NONRUNTIME_DEPENDENCIES, app.getPath().toOSString());
        
        assertTrue(entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY);
    }

}
