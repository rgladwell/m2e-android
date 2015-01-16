/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration.workspace;

import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

public class AddAndroidNatureWorkspaceConfigurer implements WorkspaceConfigurer {

    public boolean isConfigured(EclipseAndroidProject eclipseProject, MavenAndroidProject mavenProject) {
        return eclipseProject.isAndroidProject();
    }

    public boolean isValid(MavenAndroidProject project) {
        return true;
    }

    public void configure(EclipseAndroidProject eclipseProject, MavenAndroidProject mavenProject) {
        eclipseProject.setAndroidProject(mavenProject.isAndroidProject());
    }

}
