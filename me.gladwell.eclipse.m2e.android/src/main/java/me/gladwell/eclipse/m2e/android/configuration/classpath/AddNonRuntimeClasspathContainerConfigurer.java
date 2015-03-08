/*******************************************************************************
 * Copyright (c) 2013, 2014 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration.classpath;

import org.eclipse.jdt.core.IClasspathContainer;

import com.google.inject.Inject;

import me.gladwell.eclipse.m2e.android.configuration.Caching;
import me.gladwell.eclipse.m2e.android.configuration.ClasspathLoader;
import me.gladwell.eclipse.m2e.android.configuration.NonRuntimeDependenciesClasspathContainer;
import me.gladwell.eclipse.m2e.android.project.IDEAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

public class AddNonRuntimeClasspathContainerConfigurer implements ClasspathConfigurer {

    final private ClasspathLoader loader;

    @Inject
    public AddNonRuntimeClasspathContainerConfigurer(@Caching ClasspathLoader loader) {
        super();
        this.loader = loader;
    }

    public boolean shouldApplyTo(MavenAndroidProject project) {
        return true;
    }

    public void configure(MavenAndroidProject mavenProject, IDEAndroidProject eclipseProject) {
        final IClasspathContainer nonRuntimeContainer = new NonRuntimeDependenciesClasspathContainer(loader,
                eclipseProject);
        eclipseProject.getClasspath().addContainer(nonRuntimeContainer);
    }

}
