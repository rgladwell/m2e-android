/*******************************************************************************
 * Copyright (c) 2015 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import static org.eclipse.jdt.core.IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME;
import static org.eclipse.jdt.core.JavaCore.newClasspathAttribute;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.maven.artifact.Artifact;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;

class JavadocsEntryAttacher implements EntryAttacher {

    public boolean isAttached(IClasspathEntry entry) {
        ClasspathAttributes attributes = new ClasspathAttributes(entry.getExtraAttributes());
        return attributes.hasAttribute(JAVADOC_LOCATION_ATTRIBUTE_NAME);
    }

    public IClasspathEntry attach(IClasspathEntry entry, Artifact docs) {
        try {
            ClasspathAttributes attributes = new ClasspathAttributes(entry.getExtraAttributes());
            attributes.set(newClasspathAttribute(JAVADOC_LOCATION_ATTRIBUTE_NAME, getJavaDocUrl(docs.getFile())));
    
            IClasspathEntry entryWithDocs = JavaCore.newLibraryEntry(entry.getPath(),
                                                            entry.getSourceAttachmentPath(),
                                                            null,
                                                            entry.getAccessRules(),
                                                            attributes.toArray(),
                                                            entry.isExported());

            return entryWithDocs;
        } catch(Exception e) {
            throw new ProjectConfigurationException(e);
        }
    }

    private static String getJavaDocUrl(File file) throws ZipException, IOException {
        URL fileUrl = file.toURI().toURL();
        return "jar:" + fileUrl.toExternalForm() + "!/" + getJavaDocPathInArchive(file);
    }

    private static String getJavaDocPathInArchive(File file) throws ZipException, IOException {
        ZipFile jarFile = null;
        try {
            jarFile = new ZipFile(file);
            String marker = "package-list";
            for (Enumeration<? extends ZipEntry> en = jarFile.entries(); en.hasMoreElements();) {
                ZipEntry entry = en.nextElement();
                String entryName = entry.getName();
                if (entryName.endsWith(marker)) {
                    return entry.getName().substring(0, entryName.length() - marker.length());
                }
            }
        } finally {
            if (jarFile != null) jarFile.close();
        }

        throw new ProjectConfigurationException("error finding javadoc path in JAR=[" + file + "]");
    }
}
