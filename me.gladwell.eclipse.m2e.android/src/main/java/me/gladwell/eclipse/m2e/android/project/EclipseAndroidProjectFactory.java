/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;

import com.google.inject.Inject;

public class EclipseAndroidProjectFactory implements AndroidProjectFactory<EclipseAndroidProject, IProject> {

    private final IWorkspace workspace;
    private final IMavenProjectRegistry registry;

    @Inject
    public EclipseAndroidProjectFactory(IWorkspace workspace, IMavenProjectRegistry registry) {
        super();
        this.workspace = workspace;
        this.registry = registry;
    }

    public EclipseAndroidProject createAndroidProject(IProject target) {
        EclipseAndroidProject project = new AdtEclipseAndroidProject(registry, workspace, target);
        return project;
    }

}
