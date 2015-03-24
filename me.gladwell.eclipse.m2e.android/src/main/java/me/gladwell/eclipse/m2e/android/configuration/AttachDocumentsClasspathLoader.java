/*******************************************************************************
 * Copyright (c) 2015 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import static java.util.Arrays.asList;
import static me.gladwell.eclipse.m2e.android.Log.warn;
import static org.eclipse.jdt.core.IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME;
import static org.eclipse.jdt.core.IClasspathEntry.CPE_LIBRARY;
import static org.eclipse.jdt.core.JavaCore.newClasspathAttribute;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import me.gladwell.eclipse.m2e.android.project.AndroidProjectFactory;
import me.gladwell.eclipse.m2e.android.project.Dependency;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.embedder.IMavenConfiguration;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;

import com.google.inject.Inject;

public class AttachDocumentsClasspathLoader extends ClasspathLoaderDecorator {

    private static final String CLASSIFIER_DOCS = "javadoc";

    private static class ClasspathAttributes {

        private final List<IClasspathAttribute> attributes;

        public ClasspathAttributes(IClasspathAttribute[] attributes) {
            this.attributes = new ArrayList<IClasspathAttribute>(asList(attributes));
        }

        public boolean hasAttribute(String name) {
            for(IClasspathAttribute attribute: attributes) {
                if(attribute.getName().equals(name)) return true;
            }
            return false;
        }

        public void set(IClasspathAttribute toset) {
            int index = 0;
            for(IClasspathAttribute attribute : attributes) {
                if(attribute.getName().equals(toset.getName())) {
                    attributes.set(index, toset);
                    return;
                }
                index ++;
            }

            attributes.add(toset);
        }

        public IClasspathAttribute[] toArray() {
            return attributes.toArray(new IClasspathAttribute[attributes.size()]);
        }

    }

    private final IMavenConfiguration configuration;
    private final IMaven maven;
    private final IMavenProjectRegistry registry;
    private final AndroidProjectFactory<MavenAndroidProject, MavenProject> factory;

    // TODO too many dependencies: split class
    @Inject
    public AttachDocumentsClasspathLoader(@PrunePlatformProvidedDependencies ClasspathLoader wrapped, IMavenConfiguration configuration, IMaven maven, IMavenProjectRegistry registry, AndroidProjectFactory<MavenAndroidProject, MavenProject> factory) {
        super(wrapped);
        this.configuration = configuration;
        this.maven = maven;
        this.registry = registry;
        this.factory = factory;
    }

    @Override
    public Iterable<IClasspathEntry> load(IJavaProject project) throws FileNotFoundException {
        Iterable<IClasspathEntry> classpath = super.load(project);

        if(configuration.isDownloadJavaDoc()) {
            List<IClasspathEntry> processed = new ArrayList<IClasspathEntry>();
                try {
                    MavenProject mavenProject = registry.getProject(project.getProject()).getMavenProject(new NullProgressMonitor());
                    MavenAndroidProject androidProject = factory.createAndroidProject(mavenProject);
                    List<ArtifactRepository> repositories = mavenProject.getRemoteArtifactRepositories();

                    for(IClasspathEntry entry: classpath) {
                        try {

                            ClasspathAttributes attributes = new ClasspathAttributes(entry.getExtraAttributes());
                            if(CPE_LIBRARY == entry.getEntryKind() && !attributes.hasAttribute(JAVADOC_LOCATION_ATTRIBUTE_NAME)) {
                                Dependency dependency = findDependency(entry, androidProject.getNonRuntimeDependencies());
                                Artifact docs = maven.resolve(dependency.getGroup(),
                                                                        dependency.getName(),
                                                                        dependency.getVersion(),
                                                                        "jar",
                                                                        CLASSIFIER_DOCS,
                                                                        repositories,
                                                                        new NullProgressMonitor());

                                attributes.set(newClasspathAttribute(JAVADOC_LOCATION_ATTRIBUTE_NAME, getJavaDocUrl(docs.getFile())));

                                IClasspathEntry entryWithDocs = JavaCore.newLibraryEntry(entry.getPath(), entry.getSourceAttachmentPath(),
                                        null, entry.getAccessRules(),
                                        attributes.toArray(),
                                        entry.isExported());

                                processed.add(entryWithDocs);
                            } else {
                                processed.add(entry);
                            }

                        } catch (Exception e) {
                            warn("could not resolve javadocs for classpath entry=[" + entry + "]");
                            processed.add(entry);
                        }
                    }
                } catch(CoreException e) {
                    throw new ProjectConfigurationException(e);
                }


            return processed;
        }

        return classpath;
    }

    private Dependency findDependency(IClasspathEntry entry, List<Dependency> nonRuntimeDependencies) {
        for(Dependency dependency: nonRuntimeDependencies) {
            if(dependency.getPath().equals(entry.getPath().toOSString())) return dependency;
        }
        throw new ProjectConfigurationException("could not find dependency for entry=[" + entry.getPath() + "]");
    }

    static String getJavaDocUrl(File file) throws ZipException, IOException {
        URL fileUrl = file.toURI().toURL();
        return "jar:" + fileUrl.toExternalForm() + "!/" + getJavaDocPathInArchive(file);
    }

    private static String getJavaDocPathInArchive(File file) throws ZipException, IOException {
        ZipFile jarFile = null;
        try {
            jarFile = new ZipFile(file);
            String marker = "package-list";
            for(Enumeration<? extends ZipEntry> en = jarFile.entries(); en.hasMoreElements();) {
                ZipEntry entry = en.nextElement();
                String entryName = entry.getName();
                if(entryName.endsWith(marker)) {
                    return entry.getName().substring(0, entryName.length() - marker.length());
                }
            }
        } finally {
            if(jarFile != null) jarFile.close();
        }

        throw new ProjectConfigurationException("error finding javadoc path in JAR=[" + file + "]");
      }

}
