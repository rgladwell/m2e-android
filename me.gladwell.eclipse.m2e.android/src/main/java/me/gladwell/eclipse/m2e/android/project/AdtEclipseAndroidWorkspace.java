/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import java.util.ArrayList;
import java.util.List;

import me.gladwell.eclipse.m2e.android.configuration.DependencyNotFoundInWorkspace;
import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.m2e.core.embedder.MavenModelManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AdtEclipseAndroidWorkspace implements AndroidWorkspace {

    private IWorkspace workspace;
    private AndroidProjectFactory<EclipseAndroidProject, IProject> projectFactory;
    private MavenModelManager mavenModelManager;

    @Inject
    public AdtEclipseAndroidWorkspace(IWorkspace workspace,
            AndroidProjectFactory<EclipseAndroidProject, IProject> projectFactory,
            MavenModelManager mavenModelManager) {
        super();
        this.workspace = workspace;
        this.projectFactory = projectFactory;
        this.mavenModelManager = mavenModelManager;
    }

    // TODO delegate workspace dep resolution to EclipseWorkspaceArtifactRepository
    public EclipseAndroidProject findOpenWorkspaceDependency(Dependency dependency) {
        for (IProject project : workspace.getRoot().getProjects()) {
            if (!project.isOpen()) {
                continue;
            }
            EclipseAndroidProject androidProject = projectFactory.createAndroidProject(project);
            if (androidProject.isMavenised()) {
                MavenProject mavenProject;
                try {
                    mavenProject = mavenModelManager.readMavenProject(androidProject.getPom(), null);
                } catch (CoreException e) {
                    throw new ProjectConfigurationException(e);
                }

                if (StringUtils.equals(dependency.getName(), project.getName())
                        && StringUtils.equals(dependency.getGroup(), mavenProject.getGroupId())
                        && dependency.getVersion().equals(mavenProject.getVersion())) {
                    return androidProject;
                }
            }
        }

        throw new DependencyNotFoundInWorkspace(dependency);
    }

    public List<EclipseAndroidProject> findOpenWorkspaceDependencies(List<Dependency> dependencies) {
        List<EclipseAndroidProject> openWorkspaceDependencies = new ArrayList<EclipseAndroidProject>();

        for (Dependency dependency : dependencies) {
            EclipseAndroidProject workspaceDependency = null;

            try {
                workspaceDependency = findOpenWorkspaceDependency(dependency);
            } catch (DependencyNotFoundInWorkspace e) {
                continue;
            }

            openWorkspaceDependencies.add(workspaceDependency);
        }

        return openWorkspaceDependencies;
    }

}
