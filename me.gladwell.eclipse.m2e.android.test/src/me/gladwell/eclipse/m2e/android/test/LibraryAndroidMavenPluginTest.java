package me.gladwell.eclipse.m2e.android.test;

import static com.android.ide.eclipse.adt.internal.sdk.Sdk.getProjectState;

import org.eclipse.core.resources.IProject;

public class LibraryAndroidMavenPluginTest extends AndroidMavenPluginTestCase {

    private static final String ANDROID_LIB_PROJECT_NAME = "apklib-project";

    private IProject libraryProject;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		deleteProject(ANDROID_LIB_PROJECT_NAME);
		libraryProject = importAndroidProject(ANDROID_LIB_PROJECT_NAME);
	}

	public void testConfigure() throws Exception {
		assertNoErrors(libraryProject);
	}

	public void testConfigureAppliesLibraryState() throws Exception {
		assertTrue(getProjectState(libraryProject).isLibrary());
	}

	public void testConfigureAddsWorkspaceLibraryProjectToProjectProperties() throws Exception {
		IProject project = importAndroidProject("test-project-apklib-deps");
		
		assertTrue(getProjectState(project).getFullLibraryProjects().contains(libraryProject));
	}

}
