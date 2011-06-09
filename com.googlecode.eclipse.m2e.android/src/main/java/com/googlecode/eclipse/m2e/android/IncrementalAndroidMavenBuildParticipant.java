/*******************************************************************************
 * Copyright (c) 2011 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.googlecode.eclipse.m2e.android;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;

import com.github.android.tools.CommandLineAndroidTools;
import com.github.android.tools.DexService;


public class IncrementalAndroidMavenBuildParticipant extends AbstractBuildParticipant {

	private DexService dexService = new CommandLineAndroidTools();

	@Override
	public Set<IProject> build(int kind, IProgressMonitor monitor) throws Exception {
		final MavenProject pom = getMavenProjectFacade().getMavenProject();

		if(AndroidMavenPluginUtil.getAndroidProjectType(pom) == null) {
			// TODO should never reach here, throw meaningful exception
			return null;
		}
		
		final IProject project = getMavenProjectFacade().getProject();
		final File apk = AndroidMavenPluginUtil.getApkFile(project);

		if(!apk.exists()) {
			// TODO should never reach here, throw meaningful exception
		}

//		final IResourceDelta delta = getDelta(project);
		final List<File> artifacts = new ArrayList<File>();

		// determine if POM dependencies have changed, or if SNAPSHOTS have updated since last build, if so:
		boolean modifiedDependencies = false;

		for(String path : pom.getRuntimeClasspathElements()) {
			File artifact = new File(path);
			artifacts.add(artifact);
			if(artifact.lastModified() > apk.lastModified()) {
				modifiedDependencies = true;
			}
		}

		if(modifiedDependencies || IncrementalProjectBuilder.FULL_BUILD == kind || IncrementalProjectBuilder.CLEAN_BUILD == kind) {
			// create new classes.dex in existing APK
			artifacts.add(apk);
			dexService.convertClassFiles(apk, artifacts.toArray(new File[artifacts.size()]));
			// TODO regenerate classes.dex security signature if enabled
		}

		return null;
	}

}
