package me.gladwell.eclipse.m2e.android.configuration;


import me.gladwell.eclipse.m2e.android.model.AndroidProject;

import org.eclipse.core.resources.IProject;

import com.android.ide.eclipse.adt.internal.sdk.ProjectState;
import com.android.ide.eclipse.adt.internal.sdk.Sdk;
import com.android.sdklib.internal.project.ProjectProperties;
import com.android.sdklib.internal.project.ProjectPropertiesWorkingCopy;

public class ConvertLibraryProjectConfigurer extends ProjectPropertiesConfigurer {

	public boolean isConfigured(IProject project) {
		ProjectState state = Sdk.getProjectState(project);
		return state.isLibrary();
	}

	public boolean isValid(AndroidProject androidProject) {
		return androidProject.getType().equals(AndroidProject.Type.Library);
	}

	@Override
	protected void configureProperties(ProjectPropertiesWorkingCopy workingCopy) {
		workingCopy.setProperty(ProjectProperties.PROPERTY_LIBRARY, "true");
	}

}
