package me.gladwell.eclipse.m2e.android.project;

import static me.gladwell.eclipse.m2e.android.project.JaywayMavenAndroidProject.ANDROID_LIBRARY_PACKAGE_TYPE;

import org.apache.maven.artifact.Artifact;

public class MavenDependency implements Dependency {

	private Artifact artifact;

	public MavenDependency(Artifact artifact) {
		super();
		this.artifact = artifact;
	}

	public String getName() {
		return artifact.getArtifactId();
	}

	public String getGroup() {
		return artifact.getGroupId();
	}

	public String getVersion() {
		return artifact.getVersion();
	}

	public boolean isLibrary() {
		return artifact.getType().equals(ANDROID_LIBRARY_PACKAGE_TYPE);
	}

	@Override
	public String toString() {
		return artifact.toString();
	}

}
