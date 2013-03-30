/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;

import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.m2e.core.embedder.MavenModelManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MavenToEclipseAndroidProjectConverter implements AndroidProjectFactory<MavenAndroidProject, EclipseAndroidProject> {

	private AndroidProjectFactory<MavenAndroidProject, MavenProject> mavenProjectFactory;
	private MavenModelManager mavenModelManager;

	@Inject
	public MavenToEclipseAndroidProjectConverter(
			AndroidProjectFactory<MavenAndroidProject, MavenProject> mavenProjectFactory,
			MavenModelManager mavenModelManager
	) {
		super();
		this.mavenProjectFactory = mavenProjectFactory;
		this.mavenModelManager = mavenModelManager;
	}

	public MavenAndroidProject createAndroidProject(EclipseAndroidProject androidProject) {
		try {
			IFile iPomFile = androidProject.getProject().getFile("pom.xml");
			MavenProject project = mavenModelManager.readMavenProject(iPomFile, null);
			return mavenProjectFactory.createAndroidProject(project);
		} catch (CoreException e) {
			throw new ProjectConfigurationException(e);
		}
	}

}
