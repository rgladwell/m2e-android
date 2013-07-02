/*******************************************************************************
 * Copyright (c) 2013 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration.classpath;

import static me.gladwell.eclipse.m2e.android.configuration.Classpaths.findContainerContaining;

import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;

public class RemoveJREClasspathContainerConfigurer implements ClasspathConfigurer {

    public boolean shouldApplyTo(MavenAndroidProject project) {
        return true;
    }

    public void configure(Project project) {
        IClasspathEntry entry = findContainerContaining(project.getClasspath(), JavaRuntime.JRE_CONTAINER);
        project.getClasspath().removeEntry(entry.getPath());
    }

}
