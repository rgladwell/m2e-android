/*******************************************************************************
 * Copyright (c) 2013 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration.classpath;

import static java.io.File.separator;

import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.eclipse.jdt.core.IClasspathEntry;

public class ModifySourceFolderOutputClasspathConfigurer implements ClasspathConfigurer {

    private static final String ANDROID_CLASSES_FOLDER = "bin" + separator + "classes";

    public boolean shouldApplyTo(MavenAndroidProject project) {
        return true;
    }

    public void configure(Project project) {
        for(IClasspathEntry entry : project.getClasspath().getEntries()) {
            if(entry.getOutputLocation() != null && entry.getEntryKind() == IClasspathEntry.CPE_SOURCE
                    && !entry.getOutputLocation().equals(project.getJavaProject().getPath().append(ANDROID_CLASSES_FOLDER))) {
                project.getClasspath().removeEntry(entry.getPath());
                project.getClasspath().addSourceEntry(entry.getPath(), project.getJavaProject().getPath().append(ANDROID_CLASSES_FOLDER), true);
            }
        }
    }

}
