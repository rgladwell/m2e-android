/*******************************************************************************
 * Copyright (c) 2013, 2014 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.resolve.sonatype;

import java.util.ArrayList;
import java.util.List;

import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;
import me.gladwell.eclipse.m2e.android.project.Dependency;
import me.gladwell.eclipse.m2e.android.resolve.Library;
import me.gladwell.eclipse.m2e.android.resolve.LibraryResolver;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactDescriptorException;
import org.sonatype.aether.resolution.ArtifactDescriptorRequest;
import org.sonatype.aether.resolution.ArtifactDescriptorResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import com.google.inject.Inject;

public class SonatypeAetherLibraryResolver implements LibraryResolver {

    private final ArtifactResolver resolver;
    private final RepositorySystem repository;
    private final RepositorySystemSession session;

    @Inject
    public SonatypeAetherLibraryResolver(ArtifactResolver resolver, RepositorySystem repository,
            RepositorySystemSession session) {
        super();
        this.resolver = resolver;
        this.repository = repository;
        this.session = session;
    }

    public List<Library> resolveLibraries(Dependency dependency, String type, MavenProject project) {
        final DefaultArtifact artifact = new DefaultArtifact(
                dependency.getGroup(),
                dependency.getName(),
                type,
                dependency.getVersion());

        final List<ArtifactRepository> repositories = project.getRemoteArtifactRepositories();
        final List<RemoteRepository> remoteRepositories = new ArrayList<RemoteRepository>();

        for (ArtifactRepository repository : repositories) {
            RemoteRepository repo = new RemoteRepository(repository.getId(), repository.getLayout().toString(),
                    repository.getUrl());
            remoteRepositories.add(repo);
        }

        return resolveDependencies(artifact, remoteRepositories);
    }

    private List<Library> resolveDependencies(Artifact artifact, List<RemoteRepository> repositories) {
        ArtifactDescriptorRequest descriptorRequest = new ArtifactDescriptorRequest();
        descriptorRequest.setArtifact(artifact);

        for (RemoteRepository remoteRepository : repositories) {
            descriptorRequest.addRepository(remoteRepository);
        }

        List<Library> libraries = new ArrayList<Library>();

        try {
            ArtifactDescriptorResult descriptorResult = repository.readArtifactDescriptor(session, descriptorRequest);
            libraries.add(new Library(resolver.resolveArtifact(repositories, descriptorResult.getArtifact())));
            for (org.sonatype.aether.graph.Dependency d : descriptorResult.getDependencies()) {
                Artifact resolvedArtifact = resolver.resolveArtifact(repositories, d.getArtifact());
                libraries.add(new Library(resolvedArtifact));
                libraries.addAll(resolveDependencies(resolvedArtifact, repositories));
            }
        } catch (ArtifactDescriptorException e) {
            throw new ProjectConfigurationException(e);
        }

        return libraries;
    }

}
