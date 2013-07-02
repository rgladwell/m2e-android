/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import static java.io.File.separator;
import static me.gladwell.eclipse.m2e.android.configuration.Classpaths.findContainerContaining;
import static me.gladwell.eclipse.m2e.android.configuration.Classpaths.findContainerMatching;

import java.io.File;
import java.util.List;

import me.gladwell.eclipse.m2e.android.project.AndroidProject;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IClasspathDescriptor.EntryFilter;
import org.eclipse.m2e.jdt.IClasspathEntryDescriptor;
import org.eclipse.m2e.jdt.IClasspathManager;

import com.android.ide.eclipse.adt.AdtConstants;

public class MavenAndroidClasspathConfigurer implements AndroidClasspathConfigurer {

	private static final String ANDROID_GEN_FOLDER = "gen";
    public static final String ANDROID_CLASSES_FOLDER = "bin" + separator + "classes";

    public void addGenFolder(IJavaProject javaProject, AndroidProject project, IClasspathDescriptor classpath) {
        IFolder gen = javaProject.getProject().getFolder(ANDROID_GEN_FOLDER + File.separator);
        if (!gen.exists()) {
            try {
                gen.create(true, true, new NullProgressMonitor());
            } catch (CoreException e) {
                throw new ProjectConfigurationException(e);
            }
        }

        if (!classpath.containsPath(new Path(ANDROID_GEN_FOLDER))) {
			classpath.addSourceEntry(gen.getFullPath(), null, false);
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

	public void removeJreClasspathContainer(IClasspathDescriptor classpath) {
        IClasspathEntry entry = findContainerContaining(classpath, JavaRuntime.JRE_CONTAINER);
        classpath.removeEntry(entry.getPath());
	}

    public void modifySourceFolderOutput(IJavaProject javaProject, AndroidProject project, IClasspathDescriptor classpath) {
        for(IClasspathEntry entry : classpath.getEntries()) {
            if(entry.getOutputLocation() != null && entry.getEntryKind() == IClasspathEntry.CPE_SOURCE
                    && !entry.getOutputLocation().equals(javaProject.getPath().append(ANDROID_CLASSES_FOLDER))) {
                classpath.removeEntry(entry.getPath());
                classpath.addSourceEntry(entry.getPath(), javaProject.getPath().append(ANDROID_CLASSES_FOLDER), true);
            }
        }
    }

	public void markMavenContainerExported(IClasspathDescriptor classpath) {
        IClasspathEntry oldEntry = findContainerMatching(classpath, IClasspathManager.CONTAINER_ID);
        IClasspathEntry newEntry = JavaCore.newContainerEntry(oldEntry.getPath(), true);
        classpath.removeEntry(oldEntry.getPath());
        classpath.addEntry(newEntry);
	}

    public void markAndroidContainerNotExported(IClasspathDescriptor classpath) {
        IClasspathEntry oldEntry = findContainerMatching(classpath, AdtConstants.CONTAINER_PRIVATE_LIBRARIES);
        if(oldEntry != null) {
            IClasspathEntry newEntry = JavaCore.newContainerEntry(oldEntry.getPath(), false);
            classpath.removeEntry(oldEntry.getPath());
            classpath.addEntry(newEntry);
        } else {
            // TODO log warning here
        }
    }

}

