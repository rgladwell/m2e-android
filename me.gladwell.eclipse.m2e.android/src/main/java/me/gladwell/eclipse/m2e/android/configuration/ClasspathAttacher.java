/*******************************************************************************
 * Copyright (c) 2015 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import static me.gladwell.eclipse.m2e.android.Log.debug;
import static me.gladwell.eclipse.m2e.android.Log.warn;
import static org.eclipse.jdt.core.IClasspathEntry.CPE_LIBRARY;

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
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;

import com.google.inject.Inject;

public class ClasspathAttacher<T extends EntryAttacher> {

    private final T attacher;
    private final String classifier;
    private final IMaven maven;
    private final IMavenProjectRegistry registry;
    private final AndroidProjectFactory<MavenAndroidProject, MavenProject> factory;

    @Inject
    public ClasspathAttacher(T attacher, String classifier, IMaven maven, IMavenProjectRegistry registry, AndroidProjectFactory<MavenAndroidProject, MavenProject> factory) {
        super();
        this.attacher = attacher;
        this.classifier = classifier;
        this.maven = maven;
        this.registry = registry;
        this.factory = factory;
    }

    public Iterable<IClasspathEntry> attach(IJavaProject project, Iterable<IClasspathEntry> classpath) {
        List<IClasspathEntry> processed = new ArrayList<IClasspathEntry>();
        try {
            IMavenProjectFacade mavenProjectFacade = registry.getProject(project.getProject());

            if (mavenProjectFacade == null) {
                warn("maven project not yet registered for " + project);
                return processed;
            }

            MavenProject mavenProject = mavenProjectFacade.getMavenProject(new NullProgressMonitor());
            MavenAndroidProject androidProject = factory.createAndroidProject(mavenProject);
            List<ArtifactRepository> repositories = mavenProject.getRemoteArtifactRepositories();

            for(IClasspathEntry entry: classpath) {
                try {
                    if(CPE_LIBRARY == entry.getEntryKind() && !attacher.isAttached(entry)) {
                        Dependency dependency = findDependency(entry, androidProject.getNonRuntimeDependencies());
                        if(!maven.isUnavailable(dependency.getGroup(),
                                dependency.getName(),
                                dependency.getVersion(),
                                "jar",
                                classifier,
                                repositories)) {
                            Artifact docs = maven.resolve(dependency.getGroup(),
                                                                    dependency.getName(),
                                                                    dependency.getVersion(),
                                                                    "jar",
                                                                    classifier,
                                                                    repositories,
                                                                    new NullProgressMonitor());

                            processed.add(attacher.attach(entry, docs));
                        } else {
                            debug(classifier + " unavailable for classpath entry=[" + entry + "]");
                            processed.add(entry);
                        }
                    } else {
                        processed.add(entry);
                    }

                } catch (Exception e) {
                    debug("could not resolve " + classifier + " for classpath entry=[" + entry + "]", e);
                    processed.add(entry);
                }
            }
        } catch(CoreException e) {
            throw new ProjectConfigurationException(e);
        }
        return processed;
    }

    private Dependency findDependency(IClasspathEntry entry, List<Dependency> nonRuntimeDependencies) {
        for(Dependency dependency: nonRuntimeDependencies) {
            if(dependency.getPath().equals(entry.getPath().toOSString())) return dependency;
        }
        throw new ProjectConfigurationException("could not find dependency for entry=[" + entry.getPath() + "]");
    }

}
