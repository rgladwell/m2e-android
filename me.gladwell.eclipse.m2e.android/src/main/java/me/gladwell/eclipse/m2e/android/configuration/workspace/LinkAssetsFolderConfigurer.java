package me.gladwell.eclipse.m2e.android.configuration.workspace;

import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

public class LinkAssetsFolderConfigurer implements WorkspaceConfigurer {

    public boolean isConfigured(EclipseAndroidProject project) {
        return false;
    }

    public boolean isValid(MavenAndroidProject project) {
        return true;
    }

    public void configure(EclipseAndroidProject eclipseProject, MavenAndroidProject mavenProject) {
        eclipseProject.setAssetsDirectory(mavenProject.getAssetsDirectory());
    }

}
