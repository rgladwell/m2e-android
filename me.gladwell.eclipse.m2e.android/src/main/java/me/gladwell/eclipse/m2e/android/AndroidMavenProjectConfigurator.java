/*******************************************************************************
 * Copyright (c) 2009, 2010 Ricardo Gladwell and Hugo Josefson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import me.gladwell.eclipse.m2e.android.model.ProjectType;
import me.gladwell.eclipse.m2e.inject.GuiceProjectConfigurator;

import org.apache.maven.plugin.MojoExecution;
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
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IJavaProjectConfigurator;

import com.android.ide.eclipse.adt.AdtConstants;
import com.android.ide.eclipse.adt.internal.project.ProjectHelper;
import com.android.ide.eclipse.adt.internal.sdk.ProjectState;
import com.android.ide.eclipse.adt.internal.sdk.Sdk;
import com.google.inject.Module;

public class AndroidMavenProjectConfigurator extends GuiceProjectConfigurator implements IJavaProjectConfigurator {

	public static final String APK_BUILDER_COMMAND_NAME = "com.android.ide.eclipse.adt.ApkBuilder";
	public static final String ANDROID_GEN_PATH = "gen";
	
	@Inject
	private AbstractProjectConfigurator javaProjectConfigurator;

	@Inject
	private AbstractBuildParticipant incrementalAndroidMavenBuildParticipant;

	@Override
	protected void addModules(List<Module> modules) {
		super.addModules(modules);
		modules.add(new PluginModule());
	}

	@Override
	public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
		javaProjectConfigurator.configure(request, monitor);

		ProjectType type = AndroidMavenPluginUtil.getAndroidProjectType(request.getMavenProject());
		
		if (type != null) {
			IProject project = request.getProject();
			ProjectHelper.fixProject(project);

			if (!project.hasNature(AdtConstants.NATURE_DEFAULT)) {
				addNature(project, AdtConstants.NATURE_DEFAULT, monitor);
			}

			orderBuilders(monitor, project);

			ProjectState state = Sdk.getProjectState(project);
			if(type == ProjectType.Library && !state.isLibrary()) {
			    convertToLibraryProject(project);
				state.reloadProperties();
			}
		}
	}

	@Override
	public AbstractBuildParticipant getBuildParticipant(IMavenProjectFacade projectFacade, MojoExecution execution, IPluginExecutionMetadata executionMetadata) {
		if(execution.getGoal().equals("generate-sources")) {
			return incrementalAndroidMavenBuildParticipant;
		}
		return super.getBuildParticipant(projectFacade, execution, executionMetadata);
	}

	public void configureClasspath(IMavenProjectFacade facade, IClasspathDescriptor classpath, IProgressMonitor monitor) throws CoreException {
    }

	public void configureRawClasspath(ProjectConfigurationRequest request, IClasspathDescriptor classpath, IProgressMonitor monitor) throws CoreException {	 
		final IJavaProject javaProject = JavaCore.create(request.getProject());
	    final IPath genPath = javaProject.getPath().append(ANDROID_GEN_PATH);

	    if(!classpath.containsPath(genPath)) {
	    	classpath.addSourceEntry(genPath, AndroidMavenPluginUtil.getAndroidClassesOutputFolder(javaProject), true);
	    }
    }

	private void orderBuilders(IProgressMonitor monitor, IProject project) throws CoreException {
		// ensure APKBuilder build command is before maven build command 
		IProjectDescription description = project.getDescription();
		List<ICommand> buildCommands = Arrays.asList(description.getBuildSpec());
		
		Collections.sort(buildCommands, new Comparator<ICommand>() {
			public int compare(ICommand command1, ICommand command2) {
				if(IMavenConstants.BUILDER_ID.equals(command1.getBuilderName()) && APK_BUILDER_COMMAND_NAME.equals(command2.getBuilderName())) {
					return 1;
				} else if(APK_BUILDER_COMMAND_NAME.equals(command1.getBuilderName()) && IMavenConstants.BUILDER_ID.equals(command2.getBuilderName())) {
					return -1;
				}

				return 0;
			}
		});

		ICommand[] buildSpec = buildCommands.toArray(new ICommand[0]);
		description.setBuildSpec(buildSpec);
		project.setDescription(description, monitor);
	}

	private void convertToLibraryProject(IProject project) throws CoreException {
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
	}

}
