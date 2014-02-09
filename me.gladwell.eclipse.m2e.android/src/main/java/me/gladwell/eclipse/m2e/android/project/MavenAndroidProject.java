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

    public List<String> getNonRuntimeDependencies();

    public List<Dependency> getLibraryDependencies();

	public boolean matchesDependency(Dependency dependency);

	public File getAssetsDirectory();

    public List<String> getPlatformProvidedDependencies();

}
