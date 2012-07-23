/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import me.gladwell.eclipse.m2e.android.configuration.DependencyNotFoundInWorkspace;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AdtEclipseAndroidWorkspace implements AndroidWorkspace {

	private IWorkspace workspace;
	private AndroidProjectFactory<EclipseAndroidProject, IProject> projectFactory;
	private AndroidProjectFactory<MavenAndroidProject, EclipseAndroidProject> projectConverter;

	@Inject
	public AdtEclipseAndroidWorkspace(
			IWorkspace workspace,
			AndroidProjectFactory<EclipseAndroidProject, IProject> projectFactory,
			AndroidProjectFactory<MavenAndroidProject, EclipseAndroidProject> projectConverter) {
		super();
		this.workspace = workspace;
		this.projectFactory = projectFactory;
		this.projectConverter = projectConverter;
	}

	public EclipseAndroidProject findOpenWorkspaceDependency(Dependency dependency) {
		for(IProject project : workspace.getRoot().getProjects()) {
			if (!project.isOpen()) {
				continue;
			}
			EclipseAndroidProject androidProject = projectFactory.createAndroidProject(project);
			if(androidProject.isAndroidProject() && androidProject.isMavenised()) {
				MavenAndroidProject mavenProject = projectConverter.createAndroidProject(androidProject);
				if(mavenProject.matchesDependency(dependency)) {
					return androidProject;
				}
			}
		}

		throw new DependencyNotFoundInWorkspace(dependency);
	}

}
