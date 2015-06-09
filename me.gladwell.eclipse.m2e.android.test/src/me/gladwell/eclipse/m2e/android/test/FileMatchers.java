/*******************************************************************************
 * Copyright (c) 2015 Ricardo Gladwell, Csaba Kozák
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class FileMatchers {

    private FileMatchers() {
    }

    public static Matcher<IFile> containsString(final String expected) {
        return new BaseMatcher<IFile>() {
            
            @Override
            public boolean matches(Object target) {
                return getContents((IFile) target).contains(expected);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("file containing ");
                description.appendText(expected);
            }
            
            @Override
            public void describeMismatch(Object item, Description description) {
                description.appendText("was ").appendValue(item).appendText(" with content:\n")
                        .appendText(getContents((IFile) item));
            }
        };
    }

    private static String getContents(IFile file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(file.getContents()));
            StringBuilder builder = new StringBuilder();
            
            while (reader.ready()) {
                String line = reader.readLine();
                builder.append(line).append('\n');
            }
            
            return builder.toString();
        } catch (Exception e) {

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {

                }
            }
        }
        return "";
    }
}
