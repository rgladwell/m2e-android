/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;

import org.apache.maven.project.MavenProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.m2e.core.embedder.MavenModelManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MavenToEclipseAndroidProjectConverter implements
        AndroidProjectFactory<MavenAndroidProject, IDEAndroidProject> {

    private AndroidProjectFactory<MavenAndroidProject, MavenProject> mavenProjectFactory;
    private MavenModelManager mavenModelManager;

    @Inject
    public MavenToEclipseAndroidProjectConverter(
            AndroidProjectFactory<MavenAndroidProject, MavenProject> mavenProjectFactory,
            MavenModelManager mavenModelManager) {
        super();
        this.mavenProjectFactory = mavenProjectFactory;
        this.mavenModelManager = mavenModelManager;
    }

    public MavenAndroidProject createAndroidProject(IDEAndroidProject androidProject) {
        MavenProject project;
        try {
            project = mavenModelManager.readMavenProject(androidProject.getPom(), null);
        } catch (CoreException e) {
            throw new ProjectConfigurationException(e);
        }
        return mavenProjectFactory.createAndroidProject(project);
    }

}
