package me.gladwell.eclipse.m2e.android.configuration;

import me.gladwell.eclipse.m2e.android.model.ProjectType;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;

import com.android.ide.eclipse.adt.AdtConstants;

public class AddAndroidNatureProjectConfigurer implements ProjectConfigurer {

	public boolean canHandle(ProjectType type, IProject project) throws CoreException {
		return !project.hasNature(AdtConstants.NATURE_DEFAULT);
	}

	public void configure(IProject project, IProgressMonitor monitor) throws CoreException {
		AbstractProjectConfigurator.addNature(project, AdtConstants.NATURE_DEFAULT, monitor);
	}

}
