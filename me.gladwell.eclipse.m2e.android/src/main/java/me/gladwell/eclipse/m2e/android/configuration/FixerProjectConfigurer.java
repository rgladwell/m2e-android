package me.gladwell.eclipse.m2e.android.configuration;

import me.gladwell.eclipse.m2e.android.model.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.model.MavenAndroidProject;

public class FixerProjectConfigurer implements ProjectConfigurer {

	public boolean isConfigured(EclipseAndroidProject project) {
		return false;
	}

	public boolean isValid(MavenAndroidProject project) {
		return true;
	}

	public void configure(EclipseAndroidProject eclipseProject, MavenAndroidProject mavenProject) {
		eclipseProject.fixProject();
	}

}
