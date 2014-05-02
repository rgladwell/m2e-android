/*******************************************************************************
 * Copyright (c) 2013, 2014 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.resolve;

import java.util.ArrayList;
import java.util.List;

import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;
import me.gladwell.eclipse.m2e.android.project.Dependency;

import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;

import com.google.inject.Inject;

public class AetherDependencyResolver implements DependencyResolver {

    private final ArtifactResolver artifactResolver;
    private final RepositorySystem repository;
    private final RepositorySystemSession session;

    @Inject
    public AetherDependencyResolver(ArtifactResolver artifactResolver, RepositorySystem repository,
            RepositorySystemSession session) {
        super();
        this.artifactResolver = artifactResolver;
        this.repository = repository;
        this.session = session;
    }

    public List<org.eclipse.aether.artifact.Artifact> resolveDependencies(Dependency dependency, String extension, List<RemoteRepository> repositories) {
        final DefaultArtifact artifact = new DefaultArtifact(dependency.getGroup(), dependency.getName(), extension,
                dependency.getVersion());
        return resolveDependencies(artifact, repositories);
    }

    private List<org.eclipse.aether.artifact.Artifact> resolveDependencies(
            final org.eclipse.aether.artifact.Artifact artifact,
            List<RemoteRepository> repositories) {
        ArtifactDescriptorRequest descriptorRequest = new ArtifactDescriptorRequest();
        descriptorRequest.setArtifact(artifact);
        
        for (RemoteRepository remoteRepository : repositories) {
            descriptorRequest.addRepository(remoteRepository);
        }

        List<org.eclipse.aether.artifact.Artifact> resolvedDependencies = new ArrayList<org.eclipse.aether.artifact.Artifact>();

        try {
            ArtifactDescriptorResult descriptorResult = repository.readArtifactDescriptor(session, descriptorRequest);
            resolvedDependencies.add(artifactResolver.resolveArtifact(repositories, descriptorResult.getArtifact()));
            for (org.eclipse.aether.graph.Dependency d : descriptorResult.getDependencies()) {
                Artifact resolvedArtifact = artifactResolver.resolveArtifact(repositories, d.getArtifact());
                resolvedDependencies.add(resolvedArtifact);
                resolvedDependencies.addAll(resolveDependencies(resolvedArtifact, repositories));
            }
        } catch (ArtifactDescriptorException e) {
            throw new ProjectConfigurationException(e);
        }
        
        return resolvedDependencies;
    }
}
