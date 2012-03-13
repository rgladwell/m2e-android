package me.gladwell.eclipse.m2e.android.configuration;

import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

public interface ProjectConfigurer {

	boolean isConfigured(EclipseAndroidProject project);
	boolean isValid(MavenAndroidProject project);
	void configure(EclipseAndroidProject eclipseProject, MavenAndroidProject mavenProject);
	
}
