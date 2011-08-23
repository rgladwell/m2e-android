package me.gladwell.eclipse.m2e.android.configuration;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import me.gladwell.eclipse.m2e.android.model.ProjectType;

public interface ProjectConfigurer {

	boolean canHandle(ProjectType type, IProject project) throws Exception;
	void configure(IProject project, IProgressMonitor monitor) throws Exception;
	
}
