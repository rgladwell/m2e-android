/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import java.util.List;

import org.eclipse.core.resources.IProject;

public interface EclipseAndroidProject extends AndroidProject {

	public IProject getProject();

	public void setAndroidProject(boolean androidProject);

	public void setLibrary(boolean isLibrary);

	public void setProvidedDependencies(List<String> providedDependencies);

	public void setLibraryDependencies(List<String> libraryDependencies);

	public void fixProject();

}
