/*******************************************************************************
 * Copyright (c) 2014 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class ClasspathMatchers {

    private ClasspathMatchers() {
    }

    public static Matcher<IRuntimeClasspathEntry[]> containsEntry(final String path) {
        return new BaseMatcher<IRuntimeClasspathEntry[]>() {
            @Override
            public boolean matches(Object target) {
                IRuntimeClasspathEntry[] classpath = (IRuntimeClasspathEntry[]) target;
                return containsEntry(path, classpath);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("classpath containing ");
                description.appendText(path);
            }
        };
    }

    private static boolean containsEntry(String path, IRuntimeClasspathEntry[] classpath) {
        for (IRuntimeClasspathEntry entry : classpath) {
            if (entry.getPath().toString().endsWith(path)) {
                return true;
            }
        }
        return false;
    }

    public static Matcher<IClasspathEntry> containsIncludePattern(final String pattern) {
        return new BaseMatcher<IClasspathEntry>() {

            @Override
            public boolean matches(Object target) {
                IClasspathEntry entry = (IClasspathEntry) target;
                return containsIncludePattern(pattern, entry);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("classpath entry containing include pattern") //
                        .appendText(pattern);
            }
        };
    }

    private static boolean containsIncludePattern(String pattern, IClasspathEntry entry) {
        for (IPath inclusionPattern : entry.getInclusionPatterns()) {
            if (inclusionPattern.toOSString().equals(pattern)) {
                return true;
            }
        }
        return false;
    }
    
    public static Matcher<IClasspathEntry> hasAttribute(final String attributeName, final String attributeValue) {
        return new BaseMatcher<IClasspathEntry>() {

            @Override
            public boolean matches(Object target) {
                IClasspathEntry entry = (IClasspathEntry) target;
                return hasAttribute(entry, attributeName, attributeValue);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("classpath entry having attribute ") //
                        .appendText(attributeName) //
                        .appendText(" with value ") //
                        .appendText(attributeValue);
            }
        };
    }

    public static boolean hasAttribute(IClasspathEntry entry, String attributeName, String attributeValue) {
        for (IClasspathAttribute attribute : entry.getExtraAttributes()) {
            if (attribute.getName().equals(attributeName) && attribute.getValue().equals(attributeValue)) {
                return true;
            }
        }
        return false;
    }
}
