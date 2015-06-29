/*******************************************************************************
 * Copyright (c) 2013, 2014, 2015 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration.workspace;

import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IClasspathEntryDescriptor;
import org.eclipse.m2e.jdt.internal.ClasspathDescriptor;

import com.google.inject.Inject;

import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;
import me.gladwell.eclipse.m2e.android.project.IDEAndroidProject;
import me.gladwell.eclipse.m2e.android.project.IDEAndroidProjectFactory;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

public class MarkAndroidClasspathContainerNotExportedConfigurer implements WorkspaceConfigurer {

    private final IDEAndroidProjectFactory factory;
    
    @Inject
    public MarkAndroidClasspathContainerNotExportedConfigurer(IDEAndroidProjectFactory factory) {
        this.factory = factory;
    }
    
    public boolean isConfigured(IDEAndroidProject project) {
        return false;
    }

    public boolean isValid(MavenAndroidProject project) {
        return true;
    }

    public void configure(IDEAndroidProject eclipseProject, MavenAndroidProject mavenProject) {
        try {
            IJavaProject javaProject = JavaCore.create(eclipseProject.getProject());
            IClasspathDescriptor classpath = new ClasspathDescriptor(javaProject);
            IDEAndroidProject androidProject = factory.createAndroidProject(javaProject.getProject(), classpath);

            androidProject.getClasspath().getAndroidClasspathContainer().markNotExported();

            List<IClasspathEntryDescriptor> descriptors = classpath.getEntryDescriptors();
            IClasspathEntry[] entries = new IClasspathEntry[descriptors.size()];

            for (int i = 0; i < descriptors.size(); ++i) {
                entries[i] = descriptors.get(i).toClasspathEntry();
            }

            javaProject.setRawClasspath(entries, new NullProgressMonitor());
        } catch (JavaModelException e) {
            throw new ProjectConfigurationException("Could not mark ADT libraries container as not exported", e);
        }
    }

}
