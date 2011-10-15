package me.gladwell.eclipse.m2e.android.model;

import java.util.List;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public class MavenAndroidProject implements AndroidProject {

	private static final String ANDROID_PACKAGE_TYPE = "apk";
	private static final String ANDROID_LIBRARY_PACKAGE_TYPE = "apklib";

	private MavenProject mavenProject;

	public MavenAndroidProject(MavenProject mavenProject) {
		this.mavenProject = mavenProject;
	}

	public Type getType() {
		String packaging = mavenProject.getPackaging().toLowerCase();
		if (ANDROID_PACKAGE_TYPE.equals(packaging)) {
			return AndroidProject.Type.Application;
		}
		
		if (ANDROID_LIBRARY_PACKAGE_TYPE.equals(packaging)) {
			return AndroidProject.Type.Library;
		}

		throw new IllegalStateException("Unknown android project type");
	}

	public String getPlatform() {
		Plugin plugin = findAndroidPlugin(mavenProject.getBuildPlugins());
		Xpp3Dom dom = (Xpp3Dom) plugin.getConfiguration();
		String platform = dom.getChild("sdk").getChild("platform").getValue();
		return platform;
	}

	private Plugin findAndroidPlugin(List<Plugin> buildPlugins) {
		for(Plugin plugin : buildPlugins) {
			if("com.jayway.maven.plugins.android.generation2".equals(plugin.getGroupId()) &&
					"android-maven-plugin".equals(plugin.getArtifactId())) {
				return plugin;
			}
		}
		return null;
	}

}
