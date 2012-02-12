package me.gladwell.eclipse.m2e.android.configuration;

import me.gladwell.eclipse.m2e.android.model.AndroidProject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;

import com.android.ide.eclipse.adt.AdtConstants;

public class AddAndroidNatureProjectConfigurer implements ProjectConfigurer {

	public boolean isConfigured(IProject project) {
		try {
			return project.hasNature(AdtConstants.NATURE_DEFAULT);
		} catch (CoreException e) {
			throw new ProjectConfigurationException(e);
		}
	}

	public boolean isValid(AndroidProject androidProject) {
		return true;
	}

	public void configure(IProject project, AndroidProject androidProject, IProgressMonitor monitor) {
		try {
			AbstractProjectConfigurator.addNature(project, AdtConstants.NATURE_DEFAULT, monitor);
		} catch (CoreException e) {
			throw new ProjectConfigurationException(e);
		}
	}

}
