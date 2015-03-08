/*******************************************************************************
 * Copyright (c) 2013, 2014 Ricardo Gladwell, Csaba Kozák
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration.classpath;

import static com.google.common.collect.Lists.transform;

import java.util.List;

import me.gladwell.eclipse.m2e.android.project.Dependency;
import me.gladwell.eclipse.m2e.android.project.IDEAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IClasspathEntryDescriptor;
import org.eclipse.m2e.jdt.IClasspathDescriptor.EntryFilter;

import com.google.common.base.Function;

public class RemoveNonRuntimeDependenciesConfigurer implements RawClasspathConfigurer {

    public void configure(MavenAndroidProject mavenProject, IDEAndroidProject eclipseProject, IClasspathDescriptor classpath) {
        final List<Dependency> nonRuntimeDependencies = mavenProject.getNonRuntimeDependencies();

        final List<String> nonRuntimeDependencyPaths = transform(nonRuntimeDependencies, new Function<Dependency, String>() {
            public String apply(Dependency mavenDependency) {
               return mavenDependency.getPath();
            }
        });

        classpath.removeEntry(new EntryFilter() {
            public boolean accept(IClasspathEntryDescriptor descriptor) {
                return nonRuntimeDependencyPaths.contains(descriptor.getPath().toOSString());
            }
        });
    }

}
