/*******************************************************************************
 * Copyright (c) 2009-2015 Ricardo Gladwell and Hugo Josefson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import static java.util.Arrays.asList;
import static java.util.Arrays.copyOf;
import static me.gladwell.eclipse.m2e.android.test.Classpaths.bySourceFolderOrdering;
import static org.jboss.tools.maven.apt.preferences.AnnotationProcessingMode.disabled;
import static org.jboss.tools.maven.apt.preferences.AnnotationProcessingMode.jdt_apt;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.jboss.tools.maven.apt.MavenJdtAptPlugin;
import org.jboss.tools.maven.apt.preferences.IPreferencesManager;

public class AndroidAnnotationsMavenPluginTest extends AndroidMavenPluginTestCase {

    private IProject project;
    private IPreferencesManager preferencesManager = MavenJdtAptPlugin.getDefault().getPreferencesManager();

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        enableAnnotationProcessing();
        project = ProjectImporter.importAndroidTestProject("android-annotations-project").into(workspace);
    }

    @Override
    protected void tearDown() throws Exception {
        disableAnnotationProcessing();
        super.tearDown();
    }

    private void enableAnnotationProcessing() throws Exception {
        preferencesManager.setAnnotationProcessorMode(null, jdt_apt);
    }

    private void disableAnnotationProcessing() {
        preferencesManager.setAnnotationProcessorMode(null, disabled);
    }

    public void testSourceFolderOrder() throws Exception {
        IJavaProject javaProject = JavaCore.create(project);
        IClasspathEntry[] rawClasspath = copyOf(javaProject.getRawClasspath(), javaProject.getRawClasspath().length);
        List<IClasspathEntry> sorted = asList(rawClasspath);
        Collections.sort(sorted, bySourceFolderOrdering);

        assertEquals(sorted, asList(javaProject.getRawClasspath()));
    }

}
