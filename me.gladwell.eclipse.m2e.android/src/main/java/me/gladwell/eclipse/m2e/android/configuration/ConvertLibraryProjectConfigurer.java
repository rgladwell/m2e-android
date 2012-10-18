/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import org.eclipse.m2e.core.internal.markers.IMavenMarkerManager;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;

import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

public class ConvertLibraryProjectConfigurer implements ProjectConfigurer {

	public boolean isConfigured(EclipseAndroidProject project) {
		return project.isLibrary();
	}

	public boolean isValid(MavenAndroidProject project) {
		return project.isLibrary();
	}

	public void configure(EclipseAndroidProject eclipseProject, MavenAndroidProject mavenProject) {
		eclipseProject.setLibrary(mavenProject.isLibrary());
	}

	public void setupEnvironment(ProjectConfigurationRequest request, IMavenMarkerManager markerManager) {
		// not required here
	}
	
}
