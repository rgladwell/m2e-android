/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

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
