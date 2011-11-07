package me.gladwell.eclipse.m2e.android.test;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com.android.ide.eclipse.adt.AdtConstants;

public class AndroidMavenPluginTest extends AndroidMavenPluginTestCase {

	private static final String SIMPLE_PROJECT_NAME = "simple-project";
	static final String ISSUE_6_PROJECT_NAME = "issue-6";

	public void testConfigureNonAndroidProject() throws Exception {
		deleteProject(SIMPLE_PROJECT_NAME);
		IProject project = importAndroidProject(SIMPLE_PROJECT_NAME);

	    assertFalse("configurer added android nature", project.hasNature(AdtConstants.NATURE_DEFAULT));
		IJavaProject javaProject = JavaCore.create(project);
		assertFalse("output location set to android value for non-android project", javaProject.getOutputLocation().toString().equals("/"+SIMPLE_PROJECT_NAME+"/target/android-classes"));

		for(IClasspathEntry entry : javaProject.getRawClasspath()) {
			assertFalse("classpath contains reference to gen directory", entry.getPath().toOSString().contains("gen"));
		}
	}

	public void testConfigureAddsWorkspaceProjectDepsToClasspath() throws Exception {
		importAndroidProject(SIMPLE_PROJECT_NAME);
		IProject project = importAndroidProject("test-project-workspace-deps");
		assertClasspathContains(JavaCore.create(project), SIMPLE_PROJECT_NAME);
	}
}
