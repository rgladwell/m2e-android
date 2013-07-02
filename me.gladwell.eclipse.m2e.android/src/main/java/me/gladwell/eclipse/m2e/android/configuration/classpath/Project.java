/*******************************************************************************
 * Copyright (c) 2013 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration.classpath;

import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.apache.maven.execution.MavenSession;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.m2e.jdt.IClasspathDescriptor;

public class Project {
    private final IClasspathDescriptor classpath;
    private final IJavaProject javaProject;
    private final MavenAndroidProject androidProject;

    public Project(IClasspathDescriptor classpath, IJavaProject javaProject, MavenAndroidProject androidProject) {
        super();
        this.classpath = classpath;
        this.javaProject = javaProject;
        this.androidProject = androidProject;
    }

    public IClasspathDescriptor getClasspath() {
        return classpath;
    }

    public IJavaProject getJavaProject() {
        return javaProject;
    }

    public MavenAndroidProject getAndroidProject() {
        return androidProject;
    }

}
