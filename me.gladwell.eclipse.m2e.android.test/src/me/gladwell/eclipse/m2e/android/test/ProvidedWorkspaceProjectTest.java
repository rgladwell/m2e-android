package me.gladwell.eclipse.m2e.android.test;

import me.gladwell.eclipse.m2e.android.AndroidMavenPlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.m2e.jdt.IClasspathManager;

import static java.io.File.separator;

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

    public void testConfigureRemovesProjectFromMavenDependencies() throws JavaModelException {
        assertFalse(classpathContainerContains(test, IClasspathManager.CONTAINER_ID, app.getPath().toOSString()));
    }

    public void testConfigureAddsProjectToNonRuntimeDependencies() throws JavaModelException {
        IClasspathEntry entry = getClasspathEntry(test, AndroidMavenPlugin.CONTAINER_NONRUNTIME_DEPENDENCIES,
                app.getPath().toOSString());
        
        assertNotNull(entry);
        assertTrue(entry.getEntryKind() == IClasspathEntry.CPE_PROJECT);
    }
    
    public void testConfigureAddsProvidedJarIfDependencyClosed() throws CoreException, InterruptedException {
        app.getProject().close(null);
        waitForJobsToComplete();
        
        IClasspathEntry entry = getClasspathEntry(test, AndroidMavenPlugin.CONTAINER_NONRUNTIME_DEPENDENCIES,
                app.getPath().toOSString());
        
        assertNotNull(entry);
        assertTrue(entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY);
    }
}
