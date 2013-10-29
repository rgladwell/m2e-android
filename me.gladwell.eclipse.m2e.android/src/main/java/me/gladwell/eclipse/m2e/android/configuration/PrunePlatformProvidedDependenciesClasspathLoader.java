/*******************************************************************************
 * Copyright (c) 2013 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import static com.google.common.collect.Iterables.filter;

import java.io.FileNotFoundException;
import java.util.List;

import javax.inject.Inject;

import me.gladwell.eclipse.m2e.android.project.AndroidProjectFactory;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.apache.maven.project.MavenProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;

import com.google.common.base.Predicate;

public class PrunePlatformProvidedDependenciesClasspathLoader extends ClasspathLoaderDecorator {

    private final IMavenProjectRegistry projectRegistry;
    private final AndroidProjectFactory<MavenAndroidProject, MavenProject> mavenProjectFactory;

    @Inject
    public PrunePlatformProvidedDependenciesClasspathLoader(ClasspathLoader loader, IMavenProjectRegistry projectRegistry,
            AndroidProjectFactory<MavenAndroidProject, MavenProject> mavenProjectFactory) {
        super(loader);
        this.projectRegistry = projectRegistry;
        this.mavenProjectFactory = mavenProjectFactory;
    }

    @Override
    public Iterable<IClasspathEntry> load(IJavaProject project) throws FileNotFoundException {
        final Iterable<IClasspathEntry> nonRuntimeDependencies = super.load(project);

        MavenProject mavenProject = projectRegistry.getProject(project.getProject()).getMavenProject();
        if(mavenProject != null) {
            final MavenAndroidProject androidProject = mavenProjectFactory.createAndroidProject(mavenProject);
            final List<String> platformProvidedDependencies = androidProject.getPlatformProvidedDependencies();

            if(platformProvidedDependencies != null) {
                final Iterable<IClasspathEntry> prunedNonRuntimeDependencies = filter(nonRuntimeDependencies, new Predicate<IClasspathEntry>() {
                    public boolean apply(IClasspathEntry entry) {
                        if (!platformProvidedDependencies.contains(entry.getPath().toOSString())) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                });

                return prunedNonRuntimeDependencies;
            }
        }

        return nonRuntimeDependencies;
    }

}
