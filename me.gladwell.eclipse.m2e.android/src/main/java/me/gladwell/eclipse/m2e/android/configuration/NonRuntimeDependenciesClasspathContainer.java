/*******************************************************************************
 * Copyright (c) 2013 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.toArray;
import static me.gladwell.eclipse.m2e.android.Logger.info;
import static me.gladwell.eclipse.m2e.android.Logger.warn;

import java.io.FileNotFoundException;
import java.util.List;

import me.gladwell.eclipse.m2e.android.AndroidMavenPlugin;
import me.gladwell.eclipse.m2e.android.project.AndroidProjectFactory;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.apache.maven.project.MavenProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;

import com.google.common.base.Predicate;

public class NonRuntimeDependenciesClasspathContainer implements IClasspathContainer {

    private final ClasspathLoader loader;
    private final AndroidProjectFactory<MavenAndroidProject, MavenProject> mavenProjectFactory;
    private final IJavaProject project;
    private final IMavenProjectRegistry projectRegistry;

    public NonRuntimeDependenciesClasspathContainer(ClasspathLoader loader,
            AndroidProjectFactory<MavenAndroidProject, MavenProject> mavenProjectFactory,
            IJavaProject project, IMavenProjectRegistry projectRegistry) {
        super();
        this.loader = loader;
        this.mavenProjectFactory = mavenProjectFactory;
        this.project = project;
        this.projectRegistry = projectRegistry;
    }

    public IClasspathEntry[] getClasspathEntries() {
        try {
            final Iterable<IClasspathEntry> nonRuntimeDependencies = loader.load(project);
            info("non-runtime classpath=["+nonRuntimeDependencies+"] loaded");

            MavenProject mavenProject = projectRegistry.getProject(project.getProject()).getMavenProject();
            if(mavenProject != null) {
                final MavenAndroidProject androidProject = mavenProjectFactory.createAndroidProject(mavenProject);
                final List<String> platformProvidedDependencies = androidProject.getPlatformProvidedDependencies();

                if(platformProvidedDependencies != null) {
                    info("pruning platform provided dependencies=[" + platformProvidedDependencies + "] from classpath");
                    final Iterable<IClasspathEntry> prunedNonRuntimeDependencies = filter(nonRuntimeDependencies, new Predicate<IClasspathEntry>() {
                        public boolean apply(IClasspathEntry entry) {
                            if (!platformProvidedDependencies.contains(entry.getPath().toOSString())) {
                                info("retaining dependency=[" + entry.getPath() + "]");
                                return true;
                            } else {
                                info("pruning dependency=[" + entry.getPath() + "]");
                                return false;
                            }
                        }
                    });
        
                    info("adding non-runtime classpath=[" + prunedNonRuntimeDependencies + "] from classpath");
                    return toArray(prunedNonRuntimeDependencies, IClasspathEntry.class);
                } else {
                    return new IClasspathEntry[0];
                }
            } else {
                return new IClasspathEntry[0];
            }
        } catch (FileNotFoundException e) {
            warn("classpath not yet persisted", e);
            return new IClasspathEntry[0];
        }
    }

    public String getDescription() {
        return "Non-Runtime Maven Dependencies";
    }

    public int getKind() {
        return IClasspathContainer.K_APPLICATION;
    }

    public IPath getPath() {
        return new Path(AndroidMavenPlugin.CONTAINER_NONRUNTIME_DEPENDENCIES);
    }

}
