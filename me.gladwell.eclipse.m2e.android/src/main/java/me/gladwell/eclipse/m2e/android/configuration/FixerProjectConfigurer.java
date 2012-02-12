package me.gladwell.eclipse.m2e.android.configuration;

import me.gladwell.eclipse.m2e.android.model.AndroidProject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;

import com.android.ide.eclipse.adt.internal.project.ProjectHelper;

public class FixerProjectConfigurer implements ProjectConfigurer {

	public boolean isConfigured(IProject project) {
		return false;
	}

	public boolean isValid(AndroidProject androidProject) {
		return true;
	}

	public void configure(IProject project, AndroidProject androidProject, IProgressMonitor monitor) {
		try {
			ProjectHelper.fixProject(project);
		} catch (JavaModelException e) {
			throw new ProjectConfigurationException(e);
		}
	}

}
