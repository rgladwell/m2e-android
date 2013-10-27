/*******************************************************************************
 * Copyright (c) 2015 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import static me.gladwell.eclipse.m2e.android.Log.warn;
import static org.eclipse.jdt.core.IClasspathEntry.CPE_LIBRARY;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import me.gladwell.eclipse.m2e.android.project.AndroidProjectFactory;
import me.gladwell.eclipse.m2e.android.project.Dependency;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.embedder.IMavenConfiguration;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;

import com.google.inject.Inject;

public class AttachSourcesClasspathLoader extends ClasspathLoaderDecorator {

    private static final String CLASSIFIER_SOURCES = "sources";

    private final IMavenConfiguration configuration;
    private final IMaven maven;
    private final IMavenProjectRegistry registry;
    private final AndroidProjectFactory<MavenAndroidProject, MavenProject> factory;

    // TODO too many dependencies: split class
    @Inject
    public AttachSourcesClasspathLoader(@DocumentsAttacher ClasspathLoader loader, IMavenConfiguration configuration, IMaven maven, IMavenProjectRegistry registry, AndroidProjectFactory<MavenAndroidProject, MavenProject> factory) {
        super(loader);
        this.configuration = configuration;
        this.maven = maven;
        this.registry = registry;
        this.factory = factory;
    }

    @Override
    public Iterable<IClasspathEntry> load(IJavaProject project) throws FileNotFoundException {
        Iterable<IClasspathEntry> classpath = super.load(project);

        if(configuration.isDownloadSources()) {
            List<IClasspathEntry> processed = new ArrayList<IClasspathEntry>();

            try {

                MavenProject mavenProject = registry.getProject(project.getProject()).getMavenProject(new NullProgressMonitor());
                MavenAndroidProject androidProject = factory.createAndroidProject(mavenProject);
                List<ArtifactRepository> repositories = mavenProject.getRemoteArtifactRepositories();

                for(IClasspathEntry entry: classpath) {
                    try {
                        if(CPE_LIBRARY == entry.getEntryKind() && entry.getSourceAttachmentPath() == null) {
                            Dependency dependency = findDependency(entry, androidProject.getNonRuntimeDependencies());
                            Artifact sources = maven.resolve(dependency.getGroup(),
                                                                    dependency.getName(),
                                                                    dependency.getVersion(),
                                                                    "jar",
                                                                    CLASSIFIER_SOURCES,
                                                                    repositories,
                                                                    new NullProgressMonitor());
                            IClasspathEntry entryWithSources = JavaCore.newLibraryEntry(entry.getPath(), Path.fromOSString(sources.getFile().getAbsolutePath()), null);
                            processed.add(entryWithSources);
                        } else {
                            processed.add(entry);
                        }
                    } catch (Exception e) {
                        warn("could not resolve sources for classpath entry=[" + entry + "]", e);
                        processed.add(entry);
                    }
                }

            } catch (CoreException e) {
                throw new ProjectConfigurationException(e);
            }

            return processed;
        }

        return classpath;
    }

    private Dependency findDependency(IClasspathEntry entry, List<Dependency> nonRuntimeDependencies) {
        for(Dependency dependency: nonRuntimeDependencies) {
            if(dependency.getPath().equals(entry.getPath().toOSString())) return dependency;
        }
        throw new ProjectConfigurationException("could not find dependency for entry=[" + entry.getPath() + "]");
    }

}
