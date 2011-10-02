package me.gladwell.eclipse.m2e.android.configuration;

import me.gladwell.eclipse.m2e.android.model.AndroidProject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.android.ide.eclipse.adt.internal.project.ProjectHelper;

public class FixerProjectConfigurer implements ProjectConfigurer {

	public boolean canHandle(AndroidProject.Type type, IProject project) {
		return true;
	}

	public void configure(IProject project, IProgressMonitor monitor) throws CoreException {
		ProjectHelper.fixProject(project);
	}

}
