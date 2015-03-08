/*******************************************************************************
 * Copyright (c) 2013, 2014 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration.classpath;

import static java.io.File.separator;
import me.gladwell.eclipse.m2e.android.project.SourceEntry;
import me.gladwell.eclipse.m2e.android.project.IDEAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

public class ModifySourceFolderOutputClasspathConfigurer implements ClasspathConfigurer {

    private static final String ANDROID_CLASSES_FOLDER = "bin" + separator + "classes";

    public boolean shouldApplyTo(MavenAndroidProject project) {
        return true;
    }

    public void configure(MavenAndroidProject mavenProject, IDEAndroidProject eclipseProject) {
        for (SourceEntry entry : eclipseProject.getClasspath().getSourceEntries()) {
            if (mavenProject.getSourcePaths().contains(entry.getPath())
                    && !entry.getOutputLocation().endsWith(ANDROID_CLASSES_FOLDER)) {
                entry.setOutputLocation(ANDROID_CLASSES_FOLDER);
            }
        }
    }

}
