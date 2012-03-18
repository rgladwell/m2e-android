/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.gladwell.eclipse.m2e.android.AndroidMavenException;
import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.m2e.core.internal.IMavenConstants;

public class OrderBuildersProjectConfigurer implements ProjectConfigurer {

	public static final String APK_BUILDER_COMMAND_NAME = "com.android.ide.eclipse.adt.ApkBuilder";

	public boolean isConfigured(EclipseAndroidProject project) {
		return false;
	}

	public boolean isValid(MavenAndroidProject androidProject) {
		return true;
	}

	public void configure(EclipseAndroidProject eclipseProject, MavenAndroidProject mavenProject) {
		try {
			IProjectDescription description = eclipseProject.getProject().getDescription();
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
			eclipseProject.getProject().setDescription(description, null);	
		} catch (CoreException e) {
			throw new ProjectConfigurationException(e);
		}
	}

}
