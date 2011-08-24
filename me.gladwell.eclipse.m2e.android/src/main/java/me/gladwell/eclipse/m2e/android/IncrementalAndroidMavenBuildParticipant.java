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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.gladwell.android.tools.AndroidBuildService;
import me.gladwell.android.tools.DexService;
import me.gladwell.android.tools.model.Jdk;
import me.gladwell.eclipse.m2e.android.model.MavenArtifact;

import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.jdt.IClasspathManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class IncrementalAndroidMavenBuildParticipant extends AbstractBuildParticipant implements BuildListenerRegistry {

	@Inject
	private DexService dexService;
	
	@Inject
	private AndroidBuildService buildService;

	private List<AndroidBuildListener> listeners = Collections.synchronizedList(new ArrayList<AndroidBuildListener>());

	private Map<String, Set<MavenArtifact>> lastMavenClasspaths = new HashMap<String, Set<MavenArtifact>>();

	@Override
	public Set<IProject> build(int kind, IProgressMonitor monitor) throws Exception {
		// TODO make this code thread-safe
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

		Set<MavenArtifact> mavenClasspath = convertToMavenArtifacts(project);
		boolean modifiedDependencies = false;

		synchronized(this) {
			Set<MavenArtifact> lastMavenClasspath = lastMavenClasspaths.get(project.getName());
			if(!mavenClasspath.equals(lastMavenClasspath)) {
				modifiedDependencies = true;
			}
			lastMavenClasspaths.put(project.getName(), mavenClasspath);
		}

		if(modifiedDependencies || IncrementalProjectBuilder.FULL_BUILD == kind || IncrementalProjectBuilder.CLEAN_BUILD == kind) {
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
		}
		return null;
	}

	private Set<MavenArtifact> convertToMavenArtifacts(IProject project) throws JavaModelException {
		IJavaProject javaProject = JavaCore.create(project);
		IClasspathContainer container = null;
		IClasspathEntry[] entries = javaProject.getRawClasspath();

		for (int i = 0; i < entries.length; i++) {
			IClasspathEntry entry = entries[i];
			if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER && entry.getPath() != null && entry.getPath().segmentCount() > 0
			        && IClasspathManager.CONTAINER_ID.equals(entry.getPath().segment(0))) {
				container = JavaCore.getClasspathContainer(entry.getPath(),	javaProject);
			}
		}

		IClasspathEntry[] classpathEntries = container.getClasspathEntries();
		Set<File> artifacts = new HashSet<File>();

		for(IClasspathEntry entry : classpathEntries) {
			artifacts.add(entry.getPath().toFile());
		}
		
		Set<MavenArtifact> results = new HashSet<MavenArtifact>();
		for(File artifact : artifacts) {
			MavenArtifact mavenArtifact = new MavenArtifact();
			mavenArtifact.setLastModified(artifact.lastModified());
			mavenArtifact.setPath(artifact.getAbsolutePath());
			results.add(mavenArtifact);
		}
		return results;
	}

	public void registerBuildListener(AndroidBuildListener listener) {
		listeners.add(listener);		
	}

}
