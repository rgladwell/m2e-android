package me.gladwell.eclipse.m2e.android.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class MavenAndroidProject implements AndroidProject {

	private static final String ANDROID_PACKAGE_TYPE = "apk";
	private static final String ANDROID_LIBRARY_PACKAGE_TYPE = "apklib";
	private static final String ANDROID_GEN_PATH = "gen";
	private static final String ANDROID_CLASSES_FOLDER = "bin/classes";

	private MavenProject mavenProject;
	private IJavaProject javaProject;

	public MavenAndroidProject(MavenProject mavenProject, IProject project) {
		this.mavenProject = mavenProject;
		this.javaProject = JavaCore.create(project);
	}

	public IJavaProject getJavaProject() {
		return javaProject;
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

	public List<String> getProvidedDependencies() {
	    List<String> list = new ArrayList<String>( mavenProject.getArtifacts().size() + 1 );
	    list.add( mavenProject.getBuild().getOutputDirectory() );
	
	    for ( Artifact a : mavenProject.getArtifacts() ) {
	        if ( a.getArtifactHandler().isAddedToClasspath() ) {
	            if ( Artifact.SCOPE_PROVIDED.equals( a.getScope() ) ) {
	            	list.add(a.getFile().getAbsolutePath());
	            }
	        }
	    }
	
	    return list;
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
<<<<<<< HEAD
					"android-maven-plugin".equals(plugin.getArtifactId())) {
=======
					("android-maven-plugin".equals(plugin.getArtifactId()) ||
							"maven-android-plugin".equals(plugin.getArtifactId()))) {
>>>>>>> upstream/master
				return plugin;
			}
		}
		return null;
	}

	public IPath getClassesOutputFolder() {
		return javaProject.getPath().append(ANDROID_CLASSES_FOLDER);
	}

	public IPath getGenFolder() {
		return javaProject.getPath().append(ANDROID_GEN_PATH);
	}

}
