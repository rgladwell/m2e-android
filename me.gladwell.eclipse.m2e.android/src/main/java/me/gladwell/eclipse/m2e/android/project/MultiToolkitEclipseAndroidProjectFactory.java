/*******************************************************************************
 * Copyright (c) 2012, 2015 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.google.inject.Inject;

public class MultiToolkitEclipseAndroidProjectFactory implements IDEAndroidProjectFactory {
    
    private static enum Toolkit {

        ADT("com.android.ide.eclipse.adt"),
        Andmore("org.eclipse.andmore");

        private final String id;

        Toolkit(String id) {
            this.id = id;
        }

        public String pluginId() {
            return id;
        }

    }

    private final IWorkspace workspace;
    private final IMavenProjectRegistry registry;
    private final BundleContext osgi;

    @Inject
    public MultiToolkitEclipseAndroidProjectFactory(IWorkspace workspace, IMavenProjectRegistry registry, BundleContext osgi) {
        super();
        this.workspace = workspace;
        this.registry = registry;
        this.osgi = osgi;
    }

    public IDEAndroidProject createAndroidProject(IProject target) {
        ToolkitAdaptor adaptor = findToolkitAdaptor();
        return new EclipseAndroidProject(adaptor , registry, workspace, target);
    }

    public IDEAndroidProject createAndroidProject(IProject target, IClasspathDescriptor classpath) {
        ToolkitAdaptor adaptor = findToolkitAdaptor();
        return new EclipseAndroidProject(adaptor , registry, target, classpath);
    }

    private ToolkitAdaptor findToolkitAdaptor() {
        for(Toolkit toolkit : Toolkit.values()) {
            if(searchBundles(toolkit.pluginId())) {
                if(toolkit.equals(Toolkit.ADT)) {
                    return new AdtToolkitAdaptor();
                } else if(toolkit.equals(Toolkit.Andmore)) {
                    return new AndmoreToolkitAdaptor();
                }
            }
        }
        throw new ProjectConfigurationException("no installed toolkit found");
    }

    private Boolean searchBundles(String plugin) {
        for(Bundle bundle : osgi.getBundles()) {
            if(bundle.getSymbolicName().equals(plugin)) return true;
        }
        return false;
    }

}
