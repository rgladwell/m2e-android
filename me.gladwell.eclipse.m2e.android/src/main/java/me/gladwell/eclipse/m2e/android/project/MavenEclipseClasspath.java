/*******************************************************************************
 * Copyright (c) 2014 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import static me.gladwell.eclipse.m2e.android.configuration.Classpaths.findContainerContaining;
import static me.gladwell.eclipse.m2e.android.configuration.Classpaths.findContainerMatching;
import static org.eclipse.jdt.core.JavaCore.newContainerEntry;
import static org.eclipse.jdt.core.JavaCore.setClasspathContainer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.m2e.jdt.IClasspathDescriptor;

public class MavenEclipseClasspath implements Classpath {

    private final IJavaProject project;
    private final IClasspathDescriptor classpath;

    public MavenEclipseClasspath(IJavaProject project, IClasspathDescriptor classpath) {
        super();
        this.project = project;
        this.classpath = classpath;
    }

    public Iterable<SourceEntry> getSourceEntries() {
        List<SourceEntry> entries = new ArrayList<SourceEntry>();
        for(IClasspathEntry entry : classpath.getEntries()) {
            if(entry.getOutputLocation() != null && entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
                entries.add(new EclipseSourceEntry(project, classpath, entry));
            }
        }
        return entries;
    }

    public void addContainer(IClasspathContainer container) {
        try {
            setClasspathContainer(container.getPath(),
                    new IJavaProject[] { project.getJavaProject() }, new IClasspathContainer[] { container },
                    new NullProgressMonitor());
            classpath.addEntry(newContainerEntry(container.getPath(), false));
        } catch (JavaModelException e) {
            throw new ProjectConfigurationException(e);
        }
    }

    public void removeContainer(String containerId) {
        IClasspathEntry entry = findContainerContaining(classpath, containerId);
        classpath.removeEntry(entry.getPath());
    }

    public void addSourceEntry(String path) {
        IFolder folder = project.getProject().getFolder(path + File.separator);
        if (!folder.exists()) {
            try {
                folder.create(true, true, new NullProgressMonitor());
            } catch (CoreException e) {
                throw new ProjectConfigurationException(e);
            }
        }

        if (!classpath.containsPath(new Path(path))) {
            classpath.addSourceEntry(folder.getFullPath(), null, false);
        }
    }

    public void markExported(String path) {
        IClasspathEntry oldEntry = findContainerMatching(classpath, path);
        IClasspathEntry newEntry = newContainerEntry(oldEntry.getPath(), true);
        classpath.removeEntry(oldEntry.getPath());
        classpath.addEntry(newEntry);
    }

    public void markNotExported(String path) {
        IClasspathEntry oldEntry = findContainerMatching(classpath, path);
        if(oldEntry != null) {
            IClasspathEntry newEntry = newContainerEntry(oldEntry.getPath(), false);
            classpath.removeEntry(oldEntry.getPath());
            classpath.addEntry(newEntry);
        } else {
            // TODO log warning here
        }
    }

}
