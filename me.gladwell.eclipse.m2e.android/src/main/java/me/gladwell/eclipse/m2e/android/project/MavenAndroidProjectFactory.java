/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import me.gladwell.eclipse.m2e.android.AndroidMavenException;

import org.apache.maven.project.MavenProject;

public class MavenAndroidProjectFactory implements AndroidProjectFactory<MavenAndroidProject, MavenProject> {

	public MavenAndroidProject createAndroidProject(MavenProject mavenProject) {
		if(JaywayMavenAndroidProject.findJaywayAndroidPlugin(mavenProject.getBuildPlugins()) != null) {
			return new JaywayMavenAndroidProject(mavenProject);
		}

		throw new AndroidMavenException("un-recognised maven-android project type");
	}
}
