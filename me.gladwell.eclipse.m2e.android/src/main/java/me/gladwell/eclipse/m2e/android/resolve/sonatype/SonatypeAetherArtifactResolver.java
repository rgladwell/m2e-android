/*******************************************************************************
 * Copyright (c) 2014 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.resolve.sonatype;

import java.util.List;

import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;

import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;

import com.google.inject.Inject;

class SonatypeAetherArtifactResolver implements ArtifactResolver {

    private final RepositorySystem repository;
    private final RepositorySystemSession session;

    @Inject
    public SonatypeAetherArtifactResolver(RepositorySystem repository, RepositorySystemSession session) {
        super();
        this.repository = repository;
        this.session = session;
    }

    public Artifact resolveArtifact(List<RemoteRepository> repositories, Artifact artifact) {
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(artifact);

        for (RemoteRepository remoteRepository : repositories) {
            artifactRequest.addRepository(remoteRepository);
        }

        ArtifactResult artifactResult;
        try {
            artifactResult = repository.resolveArtifact(session, artifactRequest);
            return artifactResult.getArtifact();
        } catch (ArtifactResolutionException e) {
            throw new ProjectConfigurationException(e);
        }
    }

}
