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

import org.eclipse.andmore.AndmoreAndroidConstants;
import org.eclipse.andmore.internal.project.ProjectHelper;
import org.eclipse.andmore.internal.sdk.ProjectState;
import org.eclipse.andmore.internal.sdk.Sdk;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.m2e.jdt.IClasspathDescriptor;

import com.android.io.StreamException;
import com.android.sdklib.internal.project.ProjectPropertiesWorkingCopy;

public class AndmoreToolkitAdaptor extends ToolkitAdaptor {

    AndmoreToolkitAdaptor() {
        super(AndmoreAndroidConstants.NATURE_DEFAULT,
                AndmoreAndroidConstants.WS_ASSETS,
                "org.eclipse.andmore.ApkBuilder",
                AndmoreAndroidConstants.CONTAINER_PRIVATE_LIBRARIES);
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
