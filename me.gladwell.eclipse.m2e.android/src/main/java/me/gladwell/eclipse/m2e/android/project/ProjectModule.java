/*******************************************************************************
 * Copyright (c) 2014, 2015 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import org.apache.maven.project.MavenProject;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

public class ProjectModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AndroidWorkspace.class).to(EclipseAndroidWorkspace.class);

        bind(new TypeLiteral<AndroidProjectFactory<MavenAndroidProject, MavenProject>>(){}).to(MavenAndroidProjectFactory.class);
        bind(IDEAndroidProjectFactory.class).to(MultiToolkitEclipseAndroidProjectFactory.class);
        bind(new TypeLiteral<AndroidProjectFactory<MavenAndroidProject, IDEAndroidProject>>(){}).to(MavenToEclipseAndroidProjectConverter.class);
    
        bind(IMaven.class).toInstance(MavenPlugin.getMaven());
    }

}
