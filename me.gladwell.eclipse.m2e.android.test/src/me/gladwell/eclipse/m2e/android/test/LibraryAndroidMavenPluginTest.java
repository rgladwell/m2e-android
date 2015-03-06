/*******************************************************************************
 * Copyright (c) 2012, 2013 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import static org.eclipse.m2e.core.MavenPlugin.getProjectConfigurationManager;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.jdt.IClasspathManager;

@SuppressWarnings("restriction")
public class LibraryAndroidMavenPluginTest extends AndroidMavenPluginTestCase {

    private static final String APKLIB_ERROR_TYPE = "me.gladwell.eclipse.m2e.android.markers.dependency.apklib";
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
        TestAndroidProject libraryProject = new TestAndroidProject(importAndroidProject(ANDROID_LIB_PROJECT_NAME));
        assertTrue(libraryProject.isLibrary());
    }

    public void testConfigureAddsWorkspaceLibraryProjectToProjectProperties() throws Exception {
        IProject libraryProject = importAndroidProject(ANDROID_LIB_PROJECT_NAME);
        TestAndroidProject project = new TestAndroidProject(importAndroidProject(TEST_PROJECT_WITH_APKLIB_DEPS));

        assertTrue(project.libraries().contains(libraryProject));
    }

    public void testConfigureAddsErrorForNonExistentLibraryProject() throws Exception {
        IProject project = importAndroidProject(TEST_PROJECT_WITH_APKLIB_DEPS);

        assertErrorMarker(project, APKLIB_ERROR_TYPE);
    }

    public void testConfigureWithClosedProject() throws Exception {
        IProject closedProject = importAndroidProject("simple-project");
        closedProject.close(monitor);

        try {
            importAndroidProject(TEST_PROJECT_WITH_APKLIB_DEPS);
        } catch (CoreException ex) {
            fail("Access denied: M2E is trying to access a closed project");
        }
    }

    public void testConfigureAddsWorkspaceLibraryProjectWithDifferentArtifactId() throws Exception {
        IProject libraryProject = importAndroidProject(ANDROID_LIB_PROJECT_NAME);
        TestAndroidProject project = new TestAndroidProject(importAndroidProject("test-project-apklib-deps-diff-artifact-id"));

        assertTrue(project.libraries().contains(libraryProject));
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
        TestAndroidProject project = new TestAndroidProject(importAndroidProject(TEST_PROJECT_WITH_APKLIB_DEPS));

        assertTrue(project.libraries().contains(libraryProject));
    }

    public void testConfigureAddsWorkspaceLibraryOutsideWorkspaceFolder() throws Exception {
        File temp = createTempFolder();
        IProject libraryProject = importAndroidProject(ANDROID_LIB_PROJECT_NAME, temp);
        TestAndroidProject project = new TestAndroidProject(importAndroidProject(TEST_PROJECT_WITH_APKLIB_DEPS));

        assertTrue(project.libraries().contains(libraryProject));
    }

    public void testConfigureDoesNotMarkMavenContainerExported() throws Exception {
        IProject libraryProject = importAndroidProject(ANDROID_LIB_PROJECT_NAME);
        IJavaProject javaProject = JavaCore.create(libraryProject);
        IClasspathEntry mavenContainer = getClasspathContainer(javaProject, IClasspathManager.CONTAINER_ID);
        assertTrue(!mavenContainer.isExported());
    }

}
