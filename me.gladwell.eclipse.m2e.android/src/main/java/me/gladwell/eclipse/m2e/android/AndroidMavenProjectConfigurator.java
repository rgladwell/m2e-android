/*******************************************************************************
 * Copyright (c) 2009 - 2014 Ricardo Gladwell and Hugo Josefson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android;

import java.util.List;

import me.gladwell.eclipse.m2e.android.configuration.DependencyNotFoundInWorkspace;
import me.gladwell.eclipse.m2e.android.configuration.classpath.ClasspathConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.classpath.RawClasspathConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.workspace.WorkspaceConfigurer;
import me.gladwell.eclipse.m2e.android.project.AdtEclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.project.AndroidProjectFactory;
import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IJavaProjectConfigurator;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AndroidMavenProjectConfigurator extends AbstractProjectConfigurator implements IJavaProjectConfigurator {

    @Inject private AbstractProjectConfigurator javaProjectConfigurator;

    @Inject private List<WorkspaceConfigurer> workspaceConfigurers;

    @Inject private List<RawClasspathConfigurer> rawClasspathConfigurers;

    @Inject private List<ClasspathConfigurer> classpathConfigurers;

    @Inject private AndroidProjectFactory<MavenAndroidProject, MavenProject> mavenProjectFactory;

    @Inject private AndroidProjectFactory<EclipseAndroidProject, IProject> eclipseProjectFactory;

    public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
        markerManager.deleteMarkers(request.getPom(), AndroidMavenPlugin.APKLIB_ERROR_TYPE);

        try {
            final MavenAndroidProject mavenProject = mavenProjectFactory
                    .createAndroidProject(request.getMavenProject());
            final EclipseAndroidProject eclipseProject = eclipseProjectFactory.createAndroidProject(request
                    .getProject());

            if (mavenProject.isAndroidProject()) {
                javaProjectConfigurator.configure(request, monitor);

                for (WorkspaceConfigurer configurer : workspaceConfigurers) {
                    try {
                        if (configurer.isValid(mavenProject) && !configurer.isConfigured(eclipseProject)) {
                            configurer.configure(eclipseProject, mavenProject);
                        }
                    } catch (DependencyNotFoundInWorkspace e) {
                        markerManager.addErrorMarkers(request.getPom(), e.getType(), e);
                    }
                }
            }
        } catch (AndroidMavenException e) {
            throw new CoreException(new Status(IStatus.ERROR, AndroidMavenPlugin.PLUGIN_ID,
                    "error configuring project", e));
        }
    }

    public AbstractBuildParticipant getBuildParticipant(IMavenProjectFacade f, MojoExecution e,
            IPluginExecutionMetadata m) {
        return null;
    }

    public void configureClasspath(IMavenProjectFacade facade, IClasspathDescriptor classpath, IProgressMonitor monitor)
            throws CoreException {
        final MavenAndroidProject project = mavenProjectFactory.createAndroidProject(facade.getMavenProject());
        try {
            for (RawClasspathConfigurer configurer : rawClasspathConfigurers) {
                configurer.configure(project, classpath);
            }
        } catch (Exception e) {
            throw new CoreException(new Status(IStatus.ERROR, AndroidMavenPlugin.PLUGIN_ID,
                    "error configuring project classpath", e));
        }
    }

    public void configureRawClasspath(ProjectConfigurationRequest request, IClasspathDescriptor classpath,
            IProgressMonitor monitor) throws CoreException {
        final MavenAndroidProject mavenProject = mavenProjectFactory.createAndroidProject(request.getMavenProject());
        final EclipseAndroidProject eclipseProject = new AdtEclipseAndroidProject(request.getProject(), classpath);
        try {
            for (ClasspathConfigurer classpathConfigurer : classpathConfigurers) {
                if (classpathConfigurer.shouldApplyTo(mavenProject)) {
                    classpathConfigurer.configure(mavenProject, eclipseProject);
                }
            }
        } catch (Exception e) {
            throw new CoreException(new Status(IStatus.ERROR, AndroidMavenPlugin.PLUGIN_ID,
                    "error configuring project classpath", e));
        }
    }

}
