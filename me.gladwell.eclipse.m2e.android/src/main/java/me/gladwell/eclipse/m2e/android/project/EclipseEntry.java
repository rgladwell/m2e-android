/*******************************************************************************
 * Copyright (c) 2015 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import static me.gladwell.eclipse.m2e.android.configuration.Classpaths.findContainerMatching;

import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IClasspathEntryDescriptor;

public class EclipseEntry implements Entry {

    private final IClasspathDescriptor classpath;
    private final String path;

    public EclipseEntry(IClasspathDescriptor classpath, String path) {
        super();
        this.classpath = classpath;
        this.path = path;
    }

    public void markNotExported() {
        IClasspathEntryDescriptor entry = findContainerMatching(classpath, path);
        if(entry != null) {
            entry.setExported(false);
        } else {
            // TODO log warning here
        }
    }

}
