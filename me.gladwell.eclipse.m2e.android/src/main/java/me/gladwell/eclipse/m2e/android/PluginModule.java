/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android;

import me.gladwell.eclipse.m2e.android.configuration.ConfigurationModule;
import me.gladwell.eclipse.m2e.android.configuration.classpath.ClasspathModule;
import me.gladwell.eclipse.m2e.android.configuration.workspace.WorkspaceModule;
import me.gladwell.eclipse.m2e.android.project.ProjectModule;
import me.gladwell.eclipse.m2e.android.resolve.ResolutionModule;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.MavenModelManager;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.jdt.internal.JavaProjectConfigurator;
import com.google.inject.AbstractModule;

public class PluginModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new ConfigurationModule());
        install(new ClasspathModule());
        install(new WorkspaceModule());
        install(new ProjectModule());
        install(new ResolutionModule());

        bind(AbstractProjectConfigurator.class).to(JavaProjectConfigurator.class);
        bind(IWorkspace.class).toInstance(ResourcesPlugin.getWorkspace());
        bind(MavenModelManager.class).toInstance(MavenPlugin.getMavenModelManager());
    }

}
