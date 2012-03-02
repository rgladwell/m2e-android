package me.gladwell.eclipse.m2e.android;

import java.util.List;

import me.gladwell.eclipse.m2e.android.model.JaywayMavenAndroidProject;
import me.gladwell.eclipse.m2e.android.model.MavenAndroidProject;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;

public class MavenAndroidProjectFactory implements AndroidProjectFactory<MavenAndroidProject, MavenProject> {

	public MavenAndroidProject createAndroidProject(MavenProject mavenProject) {
		if(findJaywayAndroidPlugin(mavenProject.getBuildPlugins()) != null) {
			return new JaywayMavenAndroidProject(mavenProject);
		}

		throw new AndroidMavenException("un-recognised maven-android project type");
	}

	private Plugin findJaywayAndroidPlugin(List<Plugin> buildPlugins) {
		for(Plugin plugin : buildPlugins) {
			if("com.jayway.maven.plugins.android.generation2".equals(plugin.getGroupId()) &&
					("android-maven-plugin".equals(plugin.getArtifactId()) ||
							"maven-android-plugin".equals(plugin.getArtifactId()))) {
				return plugin;
			}
		}
		return null;
	}

}
