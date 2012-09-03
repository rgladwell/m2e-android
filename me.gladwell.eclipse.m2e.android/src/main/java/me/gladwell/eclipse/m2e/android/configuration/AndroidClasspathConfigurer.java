/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import me.gladwell.eclipse.m2e.android.project.AndroidProject;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.m2e.jdt.IClasspathDescriptor;

public interface AndroidClasspathConfigurer {

	public void addGenFolder(IJavaProject javaProject, AndroidProject project, IClasspathDescriptor classpath);
	public void removeNonRuntimeDependencies(AndroidProject project, IClasspathDescriptor classpath);
	public void markMavenContainerExported(IClasspathDescriptor classpath);
	public void removeJreClasspathContainer(IClasspathDescriptor classpath);
    public void modifySourceFolderOutput(IJavaProject javaProject, AndroidProject project, IClasspathDescriptor classpath);


}
