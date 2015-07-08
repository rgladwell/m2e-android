/*******************************************************************************
 * Copyright (c) 2015 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import java.util.List;

import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;

enum SourceType {
    UNDEFINED(-1) {
        @Override
        protected boolean matches(MavenAndroidProject project, IClasspathEntry entry) {
            return entry.getEntryKind() != IClasspathEntry.CPE_SOURCE;
        }
    },
    MAIN_JAVA(0) {
        @Override
        protected boolean matches(MavenAndroidProject project, IClasspathEntry entry) {
            IPath segment = entry.getPath().removeFirstSegments(1);
            return contains(project.getSourcePaths(), segment);
        }
    },
    MAIN_RESOURCES(1) {
        @Override
        protected boolean matches(MavenAndroidProject project, IClasspathEntry entry) {
            IPath segment = entry.getPath().removeFirstSegments(1);
            return contains(project.getResourcePaths(), segment);
        }
    },
    GEN(2) {
        @Override
        protected boolean matches(MavenAndroidProject project, IClasspathEntry entry) {
            IPath segment = entry.getPath().removeFirstSegments(1);
            return segment.toString().equals("gen");
        }
    },
    TEST_JAVA(3) {
        @Override
        protected boolean matches(MavenAndroidProject project, IClasspathEntry entry) {
            IPath segment = entry.getPath().removeFirstSegments(1);
            return contains(project.getTestSourcePaths(), segment);
        }
    },
    TEST_RESOURCES(4) {
        @Override
        protected boolean matches(MavenAndroidProject project, IClasspathEntry entry) {
            IPath segment = entry.getPath().removeFirstSegments(1);
            return contains(project.getTestResourcePaths(), segment);
        }
    };

    int priority;

    SourceType(int priority) {
        this.priority = priority;
    }

    protected abstract boolean matches(MavenAndroidProject project, IClasspathEntry entry);

    public static SourceType forPathIn(MavenAndroidProject project, IClasspathEntry entry) {
        for(SourceType type: SourceType.values()) {
            if(type.matches(project, entry)) return type;
        }
        return UNDEFINED;
    }

    private static boolean contains(List<String> paths, IPath target) {
        for (String path : paths) {
            if (path.endsWith(target.toString())) {
                return true;
            }
        }
        return false;
    }
}