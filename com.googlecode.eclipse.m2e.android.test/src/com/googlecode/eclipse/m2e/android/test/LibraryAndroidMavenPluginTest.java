package com.googlecode.eclipse.m2e.android.test;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com.android.ide.eclipse.adt.AndroidConstants;
import com.android.ide.eclipse.adt.internal.sdk.Sdk;

public class LibraryAndroidMavenPluginTest extends AndroidMavenPluginTestCase {

    private static final String ANDROID_LIB_PROJECT_NAME = "apklib-project";

    private IProject project;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		deleteProject(ANDROID_LIB_PROJECT_NAME);
		project = importProject("projects/"+ANDROID_LIB_PROJECT_NAME+"/pom.xml");
		waitForJobsToComplete();
	}

	public void testConfigure() throws Exception {
		assertNoErrors(project);
		assertTrue(Sdk.getProjectState(project).isLibrary());
	}

	public void testConfigureAddsAndroidNature() throws Exception {
	    assertTrue("configurer failed to add android nature", project.hasNature(AndroidConstants.NATURE_DEFAULT));
	}

	public void testConfigureSetsAndroidOutputLocation() throws Exception {
		IJavaProject javaProject = JavaCore.create(project);
		assertEquals("failed to set output location", javaProject.getOutputLocation().toString(), "/"+ANDROID_LIB_PROJECT_NAME+"/target/android-classes");
	}

	public void testConfigureRemovesApkBuilder() throws Exception {
		assertFalse("project contains redundant APKBuilder build command", containsApkBuildCommand(project));
	}

	public void testConfigureDoesNotAddTargetDirectoryToClasspath() throws Exception {
		IJavaProject javaProject = JavaCore.create(project);
		for(IClasspathEntry entry : javaProject.getRawClasspath()) {
			assertFalse("classpath contains reference to target directory: cause infinite build loops and build conflicts", entry.getPath().toOSString().contains("target"));
		}
	}

}
