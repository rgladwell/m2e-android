/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import java.util.List;

import com.google.inject.Inject;

import me.gladwell.eclipse.m2e.android.project.AndroidProject;
import me.gladwell.eclipse.m2e.android.project.AndroidWorkspace;
import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

public class LibraryDependenciesProjectConfigurer implements ProjectConfigurer {

	private AndroidWorkspace workspace;

	@Inject
	public LibraryDependenciesProjectConfigurer(AndroidWorkspace workspace) {
		super();
		this.workspace = workspace;
	}

	public boolean isConfigured(EclipseAndroidProject project) {
		return false;
	}

	public boolean isValid(MavenAndroidProject project) {
		return !project.getLibraryDependencies().isEmpty();
	}

	public void configure(EclipseAndroidProject eclipseProject, MavenAndroidProject mavenProject) {
		List<AndroidProject> libraryProjects = workspace.getAndroidLibraryProjects();
		List<String> libraryDependencies = mavenProject.getLibraryDependencies();

		for(String dependency : libraryDependencies) {
			checkContainsDependency(dependency, libraryProjects);
		}

		eclipseProject.setLibraryDependencies(libraryDependencies);
	}

	private void checkContainsDependency(String dependency, List<AndroidProject> libraryProjects) {
		boolean found = false;

		for(AndroidProject library : libraryProjects) {
			if(library.getName().equals(dependency)) {
				found = true;
			}
		}

		if(!found) {
			throw new LibraryDependencyNotFoundInWorkspace(dependency);
		}
	}

}
