/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import static org.codehaus.plexus.util.StringUtils.isEmpty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public class JaywayMavenAndroidProject implements MavenAndroidProject {

	private static final String ANDROID_PACKAGE_TYPE = "apk";
	static final String ANDROID_LIBRARY_PACKAGE_TYPE = "apklib";

	private final MavenProject mavenProject;
    private final Plugin jaywayPlugin;

    public JaywayMavenAndroidProject(MavenProject mavenProject, Plugin jaywayPlugin) {
        this.mavenProject = mavenProject;
        this.jaywayPlugin = jaywayPlugin;
    }

    public String getName() {
		return mavenProject.getArtifactId();
	}

	public String getGroup() {
		return mavenProject.getGroupId();
	}

	public String getVersion() {
		return mavenProject.getVersion();
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

	public List<Dependency> getLibraryDependencies() {
	    List<Dependency> results = new ArrayList<Dependency>(mavenProject.getArtifacts().size());
	
	    for(Artifact a : mavenProject.getArtifacts()) {
	    	Dependency dependency = new MavenDependency(a);
	        if(dependency.isLibrary()) {
	        	results.add(new MavenDependency(a));
	        }
	    }

	    return results;
	}

	public boolean matchesDependency(Dependency dependency) {
		return StringUtils.equals(dependency.getName(), getName())
				&& StringUtils.equals(dependency.getGroup(), mavenProject.getGroupId())
				&& StringUtils.equals(dependency.getVersion(), mavenProject.getVersion());
	}

	public File getAssetsDirectory() {
		String configuredAssetsDirectory = getConfiguredAssetsDirectory();
		if(configuredAssetsDirectory == null) return null;
		File assetsDirectory = new File(configuredAssetsDirectory);

		if (!assetsDirectory.isAbsolute()) {
			assetsDirectory = new File(mavenProject.getBasedir(), configuredAssetsDirectory);
		}

		return assetsDirectory;
	}

	private String getConfiguredAssetsDirectory() {
		Object configuration = jaywayPlugin.getConfiguration();
		if (configuration instanceof Xpp3Dom) {
			Xpp3Dom confDom = (Xpp3Dom) configuration;
			Xpp3Dom assetsDirectoryDom = confDom.getChild("assetsDirectory");
			if (assetsDirectoryDom != null) {
				String assetsDirectory = assetsDirectoryDom.getValue();
				if (!isEmpty(assetsDirectory)) {
					return assetsDirectory;
				}
			}
		}
		return null;
	}

}
