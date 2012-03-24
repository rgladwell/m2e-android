/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import java.util.List;

import me.gladwell.eclipse.m2e.android.project.AndroidProject;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IClasspathDescriptor.EntryFilter;
import org.eclipse.m2e.jdt.IClasspathEntryDescriptor;
import org.eclipse.m2e.jdt.IClasspathManager;

public class MavenAndroidClasspathConfigurer implements AndroidClasspathConfigurer {

	private static final String ANDROID_GEN_PATH = "gen";
	private static final String ANDROID_CLASSES_FOLDER = "bin/classes";

	public void addGenFolder(IJavaProject javaProject, AndroidProject project, IClasspathDescriptor classpath) {
		IPath gen = javaProject.getPath().append(ANDROID_GEN_PATH);
		if (!classpath.containsPath(gen)) {
			IPath classesOutput = javaProject.getPath().append(ANDROID_CLASSES_FOLDER);
			classpath.addSourceEntry(gen, classesOutput, true);
		}
	}

	public void removeNonRuntimeDependencies(AndroidProject project, IClasspathDescriptor classpath) {
		final List<String> providedDependencies = project.getProvidedDependencies();

		classpath.removeEntry(new EntryFilter() {
			public boolean accept(IClasspathEntryDescriptor descriptor) {
				return providedDependencies.contains(descriptor.getPath().toOSString());
			}
		});
	}

	public void modifySourceFolderOutput(IJavaProject javaProject, AndroidProject project, IClasspathDescriptor classpath) {
		for(IClasspathEntry entry : classpath.getEntries()) {
			if(entry.getOutputLocation() != null && entry.getEntryKind() == IClasspathEntry.CPE_SOURCE
					&& !entry.getOutputLocation().equals(javaProject.getPath().append(ANDROID_CLASSES_FOLDER))) {
				classpath.removeEntry(entry.getPath());
				classpath.addSourceEntry(entry.getPath(), javaProject.getPath().append(ANDROID_CLASSES_FOLDER), false);
			}
		}
	}

	public void markMavenContainerExported(IClasspathDescriptor classpath) {
		for(IClasspathEntry entry : classpath.getEntries()) {
			if(entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
            	if(entry.getPath().toOSString().equals(IClasspathManager.CONTAINER_ID)) {
            		IClasspathEntry newEntry = JavaCore.newContainerEntry(entry.getPath(), true);
            		classpath.removeEntry(entry.getPath());
            		classpath.addEntry(newEntry);
    			}
			}
		}
	}

}
