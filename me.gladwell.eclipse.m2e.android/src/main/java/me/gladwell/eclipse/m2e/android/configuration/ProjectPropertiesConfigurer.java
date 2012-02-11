package me.gladwell.eclipse.m2e.android.configuration;

import java.io.IOException;

import me.gladwell.eclipse.m2e.android.model.AndroidProject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import com.android.ide.eclipse.adt.internal.sdk.ProjectState;
import com.android.ide.eclipse.adt.internal.sdk.Sdk;
import com.android.io.StreamException;
import com.android.sdklib.internal.project.ProjectProperties;
import com.android.sdklib.internal.project.ProjectPropertiesWorkingCopy;

public  abstract class ProjectPropertiesConfigurer implements ProjectConfigurer {

	public void configure(IProject project, AndroidProject androidProject, IProgressMonitor monitor) {
		try {
			ProjectState state = Sdk.getProjectState(project);
			ProjectPropertiesWorkingCopy workingCopy = state.getProperties().makeWorkingCopy();
			configureProperties(project, androidProject, workingCopy);
			workingCopy.save();
			state.reloadProperties();
		} catch (IOException e) {
			throw new ProjectConfigurationException(e);
		} catch (StreamException e) {
			throw new ProjectConfigurationException(e);
		}
		
	}

	protected abstract void configureProperties(IProject project, AndroidProject androidProject, ProjectPropertiesWorkingCopy workingCopy);

}