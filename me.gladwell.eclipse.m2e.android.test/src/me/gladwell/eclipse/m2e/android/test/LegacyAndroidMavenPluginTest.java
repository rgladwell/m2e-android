/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import org.eclipse.core.resources.IProject;

@SuppressWarnings("restriction")
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

}
