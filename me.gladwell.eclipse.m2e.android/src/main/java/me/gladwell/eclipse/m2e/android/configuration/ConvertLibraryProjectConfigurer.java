package me.gladwell.eclipse.m2e.android.configuration;

import me.gladwell.eclipse.m2e.android.model.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.model.MavenAndroidProject;

public class ConvertLibraryProjectConfigurer implements ProjectConfigurer {

	public boolean isConfigured(EclipseAndroidProject project) {
		return project.isLibrary();
	}

	public boolean isValid(MavenAndroidProject project) {
		return project.isLibrary();
	}

	public void configure(EclipseAndroidProject eclipseProject, MavenAndroidProject mavenProject) {
		eclipseProject.setLibrary(mavenProject.isLibrary());
	}

}
