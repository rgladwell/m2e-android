/*******************************************************************************
 * Copyright (c) 2012, 2013, 2014 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import java.io.File;

import org.eclipse.core.resources.IProject;

import com.android.ide.eclipse.adt.AdtConstants;

@SuppressWarnings("restriction")
public class MultiModulePluginTest extends AndroidMavenPluginTestCase {

    private static final String PARENT_PROJECT_NAME = "android-multi-module";
    private static final String CHILD_PROJECT_NAME = "android-child";
    private static final String CHILD_LIBRARY_PROJECT_NAME = "android-child-library";

    private IProject parentProject;
    private IProject childProject;
    private IProject childLibraryProject;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        parentProject = importAndroidProject(PARENT_PROJECT_NAME);
        childLibraryProject = importAndroidProject(PARENT_PROJECT_NAME + File.separator + CHILD_LIBRARY_PROJECT_NAME);
        childProject = importAndroidProject(PARENT_PROJECT_NAME + File.separator + CHILD_PROJECT_NAME);
    }

    public void testConfigure() throws Exception {
        assertNoErrors(parentProject);
        assertNoErrors(childLibraryProject);
        assertNoErrors(childProject);
    }

    public void testConfigureAddsAndroidNature() throws Exception {
        assertTrue("failed to add android nature to child module", childProject.hasNature(AdtConstants.NATURE_DEFAULT));
    }

    public void testConfigureDoesNotAddAndroidNatureToParentProject() throws Exception {
        assertFalse("added android nature to parent", parentProject.hasNature(AdtConstants.NATURE_DEFAULT));
    }

}
