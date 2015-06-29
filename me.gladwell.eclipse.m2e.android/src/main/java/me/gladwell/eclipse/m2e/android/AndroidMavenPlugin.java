/*******************************************************************************
 * Copyright (c) 2009-2015 Ricardo Gladwell, Hugo Josefson, Csaba Kozák
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.debug.core.ILaunchConfigurationListener;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.embedder.IMavenConfiguration;
import org.eclipse.m2e.core.embedder.IMavenConfigurationChangeListener;
import org.eclipse.m2e.core.project.IMavenProjectChangedListener;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.osgi.framework.BundleContext;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class AndroidMavenPlugin extends Plugin {

    public static final String PLUGIN_ID = "me.gladwell.eclipse.m2e.android";
    public static final String APKLIB_ERROR_TYPE = "me.gladwell.eclipse.m2e.android.markers.dependency.apklib";
    public static final String CONTAINER_NONRUNTIME_DEPENDENCIES = "me.gladwell.eclipse.m2e.android.classpath.NONRUNTIME_DEPENDENCIES";
    public static final String ANDROID_GEN_FOLDER = "gen";

    private static AndroidMavenPlugin plugin;

    private Injector injector;
    private @Inject ILaunchManager launchManager;
    private @Inject ILaunchConfigurationListener launchConfigurationListener;
    private @Inject IMavenProjectRegistry projectManager;
    private @Inject IMavenProjectChangedListener mavenProjectChangedListener;
    private @Inject IMavenConfiguration configuration;
    private @Inject IMavenConfigurationChangeListener mavenConfigurationListener;
    private @Inject IElementChangedListener elementChangedListener;

    /**
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        createInjector(context).injectMembers(this);
        launchManager.addLaunchConfigurationListener(launchConfigurationListener);
        projectManager.addMavenProjectChangedListener(mavenProjectChangedListener);
        configuration.addConfigurationChangeListener(mavenConfigurationListener);
        JavaCore.addElementChangedListener(elementChangedListener, ElementChangedEvent.POST_CHANGE);
    }

    /**
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        launchManager.removeLaunchConfigurationListener(launchConfigurationListener);
        projectManager.removeMavenProjectChangedListener(mavenProjectChangedListener);
        JavaCore.removeElementChangedListener(elementChangedListener);
        plugin = null;
        super.stop(context);
    }

    public static AndroidMavenPlugin getDefault() {
        return plugin;
    }

    public Injector createInjector(BundleContext context) {
        injector = Guice.createInjector(new PluginModule(context));
        return injector;
    }

    public Injector getInjector() {
        return injector;
    }

}
