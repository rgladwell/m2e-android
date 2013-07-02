package me.gladwell.eclipse.m2e.android.configuration.classpath;

import static me.gladwell.eclipse.m2e.android.configuration.Classpaths.findContainerMatching;

import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;

import com.android.ide.eclipse.adt.AdtConstants;

/*******************************************************************************
 * Copyright (c) 2013 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

public class MarkAndroidClasspathContainerNotExportedConfigurer implements ClasspathConfigurer {

    public boolean shouldApplyTo(MavenAndroidProject project) {
        return true;
    }

    public void configure(Project project) {
        IClasspathEntry oldEntry = findContainerMatching(project.getClasspath(), AdtConstants.CONTAINER_PRIVATE_LIBRARIES);
        if(oldEntry != null) {
            IClasspathEntry newEntry = JavaCore.newContainerEntry(oldEntry.getPath(), false);
            project.getClasspath().removeEntry(oldEntry.getPath());
            project.getClasspath().addEntry(newEntry);
        } else {
            // TODO log warning here
        }
    }

}
