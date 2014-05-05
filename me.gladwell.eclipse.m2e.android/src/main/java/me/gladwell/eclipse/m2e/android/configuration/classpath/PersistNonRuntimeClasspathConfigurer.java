/*******************************************************************************
 * Copyright (c) 2013, 2014 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration.classpath;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.gladwell.eclipse.m2e.android.configuration.ClasspathPersister;
import me.gladwell.eclipse.m2e.android.configuration.DependencyNotFoundInWorkspace;
import me.gladwell.eclipse.m2e.android.project.AndroidWorkspace;
import me.gladwell.eclipse.m2e.android.project.Dependency;
import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.apache.maven.artifact.Artifact;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IClasspathEntryDescriptor;

import com.google.common.base.Function;

import static com.google.common.collect.Lists.transform;

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

    public void configure(MavenAndroidProject mavenProject, EclipseAndroidProject eclipseProject, IClasspathDescriptor classpath) {
        final List<Dependency> nonRuntimeDependencies = mavenProject.getNonRuntimeDependencies();
        final List<String> nonRuntimeDependencyPaths = transform(nonRuntimeDependencies, new Function<Dependency, String>() {

            public String apply(Dependency dependency) {
               return dependency.getPath();
            }
        });
        
        final List<IClasspathEntry> nonRuntimeDependenciesEntries = new ArrayList<IClasspathEntry>();
        
        List<IPath> nonRuntimeProjects = null;
        
        if (eclipseProject.shouldResolveWorkspaceProjects()) {
            nonRuntimeProjects = getNonRuntimeProjects(nonRuntimeDependencies);
        }
        
        for (IClasspathEntryDescriptor descriptor : classpath.getEntryDescriptors()) {
            if (nonRuntimeDependencyPaths.contains(descriptor.getPath().toOSString())) {
                nonRuntimeDependenciesEntries.add(descriptor.toClasspathEntry());
            }
            
            if (nonRuntimeProjects != null) {
                if (nonRuntimeProjects.contains(descriptor.getPath())) {
                    nonRuntimeDependenciesEntries.add(descriptor.toClasspathEntry());
                }
            }
        }

        persister.save(mavenProject.getName(), nonRuntimeDependenciesEntries);
    }
    
    private List<IPath> getNonRuntimeProjects(List<Dependency> dependencies) {
        Set<IPath> nonRuntimeProjects = new HashSet<IPath>();
        
        for (Dependency dependency : dependencies) {
            
            if (!Artifact.SCOPE_COMPILE.equals(dependency.getScope()) && !Artifact.SCOPE_RUNTIME.equals(dependency.getScope())) {
                EclipseAndroidProject workspaceDependency = null;
                
                try {
                    workspaceDependency = workspace.findOpenWorkspaceDependency(dependency);
                } catch (DependencyNotFoundInWorkspace e) {
                    continue;
                }
                
                nonRuntimeProjects.add(workspaceDependency.getProject().getFullPath());
            }
        }

        return new ArrayList<IPath>(nonRuntimeProjects);
    }

}
