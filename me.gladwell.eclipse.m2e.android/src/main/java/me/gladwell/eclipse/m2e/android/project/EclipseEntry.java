/*******************************************************************************
 * Copyright (c) 2015 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import static me.gladwell.eclipse.m2e.android.configuration.Classpaths.findContainerMatching;
import static org.eclipse.jdt.core.JavaCore.newContainerEntry;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.m2e.jdt.IClasspathDescriptor;

public class EclipseEntry implements Entry {

    private final IClasspathDescriptor classpath;
    private final String path;

    public EclipseEntry(IClasspathDescriptor classpath, String path) {
        super();
        this.classpath = classpath;
        this.path = path;
    }

    public void markNotExported() {
        IClasspathEntry oldEntry = findContainerMatching(classpath, path);
        if(oldEntry != null) {
            IClasspathEntry newEntry = newContainerEntry(oldEntry.getPath(), false);
            classpath.removeEntry(oldEntry.getPath());
            classpath.addEntry(newEntry);
        } else {
            // TODO log warning here
        }
    }
    
    public boolean isPresent() {
       return findContainerMatching(classpath, path) != null;
    }

    public boolean isExported() {
        IClasspathEntry entry = findContainerMatching(classpath, path);
        if (entry != null) {
            return entry.isExported();
        }
        return false;
    }

}
