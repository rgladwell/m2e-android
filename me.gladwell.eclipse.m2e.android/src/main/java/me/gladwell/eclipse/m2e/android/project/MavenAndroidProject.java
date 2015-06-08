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
    
    public File getDestinationManifestFile();

    public List<String> getPlatformProvidedDependencies();

    public List<String> getSourcePaths();
    
    public String getOutputDirectory();

    public boolean isIgnoreOptionalWarningsInGenFolder();

}
