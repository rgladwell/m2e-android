package com.byluroid.eclipse.maven.android.test;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.maven.ide.eclipse.core.IMavenConstants;
import org.maven.ide.eclipse.project.IProjectConfigurationManager;
import org.maven.ide.eclipse.project.MavenProjectManager;
import org.maven.ide.eclipse.project.ResolverConfiguration;
import org.maven.ide.eclipse.tests.common.AbstractMavenProjectTestCase;

import com.android.ide.eclipse.adt.AndroidConstants;

public class AndroidMavenPluginTest extends AbstractMavenProjectTestCase {

	private static final String ANDROID_11_PROJECT_NAME = "apidemos-11-app";
	private static final String ANDROID_15_PROJECT_NAME = "apidemos-15-app";

	@Override
    protected void setUp() throws Exception {
	    super.setUp();
    }

	@SuppressWarnings("restriction")
	public void testCleanBuildAndroid11() throws Exception {
		deleteProject(ANDROID_11_PROJECT_NAME);
		IProject project = createExisting(ANDROID_11_PROJECT_NAME, "projects/" + ANDROID_11_PROJECT_NAME);
		waitForJobsToComplete();

		assertTrue(project.hasNature(IMavenConstants.NATURE_ID));

		IProjectConfigurationManager projectConfigurationManager = plugin.getProjectConfigurationManager();
		MavenProjectManager projectManager = plugin.getMavenProjectManager();

		ResolverConfiguration resolverConfiguration = projectManager.getResolverConfiguration(project);

		projectConfigurationManager.updateProjectConfiguration(project, resolverConfiguration, null, monitor);
		waitForJobsToComplete();

		assertTrue(project.hasNature(AndroidConstants.NATURE));

		project.build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);
		waitForJobsToComplete();
	}

	@SuppressWarnings("restriction")
	public void testCleanBuildAndroid15() throws Exception {
		deleteProject(ANDROID_15_PROJECT_NAME);
		IProject project = createExisting(ANDROID_15_PROJECT_NAME, "projects/" + ANDROID_15_PROJECT_NAME);
		waitForJobsToComplete();

		assertTrue(project.hasNature(IMavenConstants.NATURE_ID));

		IProjectConfigurationManager projectConfigurationManager = plugin.getProjectConfigurationManager();
		MavenProjectManager projectManager = plugin.getMavenProjectManager();

		ResolverConfiguration resolverConfiguration = projectManager.getResolverConfiguration(project);

		projectConfigurationManager.updateProjectConfiguration(project, resolverConfiguration, null, monitor);
		waitForJobsToComplete();
		assertTrue(project.hasNature(AndroidConstants.NATURE));

		project.build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);
		waitForJobsToComplete();
	}

}
