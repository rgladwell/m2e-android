/*******************************************************************************
 * Copyright (c) 2013, 2014 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android;

import static org.eclipse.jdt.core.JavaCore.setClasspathContainer;
import me.gladwell.eclipse.m2e.android.configuration.ClasspathLoader;
import me.gladwell.eclipse.m2e.android.configuration.NonRuntimeDependenciesClasspathContainer;
import me.gladwell.eclipse.m2e.android.configuration.PrunePlatformProvidedDependencies;
import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProjectFactory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;

import com.google.inject.Inject;

public class NonRuntimeDependenciesContainerInitializer extends ClasspathContainerInitializer {

    @Inject @PrunePlatformProvidedDependencies private ClasspathLoader loader;
    @Inject EclipseAndroidProjectFactory factory;

    @Override
    public void initialize(IPath path, IJavaProject project) throws CoreException {
        final EclipseAndroidProject eclipseProject = factory.createAndroidProject(project.getProject());
        final IClasspathContainer nonRuntimeContainer = new NonRuntimeDependenciesClasspathContainer(loader, eclipseProject);
        setClasspathContainer(path, new IJavaProject[] { project }, new IClasspathContainer[] { nonRuntimeContainer }, new NullProgressMonitor());
    }

    @Override
    public boolean canUpdateClasspathContainer(IPath containerPath, IJavaProject project) {
        return true;
    }

    @Override
    public void requestClasspathContainerUpdate(IPath containerPath, IJavaProject project, IClasspathContainer container) throws CoreException {
        super.requestClasspathContainerUpdate(containerPath, project, container);
    }

}
