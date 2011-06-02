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

}
