package me.gladwell.eclipse.m2e.android.configuration.workspace;

import me.gladwell.eclipse.m2e.android.project.IDEAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

public class LinkResourceDirectoryConfigurer implements WorkspaceConfigurer {

    public boolean isConfigured(IDEAndroidProject project) {
        return false;
    }

    public boolean isValid(MavenAndroidProject project) {
        return true;
    }

    public void configure(IDEAndroidProject eclipseProject, MavenAndroidProject mavenProject) {
        eclipseProject.setResourceFolder(mavenProject.getResourceFolder());
    }

}
