package me.gladwell.eclipse.m2e.android.configuration;

import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.eclipse.core.resources.IWorkspace;

import com.google.inject.Inject;

public class LinkAssetsFolderConfigurer implements ProjectConfigurer {

	@Inject
	IWorkspace workspace;

	public boolean isConfigured(EclipseAndroidProject project) {
		return false;
	}

	public boolean isValid(MavenAndroidProject project) {
		return true;
	}

	public void configure(EclipseAndroidProject eclipseProject,
			MavenAndroidProject mavenProject) {
		System.out.println("Configuring Linked Asset Folder");
		eclipseProject.configureAssetsDirectory(mavenProject.getAssetsDirectory());
	}

}
