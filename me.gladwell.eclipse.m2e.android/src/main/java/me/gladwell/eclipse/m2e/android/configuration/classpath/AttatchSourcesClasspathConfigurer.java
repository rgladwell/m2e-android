/*******************************************************************************
 * Copyright (c) 2014 Csaba Kozák
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration.classpath;

import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.jdt.IClasspathDescriptor;

import com.google.inject.Inject;

public class AttatchSourcesClasspathConfigurer implements RawClasspathConfigurer {

    private final BuildPathManager manager;

    @Inject
    public AttatchSourcesClasspathConfigurer(BuildPathManager manager) {
        this.manager = manager;
    }

    public void configure(MavenAndroidProject project, EclipseAndroidProject eclipseProject,
            IClasspathDescriptor classpath) {

        manager.updateClasspath(eclipseProject.getProject(), new NullProgressMonitor());
    }

}
