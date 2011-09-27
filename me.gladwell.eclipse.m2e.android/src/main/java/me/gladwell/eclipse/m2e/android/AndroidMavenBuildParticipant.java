/*******************************************************************************
 * Copyright (c) 2011 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import java.util.Set;

import me.gladwell.android.tools.AndroidBuildService;
import me.gladwell.android.tools.DexService;
import me.gladwell.android.tools.model.Jdk;

import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AndroidMavenBuildParticipant extends AbstractBuildParticipant implements BuildListenerRegistry {

	@Inject
	private DexService dexService;
	
	@Inject
	private AndroidBuildService buildService;

	private List<AndroidBuildListener> listeners = Collections.synchronizedList(new ArrayList<AndroidBuildListener>());

	@Override
	public Set<IProject> build(int kind, IProgressMonitor monitor) throws Exception {
		// TODO make this code thread-safe
		try {
			Jdk jdk = new Jdk();
			jdk.setPath(JavaRuntime.getDefaultVMInstall().getInstallLocation().getAbsoluteFile());
			buildService.setJdk(jdk);
	
			final MavenProject pom = getMavenProjectFacade().getMavenProject();
	
			if(AndroidMavenPluginUtil.getAndroidProjectType(pom) == null) {
				// TODO should never reach here, throw meaningful exception
				return null;
			}
	
			final IProject project = getMavenProjectFacade().getProject();
			
			final File apk = AndroidMavenPluginUtil.getApkFile(project);
	
			if(!apk.exists()) {
				// TODO should never reach here, throw meaningful exception
				return null;
			}
	
			// create new classes.dex in existing APK
			List<File> artifacts = new ArrayList<File>();
	
			for(String path : pom.getRuntimeClasspathElements()) {
				File artifact = new File(path);
				artifacts.add(artifact);
			}
	
			File outputDirectory = new File(getMavenProjectFacade().getMavenProject().getBuild().getDirectory(), "android-classes");
			File sourceDirectory = project.getWorkspace().getRoot().getFolder(JavaCore.create(project).getOutputLocation()).getLocation().toFile();
	
			buildService.unpack(outputDirectory, sourceDirectory, artifacts, false);
			dexService.convertClassFiles(apk, outputDirectory, apk);
			buildService.resign(apk);
	
			for(AndroidBuildListener listener : listeners) {
				listener.onBuild((new EventObject(this)));
			}
	
			return null;
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, AndroidMavenPlugin.PLUGIN_ID, "error buildiing android project", e));
		}
	}

	public void registerBuildListener(AndroidBuildListener listener) {
		listeners.add(listener);		
	}

}
