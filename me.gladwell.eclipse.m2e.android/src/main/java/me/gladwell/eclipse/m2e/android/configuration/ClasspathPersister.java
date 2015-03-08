/*******************************************************************************
 * Copyright (c) 2013,2014 Ricardo Gladwell, Csaba Kozák
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import me.gladwell.eclipse.m2e.android.project.IDEAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.eclipse.jdt.core.IClasspathEntry;

// TODO move this classpath persistance code to .classpath package
public interface ClasspathPersister {

    void save(MavenAndroidProject mavenProject, IDEAndroidProject eclipseProject, Iterable<IClasspathEntry> classpath);

}
