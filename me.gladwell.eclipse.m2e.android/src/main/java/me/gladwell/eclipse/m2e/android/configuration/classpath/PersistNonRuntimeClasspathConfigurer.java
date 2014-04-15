/*******************************************************************************
 * Copyright (c) 2013, 2014 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration.classpath;

import java.util.ArrayList;
import java.util.List;

import me.gladwell.eclipse.m2e.android.configuration.ClasspathPersister;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IClasspathEntryDescriptor;

import com.google.inject.Inject;

public class PersistNonRuntimeClasspathConfigurer implements RawClasspathConfigurer {

    private final ClasspathPersister persister;

    @Inject
    public PersistNonRuntimeClasspathConfigurer(ClasspathPersister persister) {
        super();
        this.persister = persister;
    }

    public void configure(MavenAndroidProject project, IClasspathDescriptor classpath) {
        final List<String> nonRuntimeDependencies = project.getNonRuntimeDependencies();
        final List<IClasspathEntry> nonRuntimeDependenciesEntries = new ArrayList<IClasspathEntry>();
        for (IClasspathEntryDescriptor descriptor : classpath.getEntryDescriptors()) {
            if (nonRuntimeDependencies.contains(descriptor.getPath().toOSString())) {
                nonRuntimeDependenciesEntries.add(descriptor.toClasspathEntry());
            }
        }

        persister.save(project.getName(), nonRuntimeDependenciesEntries);
    }

}
