package com.googlecode.eclipse.m2e.android.test;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com.android.ide.eclipse.adt.AdtConstants;
import com.googlecode.eclipse.m2e.android.AndroidMavenPluginUtil;

public class AndroidMavenPluginTest extends AndroidMavenPluginTestCase {

	private static final int MAX_AUTO_BUILD_LOOPS = 3;
	private static final String SIMPLE_PROJECT_NAME = "simple-project";
	static final String ISSUE_6_PROJECT_NAME = "issue-6";

	public void testConfigureNonAndroidProject() throws Exception {
		deleteProject(SIMPLE_PROJECT_NAME);
		IProject project = importProject("projects/"+SIMPLE_PROJECT_NAME+"/pom.xml");
		waitForJobsToComplete();

	    assertFalse("configurer added android nature", project.hasNature(AdtConstants.NATURE_DEFAULT));
		IJavaProject javaProject = JavaCore.create(project);
		assertFalse("output location set to android value for non-android project", javaProject.getOutputLocation().toString().equals("/"+SIMPLE_PROJECT_NAME+"/target/android-classes"));

		for(IClasspathEntry entry : javaProject.getRawClasspath()) {
			assertFalse("classpath contains reference to gen directory", entry.getPath().toOSString().contains("gen"));
		}
	}

	/**
	 * Test to ensure that AndroidMavenBuildParticipant isn't applied to non-android projects.
	 * @throws Exception
	 * @see <a href="https://code.google.com/a/eclipselabs.org/p/m2eclipse-android-integration/issues/detail?id=40">Issue 40</a>
	 */
	public void testBuildNonAndroidProject() throws Exception {
		deleteProject(SIMPLE_PROJECT_NAME);
		IProject project = importProject("projects/"+SIMPLE_PROJECT_NAME+"/pom.xml");
		waitForJobsToComplete();
		File file = new File(project.getLocation().toFile(), "/target/simple-project-1.0-SNAPSHOT.jar");		

		buildAndroidProject(project, IncrementalProjectBuilder.CLEAN_BUILD);
		buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);
		waitForJobsToComplete();
		
		assertFalse("Android builder building non-android projects", file.exists());
	}

	/**
	 * @see http://code.google.com/p/m2eclipse-android-integration/issues/detail?id=6
	 */
	public void testInfiniteBuildWithAndroid7() throws Exception {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
	    IWorkspaceDescription description = workspace.getDescription();
	    description.setAutoBuilding(true);
	    workspace.setDescription(description);

		deleteProject(ISSUE_6_PROJECT_NAME);
		IProject project = importProject("projects/"+ISSUE_6_PROJECT_NAME+"/pom.xml");
		waitForJobsToComplete();

		buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

		long first = AndroidMavenPluginUtil.getApkFile(project).lastModified();
		long second = first + 1;
		int builds = 0;

		project.refreshLocal(IProject.DEPTH_INFINITE, androidMavenMonitor);

		while(first < second) {
			builds++;
			first = second;
			Thread.sleep(2000);
			second = AndroidMavenPluginUtil.getApkFile(project).lastModified();

			assertTrue("auto build looping more than 3 times", builds <= MAX_AUTO_BUILD_LOOPS);
		}
	}

}
