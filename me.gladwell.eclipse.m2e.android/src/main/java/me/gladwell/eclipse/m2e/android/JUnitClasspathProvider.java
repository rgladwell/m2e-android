/*******************************************************************************
 * Copyright (c) 2014 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android;

import static java.util.Arrays.asList;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;
import static org.eclipse.jdt.launching.JavaRuntime.newArchiveRuntimeClasspathEntry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.gladwell.eclipse.m2e.android.project.AndroidProjectFactory;
import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IRuntimeClasspathProvider;

import com.google.inject.Inject;

public class JUnitClasspathProvider implements IRuntimeClasspathProvider {

    private final IRuntimeClasspathProvider classpathProvider;
    private final IWorkspace workspace;
    private final AndroidProjectFactory<EclipseAndroidProject, IProject> factory;
    private final AndroidProjectFactory<MavenAndroidProject, EclipseAndroidProject> factory2;

    @Inject
    public JUnitClasspathProvider(@Maven IRuntimeClasspathProvider classpathProvider, IWorkspace workspace,
            AndroidProjectFactory<EclipseAndroidProject, IProject> factory,
            AndroidProjectFactory<MavenAndroidProject, EclipseAndroidProject> factory2) {
        super();
        this.classpathProvider = classpathProvider;
        this.workspace = workspace;
        this.factory = factory;
        this.factory2 = factory2;
    }

    public IRuntimeClasspathEntry[] computeUnresolvedClasspath(ILaunchConfiguration config) throws CoreException {
        List<IRuntimeClasspathEntry> classpath = new ArrayList<IRuntimeClasspathEntry>(
                asList(classpathProvider.computeUnresolvedClasspath(config)));

        addPlatformDependencies(config, classpath);
        addBinaryFolder(config, classpath);

        return classpath.toArray(new IRuntimeClasspathEntry[classpath.size()]);
    }

    private void addPlatformDependencies(ILaunchConfiguration config, List<IRuntimeClasspathEntry> classpath)
            throws CoreException {
        IProject project = workspace.getRoot().getProject(config.getAttribute(ATTR_PROJECT_NAME, (String) null));
        MavenAndroidProject androidProject = factory2.createAndroidProject(factory.createAndroidProject(project));

        for (String dependency : androidProject.getPlatformProvidedDependencies()) {
            classpath.add(newArchiveRuntimeClasspathEntry(new Path(dependency)));
        }
    }

    private void addBinaryFolder(ILaunchConfiguration config, List<IRuntimeClasspathEntry> classpath)
            throws CoreException {
        IProject project = workspace.getRoot().getProject(config.getAttribute(ATTR_PROJECT_NAME, (String) null));
        IFolder binaries = project.getFolder("bin" + File.separator + "classes");
        classpath.add(newArchiveRuntimeClasspathEntry(binaries.getLocation()));
    }

    public IRuntimeClasspathEntry[] resolveClasspath(IRuntimeClasspathEntry[] classpath, ILaunchConfiguration config)
            throws CoreException {
        return classpathProvider.resolveClasspath(classpath, config);
    }

}
