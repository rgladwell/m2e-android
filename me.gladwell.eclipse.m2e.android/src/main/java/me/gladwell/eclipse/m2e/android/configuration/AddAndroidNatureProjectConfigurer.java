package me.gladwell.eclipse.m2e.android.configuration;

import me.gladwell.eclipse.m2e.android.model.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.model.MavenAndroidProject;

public class AddAndroidNatureProjectConfigurer implements ProjectConfigurer {

	public boolean isConfigured(EclipseAndroidProject project) {
		return project.isAndroidProject();
	}

	public boolean isValid(MavenAndroidProject project) {
		return true;
	}

	public void configure(EclipseAndroidProject eclipseProject, MavenAndroidProject mavenProject) {
		eclipseProject.setAndroidProject(mavenProject.isAndroidProject());
	}

}
