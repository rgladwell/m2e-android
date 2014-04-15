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

import com.google.inject.Inject;

public class EclipseAndroidProjectFactory implements AndroidProjectFactory<EclipseAndroidProject, IProject> {

    private final IWorkspace workspace;

    @Inject
    public EclipseAndroidProjectFactory(IWorkspace workspace) {
        super();
        this.workspace = workspace;
    }

    public EclipseAndroidProject createAndroidProject(IProject target) {
        EclipseAndroidProject project = new AdtEclipseAndroidProject(workspace, target);
        return project;
    }

}
