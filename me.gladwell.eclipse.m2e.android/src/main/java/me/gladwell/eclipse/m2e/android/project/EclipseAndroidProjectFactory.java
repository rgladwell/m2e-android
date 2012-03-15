/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import org.eclipse.core.resources.IProject;


public class EclipseAndroidProjectFactory implements AndroidProjectFactory<EclipseAndroidProject, IProject> {

	public EclipseAndroidProject createAndroidProject(IProject target) {
		return new AdtEclipseAndroidProject(target);
	}

}
