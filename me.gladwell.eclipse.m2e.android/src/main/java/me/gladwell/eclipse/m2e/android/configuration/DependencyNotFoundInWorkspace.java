/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import me.gladwell.eclipse.m2e.android.AndroidMavenPlugin;
import me.gladwell.eclipse.m2e.android.project.Dependency;

public class DependencyNotFoundInWorkspace extends ProjectConfigurationError {

	private static final long serialVersionUID = -3959048624226951719L;

	public DependencyNotFoundInWorkspace(Dependency dependency) {
		super("dependency=[" + dependency + "] not found in workspace");
	}

	@Override
	public String getType() {
		return AndroidMavenPlugin.APKLIB_ERROR_TYPE;
	}

}
