/*******************************************************************************
 * Copyright (c) 2015 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import static me.gladwell.eclipse.m2e.android.test.AndroidMavenPluginTestCase.findClasspathContainer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public class TestAndroidProject {

    private final IJavaProject project;

    public TestAndroidProject(IProject project) {
        this(JavaCore.create(project));
    }

    public TestAndroidProject(IJavaProject project) {
        this.project = project;
    }

    private Properties projectProperties() throws IOException, CoreException {
        Properties properties = new Properties();
        File file = project.getProject().getFile("project.properties").getLocation().toFile().getAbsoluteFile();
        properties.load(new FileReader(file));
        return properties;
    }

    public boolean isLibrary() throws IOException, CoreException {
        return Boolean.valueOf(projectProperties().getProperty("android.library"));
    }

    public List<IProject> libraries() throws IOException, CoreException {
        Properties properties = projectProperties();
        List<IProject> libraries = new ArrayList<IProject>();
        int index = 1;
        String library = properties.getProperty("android.library.reference." + index);

        while(library != null) {
            for(IProject project : project.getProject().getWorkspace().getRoot().getProjects()) {
                // TODO fix weak match on project path name due to bug in importAndroidProject
                if(library.endsWith(project.getLocation().toFile().getName())) {
                    libraries.add(project);
                }
            }
            index++;
            library = properties.getProperty("android.library.reference." + index);
        }

        return libraries;
    }

    public IClasspathEntry getAndroidClasspathContainer() throws JavaModelException {
        IClasspathEntry adtContainer = findClasspathContainer(project, "com.android.ide.eclipse.adt.LIBRARIES");
        if(adtContainer != null) return adtContainer;
        return findClasspathContainer(project, "org.eclipse.andmore.LIBRARIES");
    }

}
