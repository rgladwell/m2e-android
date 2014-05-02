/*******************************************************************************
 * Copyright (c) 2013 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration.classpath;

import java.util.List;

import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IClasspathEntryDescriptor;
import org.eclipse.m2e.jdt.IClasspathDescriptor.EntryFilter;

public class RemoveNonRuntimeDependenciesConfigurer implements RawClasspathConfigurer {

    public void configure(MavenAndroidProject mavenProject, EclipseAndroidProject eclipseProject,
            IClasspathDescriptor classpath) {
        final List<String> nonRuntimeDependencies = mavenProject.getNonRuntimeDependencies();

        classpath.removeEntry(new EntryFilter() {
            public boolean accept(IClasspathEntryDescriptor descriptor) {
                return nonRuntimeDependencies.contains(descriptor.getPath().toOSString());
            }
        });
    }

}
