/*******************************************************************************
 * Copyright (c) 2013, 2014, 2015 Ricardo Gladwell, Csaba Kozák
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import static com.google.inject.Scopes.SINGLETON;

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;

import me.gladwell.eclipse.m2e.android.AndroidMavenPlugin;
import me.gladwell.eclipse.m2e.android.project.AndroidProjectFactory;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ConfigurationModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(File.class).toInstance(AndroidMavenPlugin.getDefault().getStateLocation().toFile());

        bind(ClasspathLoader.class).to(ObjectSerializationClasspathPersister.class);
        bind(ClasspathLoader.class).annotatedWith(PrunePlatformProvidedDependencies.class).to(
                PrunePlatformProvidedDependenciesClasspathLoader.class);

        bind(ClasspathLoader.class).annotatedWith(Caching.class).to(CachingClasspathLoader.class);
        bind(ClasspathLoader.class).annotatedWith(SourceAttacher.class).to(AttachSourcesClasspathLoader.class);
        bind(ClasspathLoader.class).annotatedWith(DocumentsAttacher.class).to(AttachDocumentsClasspathLoader.class);

        bind(ClasspathPersister.class).to(ObjectSerializationClasspathPersister.class);
        bind(EventBus.class).in(SINGLETON);
    }

    @Provides
    public ClasspathAttacher<JavadocsEntryAttacher> javadocClasspathAttacher(IMaven maven, IMavenProjectRegistry registry, AndroidProjectFactory<MavenAndroidProject, MavenProject> factory) {
        return new ClasspathAttacher<JavadocsEntryAttacher>(new JavadocsEntryAttacher(), "javadoc", maven, registry, factory);
    }

    @Provides
    public ClasspathAttacher<SourcesEntryAttacher> sourcesClasspathAttacher(IMaven maven, IMavenProjectRegistry registry, AndroidProjectFactory<MavenAndroidProject, MavenProject> factory) {
        return new ClasspathAttacher<SourcesEntryAttacher>(new SourcesEntryAttacher(), "sources", maven, registry, factory);
    }

}
