/*******************************************************************************
 * Copyright (c) 2012, 2013, 2014 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

public interface EclipseAndroidProject {

    public boolean isAndroidProject();

    public boolean isLibrary();

    public IProject getProject();

    public void setAndroidProject(boolean androidProject);

    public void setLibrary(boolean isLibrary);

    public void setLibraryDependencies(List<EclipseAndroidProject> dependencies);
    
    public void setPlatform(String platform);

    public void fixProject();

    public boolean isMavenised();

    public IFile getPom();

    public void setAssetsDirectory(File file);
 
    public void setResourceFolder(File resourceDirectory);
    
    public void setAndroidManifest(File androidManifestFile);

    public Classpath getClasspath();
    
    public boolean shouldResolveWorkspaceProjects();

    public String getPath();

}
