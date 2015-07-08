/*******************************************************************************
 * Copyright (c) 2012, 2013, 2014, 2015 Ricardo Gladwell and David Carver
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;
import me.gladwell.eclipse.m2e.android.resolve.LibraryResolver;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.embedder.IMaven;

public class JaywayMavenAndroidProject implements MavenAndroidProject {

    private static final String ANDROID_PACKAGE_TYPE = "apk";
    public static final String ANDROID_LIBRARY_PACKAGE_TYPE = "apklib";
    private static final String IGNORE_WARNING_CONFIGURATION_NAME = "ignoreOptionalWarningsInGenFolder";

    private final MavenProject mavenProject;
    private final Plugin jaywayPlugin;
    private final LibraryResolver dependencyResolver;
    private final IMaven maven;

    public JaywayMavenAndroidProject(MavenProject mavenProject, Plugin jaywayPlugin,
            LibraryResolver dependencyResolver, IMaven maven) {
        this.mavenProject = mavenProject;
        this.jaywayPlugin = jaywayPlugin;
        this.dependencyResolver = dependencyResolver;
        this.maven = maven;
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

    public List<Dependency> getNonRuntimeDependencies() {
        List<Dependency> list = new ArrayList<Dependency>(mavenProject.getArtifacts().size());

        for (Artifact a : mavenProject.getArtifacts()) {
            if (a.getArtifactHandler().isAddedToClasspath()) {
                if (!Artifact.SCOPE_COMPILE.equals(a.getScope()) && !Artifact.SCOPE_RUNTIME.equals(a.getScope())) {
                    list.add(new MavenDependency(a));
                }
            }
        }

        return list;
    }

    public List<String> getPlatformProvidedDependencies() {
        final Dependency android = getAndroidDependency();
        final List<String> platformProvidedDependencies = new ArrayList<String>();

        final List<String> libraries = dependencyResolver.resolveLibraries(android, "jar", mavenProject);

        for (String library : libraries) {
            platformProvidedDependencies.add(library);
        }

        return platformProvidedDependencies;
    }

    private Dependency getAndroidDependency() {
        for (Artifact artifact : mavenProject.getArtifacts()) {
            if (isAndroidGroupId(artifact) && artifact.getArtifactId().equals("android")) {
                return new MavenDependency(artifact);
            }
        }
        throw new ProjectConfigurationException("cannot find android dependency for project=[" + getName() + "]");
    }

    private boolean isAndroidGroupId(Artifact artifact) {
        return artifact.getGroupId().equals("com.google.android") || artifact.getGroupId().equals("android");
    }

    public List<Dependency> getLibraryDependencies() {
        List<Dependency> results = new ArrayList<Dependency>(mavenProject.getArtifacts().size());

        for (Artifact a : mavenProject.getArtifacts()) {
            Dependency dependency = new MavenDependency(a);
            if (dependency.isLibrary()) {
                results.add(new MavenDependency(a));
            }
        }

        return results;
    }

    public File getAssetsDirectory() {
        return getConfigurationParameter("assetsDirectory", File.class);
    }

    public File getResourceFolder() {
        return getConfigurationParameter("resourceDirectory", File.class);
    }

    public File getAndroidManifestFile() {
        return getConfigurationParameter("androidManifestFile", File.class);
    }
    
    public File getDestinationManifestFile() {
        return getConfigurationParameter("destinationManifestFile", File.class);
    }

    public boolean isIgnoreOptionalWarningsInGenFolder() {
        Boolean parameter = getConfigurationParameter(IGNORE_WARNING_CONFIGURATION_NAME, Boolean.class);
        return parameter != null ? parameter : false;
    }

    private <T> T getConfigurationParameter(String name, Class<T> type) {
        try {
            return maven.getMojoParameterValue(mavenProject, name, type, jaywayPlugin, jaywayPlugin,
                    "generate-sources", new NullProgressMonitor());
        } catch (CoreException e) {
            throw new ProjectConfigurationException("Could not read POM configuration: " + name, e);
        }
    }

    public List<String> getSourcePaths() {
        return mavenProject.getCompileSourceRoots();
    }

    public String getOutputDirectory() {
        return mavenProject.getBuild().getOutputDirectory();
    }

    public List<String> getTestSourcePaths() {
        return mavenProject.getTestCompileSourceRoots();
    }

    public List<String> getResourcePaths() {
        List<Resource> resources = mavenProject.getResources();
        List<String> resourcePaths = new ArrayList<String>(resources.size());
        for(Resource resource: resources) {
            resourcePaths.add(resource.getDirectory());
        }
        return resourcePaths;
    }

    public List<String> getTestResourcePaths() {
        List<Resource> resources = mavenProject.getTestResources();
        List<String> resourcePaths = new ArrayList<String>(resources.size());
        for(Resource resource: resources) {
            resourcePaths.add(resource.getDirectory());
        }
        return resourcePaths;
    }

}
