/*******************************************************************************
 * Copyright (c) 2014 David Carver
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration.classpath;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Resource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IClasspathManager;

import com.android.ide.eclipse.adt.AdtConstants;

import me.gladwell.eclipse.m2e.android.AndroidMavenPlugin;
import me.gladwell.eclipse.m2e.android.Log;
import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

@SuppressWarnings("restriction")
public class ReorderClasspathConfigurer implements RawClasspathConfigurer {

	private MavenAndroidProject mavenAndroidProject;
	protected IClasspathEntry[] originalClasspathEntries;
	protected ArrayList<IClasspathEntry> arrayEntries;

	public boolean shouldApplyTo(MavenAndroidProject project) {
		return true;
	}

	public void configure(MavenAndroidProject mavenAndroidProject, EclipseAndroidProject eclipseProject,
			IClasspathDescriptor classpath) {

		this.mavenAndroidProject = mavenAndroidProject;
		arrayEntries = new ArrayList<IClasspathEntry>();

		IJavaProject javaproject = JavaCore.create(eclipseProject.getProject());

		originalClasspathEntries = javaproject.readRawClasspath();

		reorderClasspathEntries();

		rewriteClasspathFile(javaproject);
	}

	private void rewriteClasspathFile(IJavaProject javaproject) {
		IClasspathEntry[] newEntries = new IClasspathEntry[arrayEntries.size()];
		arrayEntries.toArray(newEntries);

		try {
			javaproject.setRawClasspath(newEntries, new NullProgressMonitor());
			javaproject.save(new NullProgressMonitor(), false);
		} catch (Exception ex) {
			Log.warn(ex.getMessage());
		}
	}

	private void reorderClasspathEntries() {
		addMavenSourceFolder();
		addMavenResourceFolder();
		addMavenTestSourceFolder();
		addMavenTestResourceFolder();
		addAndroidFrameworkContainer();
		addAndroidPrivateLibrariesContainer();
		addAndroidDependenciesContainer();
		addMavenDependenciesContainer();
		addNonRuntimeContainer();
		addTheRest();
	}

	protected void addMavenSourceFolder() {

		List<String> sourceRoots = mavenAndroidProject.getSourcePaths();

		for (String sourceRoot : sourceRoots) {
			IClasspathEntry newEntry = findClasspathEntry(sourceRoot);
			addEntry(newEntry);
		}
	}

	protected void addMavenResourceFolder() {

		List<Resource> resources = mavenAndroidProject.getResources();

		for (Resource res : resources) {
			String directory = res.getDirectory();
			IClasspathEntry newEntry = findClasspathEntry(directory);
			addEntry(newEntry);
		}
	}

	protected void addMavenTestSourceFolder() {
		List<String> sourceRoots = mavenAndroidProject.getTestSourcePaths();

		for (String sourceRoot : sourceRoots) {
			IClasspathEntry newEntry = findClasspathEntry(sourceRoot);
			addEntry(newEntry);
		}
	}

	protected void addMavenTestResourceFolder() {
		List<Resource> resources = mavenAndroidProject.getTestResources();

		for (Resource res : resources) {
			String directory = res.getDirectory();
			IClasspathEntry newEntry = findClasspathEntry(directory);
			addEntry(newEntry);
		}
	}

	protected void addAndroidFrameworkContainer() {
		IClasspathEntry entry = findClasspathEntry(AdtConstants.CONTAINER_FRAMEWORK);
		addEntry(entry);
	}

	protected void addAndroidPrivateLibrariesContainer() {
		IClasspathEntry entry = findClasspathEntry(AdtConstants.CONTAINER_PRIVATE_LIBRARIES);
		addEntry(entry);
	}

	protected void addAndroidDependenciesContainer() {
		IClasspathEntry entry = findClasspathEntry(AdtConstants.CONTAINER_DEPENDENCIES);
		addEntry(entry);
	}

	protected void addMavenDependenciesContainer() {
		IClasspathEntry entry = findClasspathEntry(IClasspathManager.CONTAINER_ID);
		addEntry(entry);
	}

	protected void addNonRuntimeContainer() {
		IClasspathEntry entry = findClasspathEntry(AndroidMavenPlugin.CONTAINER_NONRUNTIME_DEPENDENCIES);
		addEntry(entry);
	}

	protected void addTheRest() {
		for (IClasspathEntry classpathEntry : originalClasspathEntries) {
			if (!hasSourceFolder(classpathEntry) && !hasResource(classpathEntry)
					&& !hasTestSourceFolder(classpathEntry) && !hasTestResource(classpathEntry)
					&& !hasAndroidFramework(classpathEntry) && !hasMavenDependencies(classpathEntry)
					&& !hasAndroidLibraries(classpathEntry) && !hasAndroidDependencies(classpathEntry)
					&& !hasNonRuntimeDependencies(classpathEntry)) {
				addEntry(classpathEntry);
			}
		}
	}

	protected boolean hasSourceFolder(IClasspathEntry entry) {
		List<String> sourceRoots = mavenAndroidProject.getSourcePaths();

		for (String sourceRoot : sourceRoots) {
			if (sourceRoot.contains(entry.getPath().toOSString())) {
				return true;
			}
		}
		return false;
	}

	protected boolean hasAndroidFramework(IClasspathEntry entry) {
		String path = entry.getPath().toString();
		for (IClasspathEntry arrayEntry : arrayEntries) {
			if (path.equals(AdtConstants.CONTAINER_FRAMEWORK) && arrayEntry.equals(entry)) {
				return true;
			}
		}
		return false;
	}

	protected boolean hasMavenDependencies(IClasspathEntry entry) {
		String path = entry.getPath().toString();
		for (IClasspathEntry arrayEntry : arrayEntries) {
			if (path.equals(IClasspathManager.CONTAINER_ID) && arrayEntry.equals(entry)) {
				return true;
			}
		}
		return false;
	}

	protected boolean hasNonRuntimeDependencies(IClasspathEntry entry) {
		String path = entry.getPath().toString();
		for (IClasspathEntry arrayEntry : arrayEntries) {
			if (path.equals(AndroidMavenPlugin.CONTAINER_NONRUNTIME_DEPENDENCIES) && arrayEntry.equals(entry)) {
				return true;
			}
		}
		return false;
	}

	protected boolean hasAndroidLibraries(IClasspathEntry entry) {
		String path = entry.getPath().toString();
		for (IClasspathEntry arrayEntry : arrayEntries) {
			if (path.equals(AdtConstants.CONTAINER_PRIVATE_LIBRARIES) && arrayEntry.equals(entry)) {
				return true;
			}
		}
		return false;
	}

	protected boolean hasAndroidDependencies(IClasspathEntry entry) {
		String path = entry.getPath().toString();
		for (IClasspathEntry arrayEntry : arrayEntries) {
			if (path.equals(AdtConstants.CONTAINER_DEPENDENCIES) && arrayEntry.equals(entry)) {
				return true;
			}
		}
		return false;
	}

	protected boolean hasTestSourceFolder(IClasspathEntry entry) {
		List<String> sourceRoots = mavenAndroidProject.getTestSourcePaths();

		for (String sourceRoot : sourceRoots) {
			if (sourceRoot.contains(entry.getPath().toOSString())) {
				return true;
			}
		}
		return false;
	}

	protected boolean hasResource(IClasspathEntry entry) {
		List<Resource> resources = mavenAndroidProject.getResources();
		for (Resource resource : resources) {
			if (resource.getDirectory().contains(entry.getPath().toOSString())) {
				return true;
			}
		}
		return false;
	}

	protected boolean hasTestResource(IClasspathEntry entry) {
		List<Resource> resources = mavenAndroidProject.getTestResources();
		for (Resource resource : resources) {
			if (resource.getDirectory().contains(entry.getPath().toOSString())) {
				return true;
			}
		}
		return false;
	}

	protected void addEntry(IClasspathEntry entry) {
		if (entry != null) {
			arrayEntries.add(entry);
		}
	}

	private IClasspathEntry findClasspathEntry(String substring) {
		for (IClasspathEntry classpathEntry : originalClasspathEntries) {
			String path = classpathEntry.getPath().toOSString();
			if (substring.contains(path)) {
				return classpathEntry;
			}
		}
		return null;
	}

}
