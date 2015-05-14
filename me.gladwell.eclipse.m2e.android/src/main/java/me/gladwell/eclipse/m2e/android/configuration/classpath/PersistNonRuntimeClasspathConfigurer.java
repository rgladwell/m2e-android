/*******************************************************************************
 * Copyright (c) 2013, 2014, 2015 Ricardo Gladwell, Csaba Kozák
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration.classpath;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Lists.newArrayList;
import static me.gladwell.eclipse.m2e.android.AndroidMavenPlugin.CONTAINER_NONRUNTIME_DEPENDENCIES;
import static me.gladwell.eclipse.m2e.android.configuration.Classpaths.findEntryMatching;
import static me.gladwell.eclipse.m2e.android.configuration.classpath.Paths.dependencyToPathFunction;
import static me.gladwell.eclipse.m2e.android.configuration.classpath.Paths.eclipseProjectToPathFunction;
import static org.eclipse.jdt.core.JavaCore.getClasspathContainer;

import java.util.ArrayList;
import java.util.List;

import me.gladwell.eclipse.m2e.android.configuration.ClasspathPersister;
import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;
import me.gladwell.eclipse.m2e.android.project.AndroidWorkspace;
import me.gladwell.eclipse.m2e.android.project.Dependency;
import me.gladwell.eclipse.m2e.android.project.IDEAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.m2e.jdt.IClasspathDescriptor;

import com.google.inject.Inject;

public class PersistNonRuntimeClasspathConfigurer implements RawClasspathConfigurer {

    private final ClasspathPersister persister;
    private final AndroidWorkspace workspace;

    @Inject
    public PersistNonRuntimeClasspathConfigurer(ClasspathPersister persister, AndroidWorkspace workspace) {
        super();
        this.persister = persister;
        this.workspace = workspace;
    }

    public void configure(MavenAndroidProject mavenProject, IDEAndroidProject eclipseProject, IClasspathDescriptor classpath) {
        final List<Dependency> nonRuntimeDependencies = mavenProject.getNonRuntimeDependencies();

        List<String> nonRuntimeDependencyPaths = from(nonRuntimeDependencies)
                .transform(dependencyToPathFunction())
                .toList();

        if(eclipseProject.shouldResolveWorkspaceProjects()) {
            List<IDEAndroidProject> nonRuntimeProjects = workspace.findOpenWorkspaceDependencies(nonRuntimeDependencies);

            final List<String> nonRuntimeProjectPaths = from(nonRuntimeProjects)
                    .transform(eclipseProjectToPathFunction())
                    .toList();

            nonRuntimeDependencyPaths = newArrayList(concat(nonRuntimeDependencyPaths, nonRuntimeProjectPaths));
        }

        final List<IClasspathEntry> nonRuntimeDependenciesEntries = new ArrayList<IClasspathEntry>();

        try {
            IJavaProject javaProject = JavaCore.create(eclipseProject.getProject());
            IClasspathContainer container = getClasspathContainer(new Path(CONTAINER_NONRUNTIME_DEPENDENCIES), javaProject);

            for (IClasspathEntry entry : classpath.getEntries()) {
                if (nonRuntimeDependencyPaths.contains(entry.getPath().toOSString())
                        || nonRuntimeDependencyPaths.contains(entry.getPath().toString())) {
                    IClasspathEntry containerEntry = findEntryMatching(container.getClasspathEntries(), entry.getPath());
                    if (containerEntry == null) {
                        nonRuntimeDependenciesEntries.add(entry);
                    } else {
                        nonRuntimeDependenciesEntries.add(containerEntry);
                    }
                }
            }
        } catch (JavaModelException e) {
            throw new ProjectConfigurationException(e);
        }

        persister.save(mavenProject, eclipseProject, nonRuntimeDependenciesEntries);
    }

}
