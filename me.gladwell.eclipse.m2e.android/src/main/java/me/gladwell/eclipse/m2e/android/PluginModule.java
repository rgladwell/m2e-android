/*******************************************************************************
 * Copyright (c) 2012, 2013, 2014 Ricardo Gladwell
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
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationListener;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IRuntimeClasspathProvider;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.MavenModelManager;
import org.eclipse.m2e.core.project.IMavenProjectChangedListener;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.jdt.internal.JavaProjectConfigurator;
import org.eclipse.m2e.jdt.internal.launch.MavenRuntimeClasspathProvider;

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

        bind(IRuntimeClasspathProvider.class).annotatedWith(Maven.class).to(MavenRuntimeClasspathProvider.class);

        bind(Object.class).to(JUnitClasspathProvider.class);
        bind(ILaunchConfigurationListener.class).to(AndroidMavenLaunchConfigurationListener.class);
        bind(IMavenProjectChangedListener.class).to(AndroidMavenLaunchConfigurationListener.class);
        bind(ILaunchManager.class).toInstance(DebugPlugin.getDefault().getLaunchManager());
        bind(IMavenProjectRegistry.class).toInstance(MavenPlugin.getMavenProjectRegistry());
    }

}
