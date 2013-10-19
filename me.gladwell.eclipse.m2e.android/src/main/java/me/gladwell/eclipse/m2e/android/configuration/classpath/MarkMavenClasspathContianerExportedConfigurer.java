/*******************************************************************************
 * Copyright (c) 2013 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration.classpath;

import static me.gladwell.eclipse.m2e.android.configuration.Classpaths.findContainerMatching;

import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.jdt.IClasspathManager;

public class MarkMavenClasspathContianerExportedConfigurer implements ClasspathConfigurer {

    public boolean shouldApplyTo(MavenAndroidProject project) {
        return !project.isLibrary();
    }

    public void configure(Project project) {
        IClasspathEntry oldEntry = findContainerMatching(project.getClasspath(), IClasspathManager.CONTAINER_ID);
        IClasspathEntry newEntry = JavaCore.newContainerEntry(oldEntry.getPath(), true);
        project.getClasspath().removeEntry(oldEntry.getPath());
        project.getClasspath().addEntry(newEntry);
    }

}
