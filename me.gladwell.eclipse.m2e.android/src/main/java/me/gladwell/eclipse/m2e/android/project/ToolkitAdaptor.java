/*******************************************************************************
 * Copyright (c) 2015 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import org.eclipse.core.resources.IProject;

public abstract class ToolkitAdaptor {

    private final String nature;
    private final String assets;
    private final String builder;
    private final String containerId;

    ToolkitAdaptor(String nature, String assets, String builder, String containerId) {
        this.nature = nature;
        this.assets = assets;
        this.builder = builder;
        this.containerId = containerId;
    }

    public String nature() {
        return nature;
    }

    public String assetsFolder() {
        return assets;
    }

    public Object builder() {
        return builder;
    }

    public String containerId() {
        return containerId;
    }

    public abstract void setAndroidProperty(IProject project, IDEAndroidProject library, String property, String value);

    public abstract void fixProject(IProject project);

    public abstract boolean isLibrary(IProject project);

}
