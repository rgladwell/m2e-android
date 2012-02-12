package me.gladwell.eclipse.m2e.android.configuration;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import me.gladwell.eclipse.m2e.android.model.AndroidProject;

public interface ProjectConfigurer {

	boolean isConfigured(IProject project);
	boolean isValid(AndroidProject androidProject);
	void configure(IProject project, AndroidProject androidProject, IProgressMonitor monitor);
	
}
