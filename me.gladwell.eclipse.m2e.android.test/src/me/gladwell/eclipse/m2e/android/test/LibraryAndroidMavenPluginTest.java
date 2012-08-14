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

import java.io.File;

import junit.framework.Assert;

import me.gladwell.eclipse.m2e.android.AndroidMavenPlugin;
import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class LibraryAndroidMavenPluginTest extends AndroidMavenPluginTestCase {

    private static final String ANDROID_LIB_PROJECT_NAME = "android-library";
    private static final String TEST_PROJECT_WITH_APKLIB_DEPS = "test-project-apklib-deps";

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		deleteProject(ANDROID_LIB_PROJECT_NAME);
	}

    @Override
    protected void tearDown() throws Exception {
        try {
            deleteProject(ANDROID_LIB_PROJECT_NAME);
        } catch(Throwable t) {

        } finally {
            try {
                super.tearDown();
            } catch(Throwable t) {
                t.printStackTrace();
            }
        }
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

		try {
		    deleteProject(TEST_PROJECT_WITH_APKLIB_DEPS);
		} catch(Throwable t) {
            t.printStackTrace();
		}
	}

	public void testConfigureAddsErrorForNonExistentLibraryProject() throws Exception {
		IProject project = importAndroidProject(TEST_PROJECT_WITH_APKLIB_DEPS);

		assertErrorMarker(project, AndroidMavenPlugin.APKLIB_ERROR_TYPE);

        try {
            deleteProject(TEST_PROJECT_WITH_APKLIB_DEPS);
        } catch(Throwable t) {
            t.printStackTrace();
        }
	}

	public void testConfigureWithClosedProject() throws Exception {
		IProject closedProject = importAndroidProject("simple-project");
		closedProject.close(monitor);

		try{
			IProject project = importAndroidProject(TEST_PROJECT_WITH_APKLIB_DEPS);
		} catch (CoreException ex){
			if (ex.getCause() instanceof ProjectConfigurationException){
				fail("Access denied: M2E is trying to access a closed project");
			}
		} finally {
		    try {
		        deleteProject("simple-project");
            } catch(Throwable t) {
                t.printStackTrace();
            }
            try {
                deleteProject(TEST_PROJECT_WITH_APKLIB_DEPS);
            } catch(Throwable t) {
                t.printStackTrace();
            }
		}
	}

	public void testConfigureAddsWorkspaceLibraryProjectWithDifferentArtifactId() throws Exception {
		IProject libraryProject = importAndroidProject(ANDROID_LIB_PROJECT_NAME);
		IProject project = importAndroidProject("test-project-apklib-deps-diff-artifact-id");

		assertTrue(getProjectState(project).getFullLibraryProjects().contains(libraryProject));

		try {
		    deleteProject("test-project-apklib-deps-diff-artifact-id");
		} catch(Throwable t) {
		    t.printStackTrace();
		}
	}

	public void testConfigureClearsOldErrors() throws Exception {
		IProject project = importAndroidProject(TEST_PROJECT_WITH_APKLIB_DEPS);
		importAndroidProject(ANDROID_LIB_PROJECT_NAME);
		getProjectConfigurationManager().updateProjectConfiguration(project, monitor);

		assertNoErrors(project);

        try {
            deleteProject(TEST_PROJECT_WITH_APKLIB_DEPS);
        } catch(Throwable t) {
            t.printStackTrace();
        }
	}

    public void testConfigureAddsWorkspaceLibraryInSubfdoler() throws Exception {
        File subfolder = new File(workspace.getRoot().getLocation().toFile(), "subfolder");
        IProject libraryProject = importAndroidProject(ANDROID_LIB_PROJECT_NAME, subfolder);
        IProject project = importAndroidProject(TEST_PROJECT_WITH_APKLIB_DEPS);

        assertTrue(getProjectState(project).getFullLibraryProjects().contains(libraryProject));

        try {
            deleteProject(TEST_PROJECT_WITH_APKLIB_DEPS);
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }

    public void testConfigureAddsWorkspaceLibraryOutsideWorkspaceFolder() throws Exception {
        File temp = createTempFolder();
        IProject libraryProject = importAndroidProject(ANDROID_LIB_PROJECT_NAME, temp);
        IProject project = importAndroidProject(TEST_PROJECT_WITH_APKLIB_DEPS);

        assertTrue(getProjectState(project).getFullLibraryProjects().contains(libraryProject));

        try {
            deleteProject(TEST_PROJECT_WITH_APKLIB_DEPS);
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }

}
