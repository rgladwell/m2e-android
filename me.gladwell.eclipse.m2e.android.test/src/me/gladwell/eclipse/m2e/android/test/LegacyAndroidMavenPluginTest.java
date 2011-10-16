package me.gladwell.eclipse.m2e.android.test;

import org.eclipse.core.resources.IProject;

public class LegacyAndroidMavenPluginTest extends AndroidMavenPluginTestCase {

	private static final String LEGACY_ANDROID_PROJECT_NAME = "legacy-project";
	private IProject project;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		project = importAndroidProject(LEGACY_ANDROID_PROJECT_NAME);
	}

	public void testConfigure() throws Exception {
		assertNoErrors(project);
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject(LEGACY_ANDROID_PROJECT_NAME);
		project = null;

		super.tearDown();
	}

}
