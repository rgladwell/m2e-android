/*******************************************************************************
 * Copyright (c) 2015 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import java.io.IOException;

import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.m2e.jdt.IClasspathDescriptor;

import com.android.ide.eclipse.adt.AdtConstants;
import com.android.ide.eclipse.adt.internal.project.ProjectHelper;
import com.android.ide.eclipse.adt.internal.sdk.ProjectState;
import com.android.ide.eclipse.adt.internal.sdk.Sdk;
import com.android.io.StreamException;
import com.android.sdklib.internal.project.ProjectPropertiesWorkingCopy;

public class AdtToolkitAdaptor extends ToolkitAdaptor {

    AdtToolkitAdaptor() {
        super(AdtConstants.NATURE_DEFAULT,
                AdtConstants.WS_ASSETS,
                "com.android.ide.eclipse.adt.ApkBuilder",
                AdtConstants.CONTAINER_PRIVATE_LIBRARIES);
    }

    @Override
    public void setAndroidProperty(IProject project, IDEAndroidProject library, String property, String value) {
        try {
            ProjectState state = Sdk.getProjectState(project);
            ProjectPropertiesWorkingCopy workingCopy = state.getProperties().makeWorkingCopy();
            workingCopy.setProperty(property, value);
            workingCopy.save();
            state.reloadProperties();
            if (library != null) {
                state.needs(Sdk.getProjectState(library.getProject()));
            }
        } catch (IOException e) {
            throw new ProjectConfigurationException(e);
        } catch (StreamException e) {
            throw new ProjectConfigurationException(e);
        }

    }

    @Override
    public void fixProject(IProject project) {
        try {
            ProjectHelper.fixProject(project);
        } catch (JavaModelException e) {
            throw new ProjectConfigurationException(e);
        }
    }

    @Override
    public boolean isLibrary(IProject project) {
        ProjectState state = Sdk.getProjectState(project);
        return state.isLibrary();
    }

}
