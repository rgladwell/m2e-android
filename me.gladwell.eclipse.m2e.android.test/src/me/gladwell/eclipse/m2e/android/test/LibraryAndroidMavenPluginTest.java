/*******************************************************************************
 * Copyright (c) 2012, 2013 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import static com.android.ide.eclipse.adt.internal.sdk.Sdk.getProjectState;
import static org.eclipse.m2e.core.MavenPlugin.getProjectConfigurationManager;

import java.io.File;

import me.gladwell.eclipse.m2e.android.AndroidMavenPlugin;
import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.jdt.IClasspathManager;

@SuppressWarnings("restriction")
public class LibraryAndroidMavenPluginTest extends AndroidMavenPluginTestCase {

    private static final String ANDROID_LIB_PROJECT_NAME = "android-library";
    private static final String TEST_PROJECT_WITH_APKLIB_DEPS = "test-project-apklib-deps";

	@Override
	protected void setUp() throws Exception {
		super.setUp();

	}

    public void testConfigure() throws Exception {
		IProject libraryProject = importAndroidProject(ANDROID_LIB_PROJECT_NAME);
		assertNoErrors(libraryProject);
	}

	public void testConfigureAppliesLibraryState() throws Exception {
		IProject libraryProject = importAndroidProject(ANDROID_LIB_PROJECT_NAME);
		assertTrue(getProjectState(libraryProject).isLibrary());
	}

	public void testConfigureAddsWorkspaceLibraryProjectToProjectProperties() throws Exception {
		IProject libraryProject = importAndroidProject(ANDROID_LIB_PROJECT_NAME);
		IProject project = importAndroidProject(TEST_PROJECT_WITH_APKLIB_DEPS);

		assertTrue(getProjectState(project).getFullLibraryProjects().contains(libraryProject));
	}

	public void testConfigureAddsErrorForNonExistentLibraryProject() throws Exception {
		IProject project = importAndroidProject(TEST_PROJECT_WITH_APKLIB_DEPS);

		assertErrorMarker(project, AndroidMavenPlugin.APKLIB_ERROR_TYPE);
	}

	public void testConfigureWithClosedProject() throws Exception {
		IProject closedProject = importAndroidProject("simple-project");
		closedProject.close(monitor);

		try{
			importAndroidProject(TEST_PROJECT_WITH_APKLIB_DEPS);
		} catch (CoreException ex){
			if (ex.getCause() instanceof ProjectConfigurationException){
				fail("Access denied: M2E is trying to access a closed project");
			}
		}
	}

	public void testConfigureAddsWorkspaceLibraryProjectWithDifferentArtifactId() throws Exception {
		IProject libraryProject = importAndroidProject(ANDROID_LIB_PROJECT_NAME);
		IProject project = importAndroidProject("test-project-apklib-deps-diff-artifact-id");

		assertTrue(getProjectState(project).getFullLibraryProjects().contains(libraryProject));
	}

	public void testConfigureClearsOldErrors() throws Exception {
		IProject project = importAndroidProject(TEST_PROJECT_WITH_APKLIB_DEPS);
		importAndroidProject(ANDROID_LIB_PROJECT_NAME);
		getProjectConfigurationManager().updateProjectConfiguration(project, monitor);

		assertNoErrors(project);
	}

    public void testConfigureAddsWorkspaceLibraryInSubfdoler() throws Exception {
        File subfolder = new File(workspace.getRoot().getLocation().toFile(), "subfolder");
        IProject libraryProject = importAndroidProject(ANDROID_LIB_PROJECT_NAME, subfolder);
        IProject project = importAndroidProject(TEST_PROJECT_WITH_APKLIB_DEPS);

        assertTrue(getProjectState(project).getFullLibraryProjects().contains(libraryProject));
    }

    public void testConfigureAddsWorkspaceLibraryOutsideWorkspaceFolder() throws Exception {
        File temp = createTempFolder();
        IProject libraryProject = importAndroidProject(ANDROID_LIB_PROJECT_NAME, temp);
        IProject project = importAndroidProject(TEST_PROJECT_WITH_APKLIB_DEPS);

        assertTrue(getProjectState(project).getFullLibraryProjects().contains(libraryProject));
    }

    public void testConfigureDoesNotMarkMavenContainerExported() throws Exception {
        IProject libraryProject = importAndroidProject(ANDROID_LIB_PROJECT_NAME);
        IJavaProject javaProject = JavaCore.create(libraryProject);
        IClasspathEntry mavenContainer = getClasspathContainer(javaProject, IClasspathManager.CONTAINER_ID);
        assertTrue(!mavenContainer.isExported());
    }

}
