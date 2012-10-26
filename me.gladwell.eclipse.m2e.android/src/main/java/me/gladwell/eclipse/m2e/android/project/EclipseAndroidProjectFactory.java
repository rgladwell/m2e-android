/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import org.eclipse.core.resources.IProject;

import com.google.inject.Inject;
import com.google.inject.Injector;


public class EclipseAndroidProjectFactory implements AndroidProjectFactory<EclipseAndroidProject, IProject> {

	@Inject
	Injector injector;
	
	public EclipseAndroidProject createAndroidProject(IProject target) {
		EclipseAndroidProject project = new AdtEclipseAndroidProject(target);
		injector.injectMembers(project);
		return project;
	}

}
