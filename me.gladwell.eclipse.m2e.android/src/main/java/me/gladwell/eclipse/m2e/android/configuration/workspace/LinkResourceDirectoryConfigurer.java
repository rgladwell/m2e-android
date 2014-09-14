package me.gladwell.eclipse.m2e.android.configuration.workspace;

import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

public class LinkResourceDirectoryConfigurer implements WorkspaceConfigurer {

    public boolean isConfigured(EclipseAndroidProject eclipseProject, MavenAndroidProject mavenProject) {
        return false;
    }

    public boolean isValid(MavenAndroidProject project) {
        return true;
    }

    public void configure(EclipseAndroidProject eclipseProject, MavenAndroidProject mavenProject) {
        eclipseProject.setResourceFolder(mavenProject.getResourceFolder());
    }

}
