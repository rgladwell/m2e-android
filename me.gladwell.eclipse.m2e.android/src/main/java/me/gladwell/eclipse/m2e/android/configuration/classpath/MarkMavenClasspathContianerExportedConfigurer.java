/*******************************************************************************
 * Copyright (c) 2013, 2014 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration.classpath;

import me.gladwell.eclipse.m2e.android.project.IDEAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.eclipse.m2e.jdt.IClasspathManager;

public class MarkMavenClasspathContianerExportedConfigurer implements ClasspathConfigurer {

    public boolean shouldApplyTo(MavenAndroidProject project) {
        return !project.isLibrary();
    }

    public void configure(MavenAndroidProject mavenProject, IDEAndroidProject eclipseProject) {
        eclipseProject.getClasspath().markExported(IClasspathManager.CONTAINER_ID);
    }

}
