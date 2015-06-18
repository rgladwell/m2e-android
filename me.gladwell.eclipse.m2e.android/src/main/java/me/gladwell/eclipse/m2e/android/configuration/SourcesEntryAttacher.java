/*******************************************************************************
 * Copyright (c) 2014 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import org.apache.maven.artifact.Artifact;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;

public class SourcesEntryAttacher implements EntryAttacher {

    public IClasspathEntry attach(IClasspathEntry entry, Artifact sources) {
        return JavaCore.newLibraryEntry(entry.getPath(),
                Path.fromOSString(sources.getFile().getAbsolutePath()),
                null,
                entry.getAccessRules(),
                entry.getExtraAttributes(),
                entry.isExported());
    }

    public boolean isAttached(IClasspathEntry entry) {
        return entry.getSourceAttachmentPath() != null;
    }

}
