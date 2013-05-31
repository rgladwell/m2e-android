/*******************************************************************************
 * Copyright (c) 2013, 2014 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration.classpath;

import static me.gladwell.eclipse.m2e.android.AndroidMavenPlugin.ANDROID_GEN_FOLDER;

import java.io.File;

import me.gladwell.eclipse.m2e.android.AndroidMavenPlugin;
import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;
import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

public class AddGenFolderClasspathConfigurer implements ClasspathConfigurer {

    public boolean shouldApplyTo(MavenAndroidProject project) {
        return true;
    }

    public void configure(MavenAndroidProject mavenProject, EclipseAndroidProject eclipseProject) {
        IFolder gen = eclipseProject.getProject().getFolder(ANDROID_GEN_FOLDER + File.separator);
        if (!gen.exists()) {
            try {
                gen.create(true, true, new NullProgressMonitor());
            } catch (CoreException e) {
                throw new ProjectConfigurationException(e);
            }
        }

        eclipseProject.getClasspath().addSourceEntry(AndroidMavenPlugin.ANDROID_GEN_FOLDER);
    }

}
