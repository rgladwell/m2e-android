/*******************************************************************************
 * Copyright (c) 2012, 2014 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import java.util.List;

import me.gladwell.eclipse.m2e.android.AndroidMavenException;
import me.gladwell.eclipse.m2e.android.resolve.DependencyResolver;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.sonatype.aether.RepositorySystemSession;

import com.google.inject.Inject;

public class MavenAndroidProjectFactory implements AndroidProjectFactory<MavenAndroidProject, MavenProject> {

    private final RepositorySystemSession session;
    private final DependencyResolver depedendencyResolver;

    @Inject
	public MavenAndroidProjectFactory(RepositorySystemSession session, DependencyResolver depedendencyResolver) {
        super();
        this.session = session;
        this.depedendencyResolver = depedendencyResolver;
    }

    public MavenAndroidProject createAndroidProject(MavenProject mavenProject) {
		final Plugin jaywayPlugin = MavenAndroidProjectFactory.findJaywayAndroidPlugin(mavenProject.getBuildPlugins());
        if(jaywayPlugin != null) {
			return new JaywayMavenAndroidProject(mavenProject, jaywayPlugin, session, depedendencyResolver);
		}

		throw new AndroidMavenException("un-recognised maven-android project type");
	}

    public static Plugin findJaywayAndroidPlugin(List<Plugin> buildPlugins) {
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
