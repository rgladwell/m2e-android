/*******************************************************************************
 * Copyright (c) 2009, 2010 Ricardo Gladwell and Hugo Josefson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.googlecode.eclipse.m2e.android;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.maven.ide.eclipse.MavenPlugin;
import org.maven.ide.eclipse.core.IMavenConstants;
import org.maven.ide.eclipse.embedder.IMaven;
import org.maven.ide.eclipse.jdt.IClasspathDescriptor;
import org.maven.ide.eclipse.jdt.IJavaProjectConfigurator;
import org.maven.ide.eclipse.project.IMavenProjectFacade;
import org.maven.ide.eclipse.project.MavenProjectManager;
import org.maven.ide.eclipse.project.configurator.AbstractBuildParticipant;
import org.maven.ide.eclipse.project.configurator.AbstractProjectConfigurator;
import org.maven.ide.eclipse.project.configurator.ProjectConfigurationRequest;

import com.android.ide.eclipse.adt.AndroidConstants;

public class AndroidDevelopmentToolsProjectConfigurator extends AbstractProjectConfigurator implements IJavaProjectConfigurator {

	public static final String APK_BUILDER_COMMAND_NAME = "com.android.ide.eclipse.adt.ApkBuilder";
	private static final String ANDROID_GEN_PATH = "gen";

	@Override
	public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
		if (AndroidMavenPluginUtil.isAndroidProject(request.getMavenProject())) {
			IProject project = request.getProject();

			if (!project.hasNature(AndroidConstants.NATURE_DEFAULT)) {
				addNature(project, AndroidConstants.NATURE_DEFAULT, monitor);
			}

			// issue 6: remove redundant APKBuilder build command 
			IProjectDescription description = project.getDescription();
			List<ICommand> buildCommands = new LinkedList<ICommand>();
			for(ICommand command : description.getBuildSpec()) {
				if(!APK_BUILDER_COMMAND_NAME.equals(command.getBuilderName())) {
					buildCommands.add(command);
				}
			}

			ICommand[] buildSpec = buildCommands.toArray(new ICommand[0]);
			description.setBuildSpec(buildSpec);
			project.setDescription(description, monitor);

			IJavaProject javaProject = JavaCore.create(project);
			// set output location to target/android-classes so APK blob is not including in APK resources
			javaProject.setOutputLocation(AndroidMavenPluginUtil.getAndroidClassesOutputFolder(javaProject), monitor);
		}
	}

	@Override
    public AbstractBuildParticipant getBuildParticipant(MojoExecution execution) {
		if(execution.getGoal().equals("compile")) {
			return new AndroidMavenBuildParticipant(execution);
		}
	    return super.getBuildParticipant(execution);
    }

	public void configureClasspath(IMavenProjectFacade facade, IClasspathDescriptor classpath, IProgressMonitor monitor) throws CoreException {
    }

	public void configureRawClasspath(ProjectConfigurationRequest request, IClasspathDescriptor classpath, IProgressMonitor monitor) throws CoreException {	 
		IJavaProject javaProject = JavaCore.create(request.getProject());

	    IPath genPath = javaProject.getPath().append(ANDROID_GEN_PATH);

	    if(!classpath.containsPath(genPath)) {
	    	final File genFolder = genPath.toFile();
	    	if(!genFolder.exists()) {
	    		genFolder.mkdirs();
	    	}

	    	classpath.addSourceEntry(genPath, AndroidMavenPluginUtil.getAndroidClassesOutputFolder(javaProject), true);
	    }
    }

}
