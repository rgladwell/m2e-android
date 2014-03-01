/*******************************************************************************
 * Copyright (c) 2014 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class ClasspathMatchers {

    private ClasspathMatchers() {}

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
        for(IRuntimeClasspathEntry entry: classpath) {
            if(entry.getPath().toString().endsWith(path)) {
                return true;
            }
        }
        return false;
    }
}
