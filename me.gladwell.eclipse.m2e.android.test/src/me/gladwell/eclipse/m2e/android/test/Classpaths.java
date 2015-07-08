/*******************************************************************************
 * Copyright (c) 2013 - 2015 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import static java.util.Arrays.asList;
import static org.eclipse.jdt.core.IClasspathEntry.CPE_SOURCE;

import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.IPath;
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

    public final static Comparator<IClasspathEntry> bySourceFolderOrdering = new Comparator<IClasspathEntry>() {

        private final List<String> order = asList(new String[]{
                "src/main/java",
                "src/main/resources",
                "gen",
                "src/test/java",
                "src/test/resources"
        });

        @Override
        public int compare(IClasspathEntry left, IClasspathEntry right) {
            IPath lsegment = left.getPath().removeFirstSegments(1);
            IPath rsegment = right.getPath().removeFirstSegments(1);

            int li = order.indexOf(lsegment.toString());
            int ri = order.indexOf(rsegment.toString());

            int result;

            if(li < 0 || ri < 0) {
                result = left.getEntryKind() - right.getEntryKind();
            } else {
                result = li - ri;
            }

            return result;
        }

    };
}
