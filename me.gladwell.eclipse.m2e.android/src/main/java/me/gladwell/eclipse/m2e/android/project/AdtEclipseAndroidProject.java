/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import java.io.File;
import java.io.IOException;
import java.util.List;

import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;

import com.android.ide.eclipse.adt.AdtConstants;
import com.android.ide.eclipse.adt.internal.project.ProjectHelper;
import com.android.ide.eclipse.adt.internal.sdk.ProjectState;
import com.android.ide.eclipse.adt.internal.sdk.Sdk;
import com.android.io.StreamException;
import com.android.sdklib.internal.project.ProjectProperties;
import com.android.sdklib.internal.project.ProjectPropertiesWorkingCopy;

public class AdtEclipseAndroidProject implements EclipseAndroidProject, AndroidProject {

	private IProject project;

	public String getName() {
		return project.getName();
	}

	public AdtEclipseAndroidProject(IProject project) {
		this.project = project;
	}

	public IProject getProject() {
		return project;
	}

	public boolean isAndroidProject() {
		try {
			return project.getProject().hasNature(AdtConstants.NATURE_DEFAULT);
		} catch (CoreException e) {
			throw new ProjectConfigurationException(e);
		}
	}

	public void setAndroidProject(boolean androidProject) {
		try {
			if (androidProject) {
				AbstractProjectConfigurator.addNature(project, AdtConstants.NATURE_DEFAULT, null);
			} else {
				throw new UnsupportedOperationException();
			}
		} catch (CoreException e) {
			throw new ProjectConfigurationException(e);
		}
	}

	public boolean isLibrary() {
		ProjectState state = getProjectState();
		return state.isLibrary();
	}

	public void setLibrary(boolean isLibrary) {
		setAndroidProperty(ProjectProperties.PROPERTY_LIBRARY, Boolean.toString(isLibrary));
	}

	public List<String> getProvidedDependencies() {
		throw new UnsupportedOperationException();
	}

	public void setProvidedDependencies(List<String> providedDependencies) {
		throw new UnsupportedOperationException();
	}

	public List<Dependency> getLibraryDependencies() {
		throw new UnsupportedOperationException();
	}

	public void setLibraryDependencies(List<EclipseAndroidProject> libraryDependencies) {
		int i = 1;
		for (EclipseAndroidProject library : libraryDependencies) {
			setAndroidProperty(library, ProjectPropertiesWorkingCopy.PROPERTY_LIB_REF + i, relativizePath(library, getProject()));
			i++;
		}
	}

	private String relativizePath(EclipseAndroidProject libraryProject, IProject baseProject) {
		IPath libraryProjectLocation = libraryProject.getProject().getLocation();
		IPath targetProjectLocation = baseProject.getLocation();
		return libraryProjectLocation.makeRelativeTo(targetProjectLocation).toPortableString();
	}

	private void setAndroidProperty(String property, String value) {
		setAndroidProperty(null, property, value);
	}

	private void setAndroidProperty(EclipseAndroidProject library, String property, String value) {
		try {
			ProjectState state = getProjectState();
			ProjectPropertiesWorkingCopy workingCopy = state.getProperties().makeWorkingCopy();
			workingCopy.setProperty(property, value);
			workingCopy.save();
			state.reloadProperties();
			if (library != null) {
				state.needs(Sdk.getProjectState(library.getProject()));
			}
		} catch (IOException e) {
			throw new ProjectConfigurationException(e);
		} catch (StreamException e) {
			throw new ProjectConfigurationException(e);
		}
	}

	private ProjectState getProjectState() {
		return Sdk.getProjectState(project);
	}

	public void fixProject() {
		try {
			ProjectHelper.fixProject(project);
		} catch (JavaModelException e) {
			throw new ProjectConfigurationException(e);
		}
	}

	public boolean isMavenised() {
		try {
			return project.getProject().hasNature(IMavenConstants.NATURE_ID);
		} catch (CoreException e) {
			throw new ProjectConfigurationException(e);
		}
	}

	public File getPom() {
		return this.project.getFile("pom.xml").getRawLocation().makeAbsolute().toFile();
	}

}
