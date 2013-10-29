/*******************************************************************************
 * Copyright (c) 2013 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import static com.google.common.collect.Iterables.toArray;

import java.io.FileNotFoundException;

import me.gladwell.eclipse.m2e.android.AndroidMavenPlugin;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;

public class NonRuntimeDependenciesClasspathContainer implements IClasspathContainer {

    private final ClasspathLoader loader;
    private final IJavaProject project;

    public NonRuntimeDependenciesClasspathContainer(ClasspathLoader loader, IJavaProject project) {
        super();
        this.loader = loader;
        this.project = project;
    }

    public IClasspathEntry[] getClasspathEntries() {
        try {
            final Iterable<IClasspathEntry> nonRuntimeDependencies = loader.load(project);
            return toArray(nonRuntimeDependencies, IClasspathEntry.class);
        } catch (FileNotFoundException e) {
            return new IClasspathEntry[0];
        }
    }

    public String getDescription() {
        return "Non-Runtime Maven Dependencies";
    }

    public int getKind() {
        return IClasspathContainer.K_APPLICATION;
    }

    public IPath getPath() {
        return new Path(AndroidMavenPlugin.CONTAINER_NONRUNTIME_DEPENDENCIES);
    }

}
