/*******************************************************************************
 * Copyright (c) 2012, 2013, 2014 Ricardo Gladwell and David Carver
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import static java.lang.Boolean.parseBoolean;
import static org.codehaus.plexus.util.StringUtils.isEmpty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;
import me.gladwell.eclipse.m2e.android.resolve.DependencyResolver;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.repository.RemoteRepository;

public class JaywayMavenAndroidProject implements MavenAndroidProject {

    private static final String ANDROID_PACKAGE_TYPE = "apk";
    public static final String ANDROID_LIBRARY_PACKAGE_TYPE = "apklib";
    private static final String IGNORE_WARNING_CONFIGURATION_NAME = "ignoreOptionalWarningsInGenFolder";

    private final MavenProject mavenProject;
    private final Plugin jaywayPlugin;
    private final RepositorySystemSession session;
    private final DependencyResolver dependencyResolver;

    public JaywayMavenAndroidProject(MavenProject mavenProject, Plugin jaywayPlugin, RepositorySystemSession session,
            DependencyResolver dependencyResolver) {
        this.mavenProject = mavenProject;
        this.jaywayPlugin = jaywayPlugin;
        this.session = session;
        this.dependencyResolver = dependencyResolver;
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
        final DefaultProjectBuildingRequest projectBuildingRequest = new DefaultProjectBuildingRequest();
        projectBuildingRequest.setRepositorySession(session);

        List<ArtifactRepository> repositories = mavenProject.getRemoteArtifactRepositories();

        List<RemoteRepository> remoteRepositories = new ArrayList<RemoteRepository>();

        for (ArtifactRepository repository : repositories) {
            remoteRepositories.add(new RemoteRepository(repository.getId(), repository.getLayout().toString(),
                    repository.getUrl()));
        }

        final List<org.sonatype.aether.artifact.Artifact> dependencies = dependencyResolver.resolveDependencies(
                android, "jar", remoteRepositories);
        for (org.sonatype.aether.artifact.Artifact dependency : dependencies) {
            platformProvidedDependencies.add(dependency.getFile().getAbsolutePath());
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
		return getConfiguredFile(getConfiguredAssetsDirectory());
	}
	
	public File getResourceFolder() {
	    return getConfiguredFile(getConfiguredResourceDirectory());
    }
	
	public File getAndroidManifestFile() {
	    return getConfiguredFile(getConfiguredAndroidManifestFile());
	}

	private String getConfiguredAssetsDirectory() {
		return getConfigurationParameter("assetsDirectory");
	}
	
	private String getConfiguredResourceDirectory() {
	    return getConfigurationParameter("resourceDirectory");
	}
	
	private String getConfiguredAndroidManifestFile() {
	    return getConfigurationParameter("androidManifestFile");
	}

    public boolean isIgnoreOptionalWarningsInGenFolder() {
        return parseBoolean(getConfigurationParameter(IGNORE_WARNING_CONFIGURATION_NAME));
    }

    private String getConfigurationParameter(String name) {
        Object configuration = jaywayPlugin.getConfiguration();
        if (configuration instanceof Xpp3Dom) {
            Xpp3Dom confDom = (Xpp3Dom) configuration;
            Xpp3Dom parameterDom = confDom.getChild(name);
            if (parameterDom != null) {
                String parameter = parameterDom.getValue();
                if (!isEmpty(parameter)) {
                    return parameter;
                }
            }
        }
        return null;
    }
    
    private File getConfiguredFile(String path) {
        if(path == null) return null;
        File directory = new File(path);

        if (!directory.isAbsolute()) {
            directory = new File(mavenProject.getBasedir(), path);
        }

        return directory;
    }

    public List<String> getSourcePaths() {
        return mavenProject.getCompileSourceRoots();
    }

}
