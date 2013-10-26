package me.gladwell.eclipse.m2e.android.resolve;

import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;

public interface ArtifactResolver {

    Artifact resolveArtifact(RemoteRepository central, Artifact artifact);

}
