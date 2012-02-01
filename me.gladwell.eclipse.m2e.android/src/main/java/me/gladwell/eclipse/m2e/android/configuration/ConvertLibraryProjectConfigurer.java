package me.gladwell.eclipse.m2e.android.configuration;

import me.gladwell.eclipse.m2e.android.model.AndroidProject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import com.android.ide.eclipse.adt.internal.sdk.ProjectState;
import com.android.ide.eclipse.adt.internal.sdk.Sdk;
import com.android.sdklib.internal.project.ProjectProperties;
import com.android.sdklib.internal.project.ProjectPropertiesWorkingCopy;

public class ConvertLibraryProjectConfigurer implements ProjectConfigurer {

	public boolean canHandle(AndroidProject.Type type, IProject project) {
		ProjectState state = Sdk.getProjectState(project);
		return type == AndroidProject.Type.Library && !state.isLibrary();
	}

	public void configure(IProject project, IProgressMonitor monitor) throws Exception {
		ProjectState state = Sdk.getProjectState(project);
		ProjectPropertiesWorkingCopy workingCopy = state.getProperties().makeWorkingCopy();
		workingCopy.setProperty(ProjectProperties.PROPERTY_LIBRARY, "true");
		workingCopy.save();
		state.reloadProperties();
	}
}
