/*******************************************************************************
 * Copyright (c) 2013 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.resolve;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;
import me.gladwell.eclipse.m2e.android.project.Dependency;

import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactDescriptorException;
import org.sonatype.aether.resolution.ArtifactDescriptorRequest;
import org.sonatype.aether.resolution.ArtifactDescriptorResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;

public class AetherDependencyResolver implements DependencyResolver {

    private final ArtifactResolver artifactResolver;
    private final RepositorySystem repository;
    private final RepositorySystemSession session;

    @Inject
    public AetherDependencyResolver(ArtifactResolver artifactResolver, RepositorySystem repository, RepositorySystemSession session) {
        super();
        this.artifactResolver = artifactResolver;
        this.repository = repository;
        this.session = session;
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
            resolvedDependencies.add(artifactResolver.resolveArtifact(central, descriptorResult.getArtifact()));
            for(org.sonatype.aether.graph.Dependency d : descriptorResult.getDependencies()) {
                Artifact resolvedArtifact = artifactResolver.resolveArtifact(central, d.getArtifact());
                resolvedDependencies.add(resolvedArtifact);
                resolvedDependencies.addAll(resolveDependencies(resolvedArtifact));
            }
        } catch (ArtifactDescriptorException e) {
            throw new ProjectConfigurationException(e);
        }

        return resolvedDependencies;
    }
}
