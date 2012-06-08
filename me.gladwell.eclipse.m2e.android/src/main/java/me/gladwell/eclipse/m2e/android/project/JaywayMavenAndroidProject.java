/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import java.util.ArrayList;
import java.util.HashMap;
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
	    HashMap<String, ArrayList<String>> idMap = new HashMap<String, ArrayList<String>>();
	
	    for ( Artifact a : mavenProject.getArtifacts() ) {
	        if ( a.getType().equals(ANDROID_LIBRARY_PACKAGE_TYPE) ) {
	            final String groupId = a.getGroupId();
	            final String artifactId = a.getArtifactId();

	            ArrayList<String> apklibs = idMap.get(artifactId);
	            if ( apklibs == null ) {
	            	apklibs = new ArrayList<String>(2);
	            	idMap.put(artifactId, apklibs);
	            }
	            apklibs.add( groupId + "." + artifactId );
	        }
	    }

	    for ( String artifactId : idMap.keySet() ) {
	        final ArrayList<String> apklibs = idMap.get( artifactId );
	        if ( apklibs.size() > 1 ) {
	            list.addAll(apklibs);
	        } else {
	            list.add(artifactId);
	        }
	    }
	
	    return list;
	}

}
