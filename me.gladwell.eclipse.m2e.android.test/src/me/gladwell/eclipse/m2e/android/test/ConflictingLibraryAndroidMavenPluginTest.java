/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import static com.android.ide.eclipse.adt.internal.sdk.Sdk.getProjectState;

import java.util.Arrays;

import org.eclipse.core.resources.IProject;


public class ConflictingLibraryAndroidMavenPluginTest extends AndroidMavenPluginTestCase {

	private static final String ANDROID_LIB1_PROJECT_NAME = "com.example.android.tictactoe.apklib-project";
	private static final String ANDROID_LIB1_PROJECT_LOCATION = "apklib-project";
	private static final String ANDROID_LIB2_PROJECT_NAME = "com.example.android.tictactoe2.apklib-project";
	private static final String ANDROID_LIB2_PROJECT_LOCATION = "apklib-project-2";

    private IProject firstLibraryProject;
    private IProject secondLibraryProject;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		deleteProject(ANDROID_LIB1_PROJECT_NAME);
		deleteProject(ANDROID_LIB2_PROJECT_NAME);
		firstLibraryProject = importAndroidProject(ANDROID_LIB1_PROJECT_NAME, ANDROID_LIB1_PROJECT_LOCATION);
		secondLibraryProject = importAndroidProject(ANDROID_LIB2_PROJECT_NAME, ANDROID_LIB2_PROJECT_LOCATION);
	}

	public void testConfigure() throws Exception {
		assertNoErrors(firstLibraryProject);
		assertNoErrors(secondLibraryProject);
	}

	public void testConfigureAppliesLibraryState() throws Exception {
		assertTrue(getProjectState(firstLibraryProject).isLibrary());
		assertTrue(getProjectState(secondLibraryProject).isLibrary());
	}

	public void testConfigureAddsWorkspaceLibraryProjectsToProjectProperties() throws Exception {
		IProject project = importAndroidProject("test-project-apklib-deps-conflicts");

		assertTrue(getProjectState(project).getFullLibraryProjects().containsAll(
				Arrays.asList(firstLibraryProject, secondLibraryProject)));
	}

}
