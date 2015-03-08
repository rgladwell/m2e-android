/*******************************************************************************
 * Copyright (c) 2015 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.m2e.jdt.IClasspathDescriptor;

public interface IDEAndroidProjectFactory extends AndroidProjectFactory<IDEAndroidProject, IProject> {

    public IDEAndroidProject createAndroidProject(IProject target);

    public IDEAndroidProject createAndroidProject(IProject target, IClasspathDescriptor classpath);

}
