/*******************************************************************************
 * Copyright (c) 2009, 2010, 2011, 2012 Ricardo Gladwell and Hugo Josefson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android;

import java.util.List;

import me.gladwell.eclipse.m2e.android.configuration.AndroidClasspathConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.DependencyNotFoundInWorkspace;
import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurer;
import me.gladwell.eclipse.m2e.android.project.AndroidProject;
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
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
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

	@Inject
	private AbstractProjectConfigurator javaProjectConfigurator;

	@Inject
	private List<ProjectConfigurer> projectConfigurers;

	@Inject
	private AndroidClasspathConfigurer classpathConfigurer;

	@Inject
	private AndroidProjectFactory<MavenAndroidProject, MavenProject> mavenProjectFactory;

	@Inject
	private AndroidProjectFactory<EclipseAndroidProject, IProject> eclipseProjectFactory;

	public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
		markerManager.deleteMarkers(request.getPom(), AndroidMavenPlugin.APKLIB_ERROR_TYPE);

		try {
			final MavenAndroidProject mavenProject = mavenProjectFactory.createAndroidProject(request.getMavenProject());
			final EclipseAndroidProject eclipseProject = eclipseProjectFactory.createAndroidProject(request.getProject());

				if(mavenProject.isAndroidProject()) {
					javaProjectConfigurator.configure(request, monitor);
	
					for (ProjectConfigurer configurer : projectConfigurers) {
						try {
							if (configurer.isValid(mavenProject) && !configurer.isConfigured(eclipseProject)) {
								configurer.configure(eclipseProject,  mavenProject);
							}
						} catch (DependencyNotFoundInWorkspace e) {
							markerManager.addErrorMarkers(request.getPom(), e.getType(), e);
						}
					}
				}
		} catch (AndroidMavenException e) {
			throw new CoreException(new Status(IStatus.ERROR, AndroidMavenPlugin.PLUGIN_ID, "error configuring project", e));
		}
	}

	public AbstractBuildParticipant getBuildParticipant(IMavenProjectFacade projectFacade, MojoExecution execution, IPluginExecutionMetadata executionMetadata) {
		return null;
	}

	public void configureClasspath(IMavenProjectFacade facade, IClasspathDescriptor classpath, IProgressMonitor monitor) throws CoreException {
		final AndroidProject project = mavenProjectFactory.createAndroidProject(facade.getMavenProject());
		try {
			classpathConfigurer.removeNonRuntimeDependencies(project, classpath);
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, AndroidMavenPlugin.PLUGIN_ID, "error configuring project classpath", e));
		}
    }

	public void configureRawClasspath(ProjectConfigurationRequest request, IClasspathDescriptor classpath, IProgressMonitor monitor) throws CoreException {	 
		final AndroidProject project = mavenProjectFactory.createAndroidProject(request.getMavenProject());
		final IJavaProject javaProject = JavaCore.create(request.getProject());
		try {
			classpathConfigurer.addGenFolder(javaProject, project, classpath);
			classpathConfigurer.modifySourceFolderOutput(javaProject, project, classpath);
			classpathConfigurer.removeJreClasspathContainer(classpath);
			if(!project.isLibrary()) {
			    classpathConfigurer.markMavenContainerExported(classpath);
			}
            classpathConfigurer.markAndroidContainerNotExported(classpath);
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, AndroidMavenPlugin.PLUGIN_ID, "error configuring project classpath", e));
		}
    }

}
