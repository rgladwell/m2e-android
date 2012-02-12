package me.gladwell.eclipse.m2e.android.configuration;

import me.gladwell.eclipse.m2e.android.model.AndroidProject;

import org.eclipse.core.resources.IProject;

import com.android.sdklib.internal.project.ProjectPropertiesWorkingCopy;

public class LibraryDependenciesProjectConfigurer extends ProjectPropertiesConfigurer {

	public boolean isConfigured(IProject project) {
		return false;
	}

	public boolean isValid(AndroidProject androidProject) {
		return !androidProject.getLibraryDependencies().isEmpty();
	}

	@Override
	protected void configureProperties(IProject project, AndroidProject androidProject, ProjectPropertiesWorkingCopy workingCopy) {
		int i = 0;
		for (String library : androidProject.getLibraryDependencies()) {
			i++;
			workingCopy.setProperty(ProjectPropertiesWorkingCopy.PROPERTY_LIB_REF + i, "../" + library);
		}
	}

}
