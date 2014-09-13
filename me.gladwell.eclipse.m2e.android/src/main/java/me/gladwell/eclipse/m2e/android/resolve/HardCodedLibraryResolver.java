/*******************************************************************************
 * Copyright (c) 2013, 2014 Ricardo Gladwell,
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.resolve;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;
import me.gladwell.eclipse.m2e.android.project.Dependency;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.embedder.IMaven;

import com.google.inject.Inject;

@SuppressWarnings("serial")
public class HardCodedLibraryResolver implements LibraryResolver {
    
    private static final List<Dependency> DEPENDENCIES_BELOW_2_1_2 = new ArrayList<Dependency>() {{
        add(new HardCodedDependency("commons-logging", "commons-logging", "1.1.1"));
        add(new HardCodedDependency("org.apache.httpcomponents", "httpclient", "4.0.1"));
        add(new HardCodedDependency("org.apache.httpcomponents", "httpcore", "4.0.1"));
        add(new HardCodedDependency("commons-codec", "commons-codec", "1.3"));
        add(new HardCodedDependency("org.khronos", "opengl-api", "gl1.1-android-2.1_r1"));
        add(new HardCodedDependency("xerces", "xmlParserAPIs", "2.6.2"));
        add(new HardCodedDependency("xpp3", "xpp3", "1.1.4c"));
    }};
    
    private static final List<Dependency> DEPENDENCIES_FROM_2_1_2 = new ArrayList<Dependency>(DEPENDENCIES_BELOW_2_1_2) {{
        add(new HardCodedDependency("org.json", "json", "20080701"));
    }};
    
    private static final Map<String, List<Dependency>> DEPENDENCIES_OF_VERSIONS = new HashMap<String, List<Dependency>>() {{
        put("1.5_r3", DEPENDENCIES_BELOW_2_1_2);
        put("1.5_r4", DEPENDENCIES_BELOW_2_1_2);
        put("1.6_r3", DEPENDENCIES_BELOW_2_1_2);
        put("2.1_r1", DEPENDENCIES_BELOW_2_1_2);
        put("2.1.2", DEPENDENCIES_FROM_2_1_2);
        put("2.2.1", DEPENDENCIES_FROM_2_1_2);
        put("2.3.1", DEPENDENCIES_FROM_2_1_2);
        put("2.3.3", DEPENDENCIES_FROM_2_1_2);
        put("4.0.1.2", DEPENDENCIES_FROM_2_1_2);
        put("4.1.1.4", DEPENDENCIES_FROM_2_1_2);
    }};

    private final IMaven maven;

    @Inject
    public HardCodedLibraryResolver(IMaven maven) {
        this.maven = maven;
    }

    public List<String> resolveLibraries(Dependency dependency, String type, MavenProject project) {
        List<Dependency> dependenciesForPlatform = getDependenciesForPlatform(dependency,
                project.getRemoteArtifactRepositories());

        List<String> libraries = new ArrayList<String>();

        Artifact androidArtifact = resolve(dependency.getGroup(), dependency.getName(), dependency.getVersion(), type,
                project.getRemoteArtifactRepositories());
        libraries.add(androidArtifact.getFile().getAbsolutePath());

        for (Dependency platformDependency : dependenciesForPlatform) {
            Artifact artifact = resolve(platformDependency.getGroup(), platformDependency.getName(),
                    platformDependency.getVersion(), "jar", project.getRemoteArtifactRepositories());

            libraries.add(artifact.getFile().getAbsolutePath());
        }

        return libraries;
    }

    private List<Dependency> getDependenciesForPlatform(Dependency dependency,
            List<ArtifactRepository> artifactRepositories) throws ProjectConfigurationException {
        if (isDeployedAndroidArtifact(dependency, artifactRepositories)) {
            return Collections.emptyList();
        } else {
            return getPlatformProvidedDependencyList(dependency);
        }
    }

    private boolean isDeployedAndroidArtifact(Dependency dependency, List<ArtifactRepository> artifactRepositories) {
        try {
            Artifact androidArtifact = resolve(dependency.getGroup(), dependency.getName(), dependency.getVersion(),
                    "pom", artifactRepositories);
            Model androidArtifactModel = maven.readModel(androidArtifact.getFile());
            return androidArtifactModel.getDependencies().isEmpty();
        } catch (CoreException e) {
            throw new ProjectConfigurationException(e);
        }
    }

    private static List<Dependency> getPlatformProvidedDependencyList(Dependency dependency) {
        if (!DEPENDENCIES_OF_VERSIONS.containsKey(dependency.getVersion())) {
            throw new ProjectConfigurationException("Unknown version of Maven Central Android dependency!");
        }
        
        return DEPENDENCIES_OF_VERSIONS.get(dependency.getVersion());
    }

    private Artifact resolve(String groupId, String artifactId, String version, String type,
            List<ArtifactRepository> artifactRepositories) {
        try {
            return maven.resolve(groupId, artifactId, version, type, null, artifactRepositories,
                    new NullProgressMonitor());
        } catch (CoreException e) {
            throw new ProjectConfigurationException(e);
        }
    }
}
