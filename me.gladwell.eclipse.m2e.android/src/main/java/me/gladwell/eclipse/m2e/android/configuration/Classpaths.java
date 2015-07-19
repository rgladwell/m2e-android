/*******************************************************************************
 * Copyright (c) 2013, 2015 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import static com.google.common.base.Predicates.and;

import java.util.Comparator;
import java.util.List;

import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IClasspathEntryDescriptor;

import com.google.common.base.Predicate;

public class Classpaths {

    private Classpaths() {
    }

    public static Comparator<IClasspathEntry> bySourceFolderOrdering(final MavenAndroidProject project) {

        return new Comparator<IClasspathEntry>() {

            public int compare(IClasspathEntry left, IClasspathEntry right) {
                SourceType ltype = SourceType.forPathIn(project, left);
                SourceType rtype = SourceType.forPathIn(project, right);

                int result;

                if(ltype.priority < 0 || rtype.priority < 0) {
                    result = left.getEntryKind() - right.getEntryKind();
                } else {
                    result = ltype.priority - rtype.priority;
                }

                System.out.println("comparing " + left.getPath() + "(" + ltype + ") to " + right.getPath()+ "(" + rtype + ") result = " + result);
                return result;
            }
        };
    }

    public static IClasspathEntryDescriptor findContainerMatching(final IClasspathDescriptor classpath, final String path) {
        return matchContainer(classpath, new Predicate<IClasspathEntryDescriptor>() {
            public boolean apply(IClasspathEntryDescriptor entry) {
                return entry.getPath().toOSString().equals(path);
            }
        });
    }

    public static IClasspathEntryDescriptor findContainerContaining(final IClasspathDescriptor classpath, final String fragment) {
        return matchContainer(classpath, new Predicate<IClasspathEntryDescriptor>() {
            public boolean apply(IClasspathEntryDescriptor entry) {
                return entry.getPath().toOSString().contains(fragment);
            }
        });
    }

    private static IClasspathEntryDescriptor matchContainer(IClasspathDescriptor classpath, Predicate<IClasspathEntryDescriptor> predicate) {
        return matchEntryDescriptor(classpath.getEntryDescriptors(), and(entryOfType(IClasspathEntry.CPE_CONTAINER), predicate));
    }

    private static Predicate<IClasspathEntryDescriptor> entryOfType(final int type) {
        return new Predicate<IClasspathEntryDescriptor>() {
            public boolean apply(IClasspathEntryDescriptor entry) {
                return entry.getEntryKind() == type;
            }
        };
    }

    private static Predicate<IClasspathEntry> entryForPath(final IPath path) {
        return new Predicate<IClasspathEntry>() {
            public boolean apply(IClasspathEntry entry) {
                return entry.getPath().equals(path);
            }
        };
    }

    private static IClasspathEntryDescriptor matchEntryDescriptor(List<IClasspathEntryDescriptor> classpath, Predicate<IClasspathEntryDescriptor> predicate) {
        for (IClasspathEntryDescriptor entry : classpath) {
            if (predicate.apply(entry)) {
                return entry;
            }
        }
        return null;
    }

    private static IClasspathEntry matchEntry(IClasspathEntry[] classpath, Predicate<IClasspathEntry> predicate) {
        for (IClasspathEntry entry : classpath) {
            if (predicate.apply(entry)) {
                return entry;
            }
        }
        return null;
    }

    public static IClasspathEntryDescriptor findSourceEntryDescriptor(IClasspathDescriptor classpath, String path) {
        for (IClasspathEntryDescriptor entry : classpath.getEntryDescriptors()) {
            if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE && entry.getPath().toString().endsWith(path)) {
                return entry;
            }
        }
        return null;
    }

    public static IClasspathEntry findEntryMatching(IClasspathEntry[] classpath, IPath path) {
        return matchEntry(classpath, entryForPath(path));
    }

}
