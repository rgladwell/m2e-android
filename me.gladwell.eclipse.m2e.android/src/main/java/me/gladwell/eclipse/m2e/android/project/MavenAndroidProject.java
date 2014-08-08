/*******************************************************************************
 * Copyright (c) 2012 - 2014 Ricardo Gladwell and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * 2014-09-10 - David Carver - added helper methods to get test/source/resources
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import java.io.File;
import java.util.List;

import org.apache.maven.model.Resource;

public interface MavenAndroidProject {

    public String getName();

    public String getGroup();

    public String getVersion();

    public boolean isAndroidProject();

    public boolean isLibrary();

    public List<Dependency> getNonRuntimeDependencies();

    public List<Dependency> getLibraryDependencies();

    public File getAssetsDirectory();
    
    public File getResourceFolder();
    
    public File getAndroidManifestFile();

    public List<String> getPlatformProvidedDependencies();

    public List<String> getSourcePaths();

    public boolean isIgnoreOptionalWarningsInGenFolder();
    
    public List<Resource> getResources();
    
    public List<String> getTestSourcePaths();
    
    public List<Resource> getTestResources();
    
    
}
