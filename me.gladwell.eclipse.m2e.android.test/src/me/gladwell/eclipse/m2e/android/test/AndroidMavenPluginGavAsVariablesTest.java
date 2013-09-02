/*******************************************************************************
 * Copyright (c) 2011 Mykola Nikishov
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import org.eclipse.core.resources.IProject;

public class AndroidMavenPluginGavAsVariablesTest extends AndroidMavenPluginTestCase {

    private static final String PROJECT_NAME = "issue-114";

    private IProject project;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        project = importAndroidProject(PROJECT_NAME);
    }

    public void testConfigure() throws Exception {
        assertNoErrors(project);
    }


}
