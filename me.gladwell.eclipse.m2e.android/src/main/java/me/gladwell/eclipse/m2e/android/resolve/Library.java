package me.gladwell.eclipse.m2e.android.resolve;

import java.io.File;

import org.eclipse.aether.artifact.Artifact;

public class Library {

    private Artifact aetherArtifact;
    private org.sonatype.aether.artifact.Artifact sonatypeArtifact;

    public Library(Artifact artifact) {
        this.aetherArtifact = artifact;
    }

    public Library(org.sonatype.aether.artifact.Artifact artifact) {
        this.sonatypeArtifact = artifact;
    }

    public File getFile() {
        if(aetherArtifact != null) {
            return aetherArtifact.getFile();
        } else {
            return sonatypeArtifact.getFile();
        }
    }

}
