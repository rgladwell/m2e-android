/*******************************************************************************
 * Copyright (c) 2013, 2015 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import static com.google.common.base.Predicates.and;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IClasspathEntryDescriptor;

import com.google.common.base.Predicate;

public class Classpaths {

    private Classpaths() {
    }

    public static IClasspathEntry findContainerMatching(final IClasspathDescriptor classpath, final String path) {
        return matchContainer(classpath, new Predicate<IClasspathEntry>() {
            public boolean apply(IClasspathEntry entry) {
                return entry.getPath().toOSString().equals(path);
            }
        });
    }

    public static IClasspathEntry findContainerContaining(final IClasspathDescriptor classpath, final String fragment) {
        return matchContainer(classpath, new Predicate<IClasspathEntry>() {
            public boolean apply(IClasspathEntry entry) {
                return entry.getPath().toOSString().contains(fragment);
            }
        });
    }

    public static IClasspathEntry findSourceEntry(IClasspathEntry[] classpath, final String path) {
        return matchEntry(classpath, and(entryOfType(IClasspathEntry.CPE_SOURCE), new Predicate<IClasspathEntry>() {
            public boolean apply(IClasspathEntry entry) {
                return entry.getPath().toOSString().endsWith(path);
            }
        }));
    }

    private static IClasspathEntry matchContainer(IClasspathDescriptor classpath, Predicate<IClasspathEntry> predicate) {
        return matchEntry(classpath.getEntries(), and(entryOfType(IClasspathEntry.CPE_CONTAINER), predicate));
    }

    private static Predicate<IClasspathEntry> entryOfType(final int type) {
        return new Predicate<IClasspathEntry>() {
            public boolean apply(IClasspathEntry entry) {
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
