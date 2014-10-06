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
import me.gladwell.eclipse.m2e.android.AndroidMavenPlugin;
import me.gladwell.eclipse.m2e.android.resolve.LibraryResolver;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.eclipse.m2e.core.embedder.IMaven;

import com.google.inject.Inject;

public class MavenAndroidProjectFactory implements AndroidProjectFactory<MavenAndroidProject, MavenProject> {

    private final LibraryResolver depedendencyResolver;
    private final IMaven maven;

    @Inject
    public MavenAndroidProjectFactory(LibraryResolver depedendencyResolver, IMaven maven) {
        super();
        this.depedendencyResolver = depedendencyResolver;
        this.maven = maven;
    }

    public MavenAndroidProject createAndroidProject(MavenProject mavenProject) {
        final Plugin jaywayPlugin = MavenAndroidProjectFactory.findJaywayAndroidPlugin(mavenProject.getBuildPlugins());
        if (jaywayPlugin != null) {
            JaywayMavenAndroidProject androidProject = new JaywayMavenAndroidProject(mavenProject, jaywayPlugin,
                    depedendencyResolver, maven);
            AndroidMavenPlugin.getDefault().getInjector().injectMembers(androidProject);
            return androidProject;
        }

        throw new AndroidMavenException("un-recognised maven-android project type");
    }

    public static Plugin findJaywayAndroidPlugin(List<Plugin> buildPlugins) {
        for (Plugin plugin : buildPlugins) {
            if (isAndroidPlugin(plugin)) {
                return plugin;
            }
        }
        return null;
    }

    private static boolean isAndroidPlugin(Plugin plugin) {
        return isJaywayPluginWithOldArtifactName(plugin) || isJaywayPluginWithNewArtifactName(plugin)
                || isSimpligilityPlugin(plugin);
    }

    private static boolean isJaywayPluginWithOldArtifactName(Plugin plugin) {
        return "com.jayway.maven.plugins.android.generation2".equals(plugin.getGroupId())
                && "maven-android-plugin".equals(plugin.getArtifactId());
    }

    private static boolean isJaywayPluginWithNewArtifactName(Plugin plugin) {
        return "com.jayway.maven.plugins.android.generation2".equals(plugin.getGroupId())
                && "android-maven-plugin".equals(plugin.getArtifactId());
    }

    private static boolean isSimpligilityPlugin(Plugin plugin) {
        return "com.simpligility.maven.plugins".equals(plugin.getGroupId())
                && "android-maven-plugin".equals(plugin.getArtifactId());
    }
}
