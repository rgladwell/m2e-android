/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import static com.android.ide.eclipse.adt.internal.sdk.Sdk.getProjectState;
import static org.eclipse.m2e.core.MavenPlugin.getProjectConfigurationManager;
import junit.framework.Assert;

import me.gladwell.eclipse.m2e.android.AndroidMavenPlugin;
import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

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

	public void testConfigureAddsErrorForNonExistentLibraryProject() throws Exception {
		deleteProject(ANDROID_LIB_PROJECT_NAME);
		IProject project = importAndroidProject("test-project-apklib-deps");

		assertErrorMarker(project, AndroidMavenPlugin.APKLIB_ERROR_TYPE);
	}

	public void testConfigureWithAClosedProjectInTheWorkspace() throws Exception {
		
		IProject javaProject = importAndroidProject("closed-java-project");
		IJavaProject closedJavaProject = JavaCore.create(javaProject);
		closedJavaProject.close();
		javaProject.close(null);
		try{
			IProject project = importAndroidProject("test-project-apklib-deps");
			
		} catch (CoreException ex){
			if (ex.getCause() instanceof ProjectConfigurationException){
				Assert.fail("Access denied: M2E is trying to access a closed project");
			}
		}
	}

	public void testConfigureAddsWorkspaceLibraryProjectWithDifferentArtifactId() throws Exception {
		IProject project = importAndroidProject("test-project-apklib-deps-diff-artifact-id");

		assertTrue(getProjectState(project).getFullLibraryProjects().contains(libraryProject));
	}

	public void testConfigureClearsOldErrors() throws Exception {
		deleteProject(ANDROID_LIB_PROJECT_NAME);
		IProject project = importAndroidProject("test-project-apklib-deps");
		importAndroidProject(ANDROID_LIB_PROJECT_NAME);
		getProjectConfigurationManager().updateProjectConfiguration(project, monitor);

		assertNoErrors(project);
	}

}
