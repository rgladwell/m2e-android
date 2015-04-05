/*******************************************************************************
 * Copyright (c) 2015 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import java.io.FileNotFoundException;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.m2e.core.embedder.IMavenConfiguration;

import com.google.inject.Inject;

public class AttachSourcesClasspathLoader extends ClasspathLoaderDecorator {

    private final IMavenConfiguration configuration;
    private final ClasspathAttacher<SourcesEntryAttacher> attacher;

    @Inject
    public AttachSourcesClasspathLoader(@DocumentsAttacher ClasspathLoader loader, IMavenConfiguration configuration, ClasspathAttacher<SourcesEntryAttacher> attacher) {
        super(loader);
        this.configuration = configuration;
        this.attacher = attacher;
    }

    @Override
    public Iterable<IClasspathEntry> load(IJavaProject project) throws FileNotFoundException {
        Iterable<IClasspathEntry> classpath = super.load(project);

        if(configuration.isDownloadSources()) {
            return attacher.attach(project, classpath);
        }

        return classpath;
    }

}
