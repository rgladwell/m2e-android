/*******************************************************************************
 * Copyright (c) 2014 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration.classpath;

import static me.gladwell.eclipse.m2e.android.AndroidMavenPlugin.ANDROID_GEN_FOLDER;
import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

public class IgnoreOptionalWarningsConfigurer implements ClasspathConfigurer {

    public boolean shouldApplyTo(MavenAndroidProject project) {
        return project.isIgnoreOptionalWarningsInGenFolder();
    }

    public void configure(MavenAndroidProject mavenProject, EclipseAndroidProject eclipseProject) {
        eclipseProject.getClasspath().getSourceEntry(ANDROID_GEN_FOLDER).ignoreOptionalWarnings();
    }

}
