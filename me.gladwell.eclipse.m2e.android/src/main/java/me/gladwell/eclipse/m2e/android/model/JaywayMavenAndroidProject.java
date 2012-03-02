package me.gladwell.eclipse.m2e.android.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;

public class JaywayMavenAndroidProject implements MavenAndroidProject {

	private static final String ANDROID_PACKAGE_TYPE = "apk";
	private static final String ANDROID_LIBRARY_PACKAGE_TYPE = "apklib";

	private MavenProject mavenProject;

	public JaywayMavenAndroidProject(MavenProject mavenProject) {
		this.mavenProject = mavenProject;
	}

	public boolean isAndroidProject() {
		String packaging = mavenProject.getPackaging().toLowerCase();
		return ANDROID_LIBRARY_PACKAGE_TYPE.equals(packaging) || ANDROID_PACKAGE_TYPE.equals(packaging);
	}

	public boolean isLibrary() {
		String packaging = mavenProject.getPackaging().toLowerCase();
		return ANDROID_LIBRARY_PACKAGE_TYPE.equals(packaging);
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

	public List<String> getLibraryDependencies() {
	    List<String> list = new ArrayList<String>();
	
	    for ( Artifact a : mavenProject.getArtifacts() ) {
	        if ( a.getType().equals(ANDROID_LIBRARY_PACKAGE_TYPE) ) {
	            list.add(a.getArtifactId());
	        }
	    }
	
	    return list;
	}

}
