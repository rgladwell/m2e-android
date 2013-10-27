/*******************************************************************************
 * Copyright (c) 2015 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import static me.gladwell.eclipse.m2e.android.Log.debug;

import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.embedder.MavenConfigurationChangeEvent;
import org.eclipse.m2e.core.project.MavenProjectChangedEvent;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

public class CachingClasspathLoader extends ClasspathLoaderDecorator {

    private final ClasspathLoader loader;

    private final LoadingCache<IJavaProject, Iterable<IClasspathEntry>> cache = CacheBuilder.newBuilder()
        .build(
            new CacheLoader<IJavaProject, Iterable<IClasspathEntry>>() {
                public Iterable<IClasspathEntry> load(final IJavaProject project) throws FileNotFoundException {
                    debug("loading runtime classpath container for project=[" + project + "]");
                    return loader.load(project);
                }
            });

    @Inject
    public CachingClasspathLoader(@SourceAttacher ClasspathLoader loader, EventBus events) {
        super(loader);
        this.loader = loader;
        events.register(this);
    }

    @Override
    public Iterable<IClasspathEntry> load(IJavaProject project) throws FileNotFoundException {
        try {
            return cache.get(project);
        } catch (ExecutionException e) {
            if( e.getCause() instanceof FileNotFoundException ) {
                throw (FileNotFoundException) e.getCause();
            } else {
                throw new ProjectConfigurationException(e);
            }
        }
    }

    @Subscribe
    public void onProjectChanged(MavenProjectChangedEvent event) {
        IProject project = event.getMavenProject().getProject();
        IJavaProject javaProject = JavaCore.create(project);
        debug("cache invalidated on maven project=[" + project + "] changed"); 
        cache.invalidate(javaProject);
    }

    @Subscribe
    public void onConfigurationChanged(MavenConfigurationChangeEvent event) {
        debug("cache invalidated on all projecs"); 
        cache.invalidateAll();
    }

}
