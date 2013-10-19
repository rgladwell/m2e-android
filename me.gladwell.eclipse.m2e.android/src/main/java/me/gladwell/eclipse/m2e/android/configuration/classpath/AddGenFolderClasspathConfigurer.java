/*******************************************************************************
 * Copyright (c) 2013 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration.classpath;

import java.io.File;

import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

public class AddGenFolderClasspathConfigurer implements ClasspathConfigurer {

    private static final String ANDROID_GEN_FOLDER = "gen";

    public boolean shouldApplyTo(MavenAndroidProject project) {
        return true;
    }

    public void configure(Project session) {
        IFolder gen = session.getJavaProject().getProject().getFolder(ANDROID_GEN_FOLDER + File.separator);
        if (!gen.exists()) {
            try {
                gen.create(true, true, new NullProgressMonitor());
            } catch (CoreException e) {
                throw new ProjectConfigurationException(e);
            }
        }

        if (!session.getClasspath().containsPath(new Path(ANDROID_GEN_FOLDER))) {
            session.getClasspath().addSourceEntry(gen.getFullPath(), null, false);
        }
    }

}
