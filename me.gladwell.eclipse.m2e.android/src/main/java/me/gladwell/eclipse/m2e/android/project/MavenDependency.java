package me.gladwell.eclipse.m2e.android.project;

import static me.gladwell.eclipse.m2e.android.project.JaywayMavenAndroidProject.ANDROID_LIBRARY_PACKAGE_TYPE;

import org.apache.maven.artifact.Artifact;

/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

public class MavenDependency implements Dependency {

	private String groupId;
	private String artifactId;
	private String type;
	private String version;

	public MavenDependency(Artifact artifact) {
		groupId = artifact.getGroupId();
		artifactId = artifact.getArtifactId();
		version = artifact.getVersion();
		type = artifact.getType();
	}
	
	public MavenDependency(org.apache.maven.model.Dependency dependency) {
		groupId = dependency.getGroupId();
		artifactId = dependency.getArtifactId();
		type = dependency.getType();
		version = dependency.getVersion();
	}

	public MavenDependency(String groupId, String artifactId, String type, String version) {
		super();
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.type = type;
		this.version = version;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getGroupId() {
		return groupId;
	}
	
	public String getType() {
		return type;
	}

	public String getVersion() {
		return version;
	}

	public boolean isLibrary() {
		return type.equals(ANDROID_LIBRARY_PACKAGE_TYPE);
	}

	@Override
	public String toString() {
		return groupId + ":" + artifactId + ":" + type + ":" + version;
	}

}
