/*******************************************************************************
 * Copyright (c) 2009, 2010 Ricardo Gladwell and Hugo Josefson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.urbanmania.eclipse.maven.android;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.maven.ide.eclipse.MavenPlugin;
import org.maven.ide.eclipse.core.IMavenConstants;
import org.maven.ide.eclipse.embedder.IMaven;
import org.maven.ide.eclipse.project.IMavenProjectFacade;
import org.maven.ide.eclipse.project.MavenProjectManager;
import org.maven.ide.eclipse.project.ResolverConfiguration;
import org.maven.ide.eclipse.project.configurator.AbstractBuildParticipant;

import com.android.ide.eclipse.adt.AndroidConstants;
import com.android.ide.eclipse.adt.internal.project.ApkInstallManager;

public class AndroidMavenBuildParticipant extends AbstractBuildParticipant {

	MojoExecution execution;

	public AndroidMavenBuildParticipant (MojoExecution execution) {
		this.execution = execution;
	}

	@Override
	public Set<IProject> build(int kind, IProgressMonitor monitor) throws Exception {
		final IProject project = getMavenProjectFacade().getProject();
		if(IncrementalProjectBuilder.AUTO_BUILD == kind || IncrementalProjectBuilder.CLEAN_BUILD == kind || IncrementalProjectBuilder.FULL_BUILD == kind) {
			try{
				MavenPlugin plugin = MavenPlugin.getDefault();
				MavenProjectManager projectManager = plugin.getMavenProjectManager();
				IMaven maven = plugin.getMaven();
				IFile pom = project.getFile(new Path(IMavenConstants.POM_FILE_NAME));
				IMavenProjectFacade projectFacade = projectManager.create(pom, false, monitor);
				ResolverConfiguration resolverConfiguration = projectFacade.getResolverConfiguration();
				MavenExecutionRequest request = projectManager.createExecutionRequest(pom, resolverConfiguration, monitor);

				List<String> goals = new ArrayList<String>();
				goals.add("package");
				request.setGoals(goals);

				Properties properties = request.getUserProperties();
				properties.setProperty("maven.test.skip", "true");
				request.setUserProperties(properties);

				MavenExecutionResult executionResult = maven.execute(request, monitor);

				if (executionResult.hasExceptions()){
					List<Throwable> exceptions = executionResult.getExceptions();
					for (Throwable throwable : exceptions) {
						// TODO report failed build
						throwable.printStackTrace();
					}
				} else {
					Artifact apkArtifact = executionResult.getProject().getArtifact();
					if (AndroidConstants.EXT_ANDROID_PACKAGE.equals(apkArtifact.getType())) {
						IFileSystem fileSystem = EFS.getLocalFileSystem();
						IFileStore source = fileSystem.fromLocalFile(apkArtifact.getFile());
						IFileStore destination = fileSystem.fromLocalFile(AndroidMavenPluginUtil.getApkFile(project));
						source.copy(destination, EFS.OVERWRITE, monitor);
						// reset the installation manager to force new installs of this project
						ApkInstallManager.getInstance().resetInstallationFor(project);
					}
				}

			} finally {
				project.refreshLocal(IProject.DEPTH_INFINITE, monitor);
			}
		} else {
			// reset the installation manager to force new installs of this project
			ApkInstallManager.getInstance().resetInstallationFor(project);
		}
		return null;
	}

}
