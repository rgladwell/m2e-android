/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import org.apache.maven.project.MavenProject;

public interface MavenAndroidProject extends AndroidProject {

	public String getGroup();

	public String getVersion();

	public boolean matchesDependency(Dependency dependency);

	/** Replies the Maven project related to this Maven/Android project.
	 * 
	 * @return the reference to the Maven project.
	 */
	public MavenProject getMavenProject();

}