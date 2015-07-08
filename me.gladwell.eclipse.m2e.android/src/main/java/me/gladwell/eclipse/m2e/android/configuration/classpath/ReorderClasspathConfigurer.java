/*******************************************************************************
 * Copyright (c) 2015 David Carver and Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration.classpath;

import static java.util.Arrays.sort;
import static me.gladwell.eclipse.m2e.android.configuration.Classpaths.bySourceFolderOrdering;

import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;
import me.gladwell.eclipse.m2e.android.project.IDEAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.m2e.jdt.IClasspathDescriptor;

public class ReorderClasspathConfigurer implements RawClasspathConfigurer {

    public void configure(MavenAndroidProject mavenProject, IDEAndroidProject eclipseProject, IClasspathDescriptor classpath) {
        try {
            IJavaProject javaproject = JavaCore.create(eclipseProject.getProject());
            IClasspathEntry[] tosort = javaproject.readRawClasspath();
            sort(tosort, bySourceFolderOrdering(mavenProject));
            javaproject.setRawClasspath(tosort, new NullProgressMonitor());
            javaproject.save(new NullProgressMonitor(), false);
        } catch (JavaModelException e) {
            throw new ProjectConfigurationException("error re-ordering classpath", e);
        }
    }

}
