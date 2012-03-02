package me.gladwell.eclipse.m2e.android.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;

import com.android.ide.eclipse.adt.AdtConstants;
import com.android.ide.eclipse.adt.internal.project.ProjectHelper;
import com.android.ide.eclipse.adt.internal.sdk.ProjectState;
import com.android.ide.eclipse.adt.internal.sdk.ProjectState.LibraryState;
import com.android.ide.eclipse.adt.internal.sdk.Sdk;
import com.android.io.StreamException;
import com.android.sdklib.internal.project.ProjectProperties;
import com.android.sdklib.internal.project.ProjectPropertiesWorkingCopy;

public class AdtEclipseAndroidProject implements EclipseAndroidProject, AndroidProject {

	private IProject project;

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
			if(androidProject) {
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
		setAndroidProperty(ProjectProperties.PROPERTY_LIBRARY,  Boolean.toString(isLibrary));
	}

	public List<String> getProvidedDependencies() {
		throw new UnsupportedOperationException();
	}

	public void setProvidedDependencies(List<String> providedDependencies) {
		throw new UnsupportedOperationException();
	}

	public List<String> getLibraryDependencies() {
		ProjectState state = getProjectState();
		List<String> libraries = new ArrayList<String>();
		for(LibraryState library : state.getLibraries()) {
			libraries.add(library.getRelativePath());
		}
		return libraries;
	}

	public void setLibraryDependencies(List<String> libraryDependencies) {
		int i = 0;
		for (String library : libraryDependencies) {
			setAndroidProperty(ProjectPropertiesWorkingCopy.PROPERTY_LIB_REF + i, "../" + library);
			i++;
		}
	}

	private void setAndroidProperty(String property, String value) {
		try {
			ProjectState state = getProjectState();
			ProjectPropertiesWorkingCopy workingCopy = state.getProperties().makeWorkingCopy();
			workingCopy.setProperty(property, value);
			workingCopy.save();
			state.reloadProperties();
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

}
