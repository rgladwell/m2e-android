/*******************************************************************************
 * Copyright (c) 2009, 2010 Ricardo Gladwell and Hugo Josefson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android;

import java.util.List;

import me.gladwell.eclipse.m2e.android.configuration.AndroidClasspathConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurer;
import me.gladwell.eclipse.m2e.android.model.AndroidProject;

import org.apache.maven.plugin.MojoExecution;
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
import org.eclipse.m2e.jdt.AbstractJavaProjectConfigurator;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IJavaProjectConfigurator;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AndroidMavenProjectConfigurator extends AbstractJavaProjectConfigurator implements IJavaProjectConfigurator {

	@Inject
	private AbstractProjectConfigurator javaProjectConfigurator;

	@Inject
	private List<ProjectConfigurer> projectConfigurers;

	@Inject
	private AndroidClasspathConfigurer classpathConfigurer;

	@Inject
	private AndroidProjectFactory androidProjectFactory;

	public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
		AndroidProject androidProject = androidProjectFactory.createAndroidProject(request.getMavenProject(), request.getProject());

		if(androidProject != null) {
			javaProjectConfigurator.configure(request, monitor);
			IProject project = request.getProject();
	
			try {
				for (ProjectConfigurer configurer : projectConfigurers) {
					if (configurer.canHandle(androidProject.getType(), project)) {
						configurer.configure(project, monitor);
					}
				}
			} catch (Exception e) {
				throw new CoreException(new Status(IStatus.ERROR, AndroidMavenPlugin.PLUGIN_ID, "error configuring project", e));
			}
		}
	}

	public AbstractBuildParticipant getBuildParticipant(IMavenProjectFacade projectFacade, MojoExecution execution, IPluginExecutionMetadata executionMetadata) {
		return null;
	}

	public void configureClasspath(IMavenProjectFacade facade, IClasspathDescriptor classpath, IProgressMonitor monitor) throws CoreException {
		super.configureClasspath(facade, classpath, monitor);
		final AndroidProject project = androidProjectFactory.createAndroidProject(facade.getMavenProject(), facade.getProject());
		try {
			classpathConfigurer.removeNonRuntimeDependencies(project, classpath);
			classpathConfigurer.addClassFoldersForProjectDependencies(project, classpath);
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, AndroidMavenPlugin.PLUGIN_ID, "error configuring project classpath", e));
		}
    }

	public void configureRawClasspath(ProjectConfigurationRequest request, IClasspathDescriptor classpath, IProgressMonitor monitor) throws CoreException {	 
		final AndroidProject project = androidProjectFactory.createAndroidProject(request.getMavenProject(), request.getProject());
		try {
			classpathConfigurer.addGenFolder(project, classpath);
			classpathConfigurer.modifySourceFolderOutput(project, classpath);
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, AndroidMavenPlugin.PLUGIN_ID, "error configuring project classpath", e));
		}
    }

}
