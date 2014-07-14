/*******************************************************************************
 * Copyright (c) 2013, 2014, 2015 Ricardo Gladwell, Csaba Kozák
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import java.io.File;

import me.gladwell.eclipse.m2e.android.AndroidMavenPlugin;

import com.google.inject.AbstractModule;

public class ConfigurationModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(File.class).toInstance(AndroidMavenPlugin.getDefault().getStateLocation().toFile());

        bind(ClasspathLoader.class).to(ObjectSerializationClasspathPersister.class);
        bind(ClasspathLoader.class).annotatedWith(PrunePlatformProvidedDependencies.class).to(
                PrunePlatformProvidedDependenciesClasspathLoader.class);

        bind(ClasspathLoader.class).annotatedWith(SourceAttacher.class).to(AttachSourcesClasspathLoader.class);
        bind(ClasspathLoader.class).annotatedWith(DocumentsAttacher.class).to(AttachDocumentsClasspathLoader.class);

        bind(ClasspathPersister.class).to(ObjectSerializationClasspathPersister.class);
    }

}
