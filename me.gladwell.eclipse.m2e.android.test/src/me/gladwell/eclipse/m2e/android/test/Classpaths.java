/*******************************************************************************
 * Copyright (c) 2013 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import static org.eclipse.jdt.core.IClasspathEntry.CPE_SOURCE;

import org.eclipse.jdt.core.IClasspathEntry;

public class Classpaths {

    private Classpaths() {
    }

    public static IClasspathEntry findEntry(IClasspathEntry[] classpath, final String path) {
        for(IClasspathEntry entry : classpath) {
            if(entry.getPath().toOSString().endsWith(path)) {
                return entry;
            }
        }
        return null;
    }

    public static IClasspathEntry findSourceEntry(IClasspathEntry[] classpath, final String path) {
        for(IClasspathEntry entry : classpath) {
            if(entry.getEntryKind() == CPE_SOURCE && entry.getPath().toOSString().endsWith(path)) {
                return entry;
            }
        }
        return null;
    }

}
