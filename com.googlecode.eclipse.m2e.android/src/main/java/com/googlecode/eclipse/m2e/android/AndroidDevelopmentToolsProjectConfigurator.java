/*******************************************************************************
 * Copyright (c) 2009, 2010 Ricardo Gladwell and Hugo Josefson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.googlecode.eclipse.m2e.android;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IJavaProjectConfigurator;

import com.android.ide.eclipse.adt.AndroidConstants;
import com.android.ide.eclipse.adt.internal.sdk.ProjectState;
import com.android.ide.eclipse.adt.internal.sdk.Sdk;
import com.googlecode.eclipse.m2e.android.model.AndroidProjectType;

public class AndroidDevelopmentToolsProjectConfigurator extends AbstractProjectConfigurator implements IJavaProjectConfigurator {

	public static final String APK_BUILDER_COMMAND_NAME = "com.android.ide.eclipse.adt.ApkBuilder";
	private static final String ANDROID_GEN_PATH = "gen";

	@Override
	public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
		AndroidProjectType type = AndroidMavenPluginUtil.getAndroidProjectType(request.getMavenProject());
		if (type != null) {
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

			ProjectState state = Sdk.getProjectState(project);
			if(type == AndroidProjectType.AndroidLibrary && !state.isLibrary()) {
			    IFile defaultProperties = project.getFile("default.properties");
			    BufferedWriter writer = null;
			    try {
			    	writer = new BufferedWriter( new FileWriter(new File(defaultProperties.getRawLocation().toOSString())) );
			    	writer.newLine();
					writer.append("android.library=true");
				} catch (FileNotFoundException e) {
					throw new CoreException(new Status(IStatus.ERROR, AndroidMavenPlugin.PLUGIN_ID, "cannot find default.propertie file", e));
				} catch (IOException e) {
					throw new CoreException(new Status(IStatus.ERROR, AndroidMavenPlugin.PLUGIN_ID, "cannot read default.propertie file", e));
				} finally {
					if(writer != null) {
						try {
							writer.close();
						} catch (IOException e) {
							throw new CoreException(new Status(IStatus.WARNING, AndroidMavenPlugin.PLUGIN_ID, "error closing file", e));
						}
					}
				}
				state.reloadProperties();
			}
		}
	}

//	@Override
//    public AbstractBuildParticipant getBuildParticipant(MojoExecution execution) {
//		if(execution.getGoal().equals("compile")) {
//			return new AndroidMavenBuildParticipant();
//		}
//	    return super.getBuildParticipant(execution);
//    }

	public void configureClasspath(IMavenProjectFacade facade, IClasspathDescriptor classpath, IProgressMonitor monitor) throws CoreException {
    }

	public void configureRawClasspath(ProjectConfigurationRequest request, IClasspathDescriptor classpath, IProgressMonitor monitor) throws CoreException {	 
		IJavaProject javaProject = JavaCore.create(request.getProject());

	    IPath genPath = javaProject.getPath().append(ANDROID_GEN_PATH);

	    if(!classpath.containsPath(genPath)) {
	    	final File genFolder = genPath.toFile();
	    	if(!genFolder.exists()) {
	    		if(!genFolder.mkdirs()) {
	    			// TODO throw exception
	    		}
	    	}

	    	classpath.addSourceEntry(genPath, AndroidMavenPluginUtil.getAndroidClassesOutputFolder(javaProject), true);
	    }
    }

}
