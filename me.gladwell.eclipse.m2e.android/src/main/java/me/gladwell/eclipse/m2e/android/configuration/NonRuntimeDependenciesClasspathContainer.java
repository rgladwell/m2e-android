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
import java.util.ArrayList;
import java.util.List;

import me.gladwell.eclipse.m2e.android.AndroidMavenPlugin;
import me.gladwell.eclipse.m2e.android.project.AndroidProjectFactory;
import me.gladwell.eclipse.m2e.android.project.Dependency;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactDescriptorException;
import org.sonatype.aether.resolution.ArtifactDescriptorRequest;
import org.sonatype.aether.resolution.ArtifactDescriptorResult;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import com.google.common.base.Predicate;

public class NonRuntimeDependenciesClasspathContainer implements IClasspathContainer {

    private final ClasspathLoader loader;
    private final RepositorySystem repository;
    private final AndroidProjectFactory<MavenAndroidProject, MavenProject> mavenProjectFactory;
    private final IJavaProject project;
    private final RepositorySystemSession session;
    private final IMavenProjectRegistry projectRegistry;

    public NonRuntimeDependenciesClasspathContainer(ClasspathLoader loader,
            RepositorySystem repository, AndroidProjectFactory<MavenAndroidProject, MavenProject> mavenProjectFactory,
            IJavaProject project, RepositorySystemSession session, IMavenProjectRegistry projectRegistry) {
        super();
        this.loader = loader;
        this.repository = repository;
        this.mavenProjectFactory = mavenProjectFactory;
        this.project = project;
        this.session = session;
        this.projectRegistry = projectRegistry;
    }

    public IClasspathEntry[] getClasspathEntries() {
        try {
            final Iterable<IClasspathEntry> nonRuntimeDependencies = loader.load(project);
            info("non-runtime classpath=["+nonRuntimeDependencies+"] loaded");

            final List<String> platformProvidedDependencies = findPlatformProvidedDependencies();

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
        } catch (FileNotFoundException e) {
            warn("classpath not yet persisted", e);
            return new IClasspathEntry[0];
        } catch (ProjectBuildingException e) {
            throw new ProjectConfigurationException(e);
        }
    }

	private List<String> findPlatformProvidedDependencies() throws ProjectBuildingException {
        MavenProject mavenProject = projectRegistry.getProject(project.getProject()).getMavenProject();
        if(mavenProject != null) {
            final MavenAndroidProject androidProject = mavenProjectFactory.createAndroidProject(mavenProject);
            final Dependency android = androidProject.getAndroidDependency();
            final List<String> platformProvidedDependencies = new ArrayList<String>();
            final DefaultProjectBuildingRequest projectBuildingRequest = new DefaultProjectBuildingRequest();
            projectBuildingRequest.setRepositorySession(session);
    
            final List<org.sonatype.aether.artifact.Artifact> dependencies = resolveDependencies(android, "jar");
            for(org.sonatype.aether.artifact.Artifact dependency : dependencies) {
                platformProvidedDependencies.add(dependency.getFile().getAbsolutePath());
            }
    
            return platformProvidedDependencies;
        } else {
            return null;
        }
    }

    public List<org.sonatype.aether.artifact.Artifact> resolveDependencies(Dependency dependency, String extension) {
        final DefaultArtifact artifact = new DefaultArtifact(dependency.getGroup(), dependency.getName(), extension, dependency.getVersion());
        return resolveDependencies(artifact);
    }

    private List<org.sonatype.aether.artifact.Artifact> resolveDependencies(final org.sonatype.aether.artifact.Artifact artifact) {
        RemoteRepository central = new RemoteRepository( "central", "default", "http://repo1.maven.org/maven2/" );
        ArtifactDescriptorRequest descriptorRequest = new ArtifactDescriptorRequest();
        descriptorRequest.setArtifact( artifact );
        descriptorRequest.addRepository( central );

        List<org.sonatype.aether.artifact.Artifact> resolvedDependencies = new ArrayList<org.sonatype.aether.artifact.Artifact>();

        try {
            ArtifactDescriptorResult descriptorResult = repository.readArtifactDescriptor( session, descriptorRequest );
            resolvedDependencies.add(resolveArtifact(central, descriptorResult.getArtifact()));
            for(org.sonatype.aether.graph.Dependency d : descriptorResult.getDependencies()) {
                Artifact resolvedArtifact = resolveArtifact(central, d.getArtifact());
                resolvedDependencies.add(resolvedArtifact);
                resolvedDependencies.addAll(resolveDependencies(resolvedArtifact));
            }
        } catch (ArtifactDescriptorException e) {
            throw new ProjectConfigurationException(e);
        } catch (ArtifactResolutionException e) {
            throw new ProjectConfigurationException(e);
        }

        return resolvedDependencies;
    }

    private Artifact resolveArtifact(RemoteRepository central, org.sonatype.aether.artifact.Artifact artifact) throws ArtifactResolutionException {
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(artifact);
        artifactRequest.addRepository( central );
        ArtifactResult artifactResult = repository.resolveArtifact(session, artifactRequest);
        return artifactResult.getArtifact();
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
