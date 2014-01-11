/*******************************************************************************
 * Copyright (c) 2013 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import static com.google.common.base.Predicates.and;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.m2e.jdt.IClasspathDescriptor;

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

    public static IClasspathEntry findClasspathEntry(IClasspathEntry[] classpath, final String path) {
        return matchClasspathEntry(classpath, and(classpathEntryOfType(IClasspathEntry.CPE_SOURCE), new Predicate<IClasspathEntry>() {
            public boolean apply(IClasspathEntry entry) {
                return entry.getPath().toOSString().endsWith(path) && entry.getOutputLocation() != null;
            }
        }));
    }

    private static IClasspathEntry matchContainer(IClasspathDescriptor classpath, Predicate<IClasspathEntry> predicate) {
        return matchClasspathEntry(classpath.getEntries(), and(classpathEntryOfType(IClasspathEntry.CPE_CONTAINER), predicate));
    }

    private static Predicate<IClasspathEntry> classpathEntryOfType(final int type) {
        return new Predicate<IClasspathEntry>() {
            public boolean apply(IClasspathEntry entry) {
                return entry.getEntryKind() == type;
            }
        };
    }

    private static IClasspathEntry matchClasspathEntry(IClasspathEntry[] classpath, Predicate<IClasspathEntry> predicate) {
        for(IClasspathEntry entry : classpath) {
            if(predicate.apply(entry)) {
                return entry;
            }
        }
        return null;
    }
}
