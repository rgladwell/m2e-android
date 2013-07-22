/*******************************************************************************
 * Copyright (c) 2012, 2013 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com.android.ide.eclipse.adt.AdtConstants;

@SuppressWarnings("restriction")
public class AndroidMavenPluginTest extends AndroidMavenPluginTestCase {

	private static final String SIMPLE_PROJECT_NAME = "simple-project";
    private static final String MULTIMODULE_ROOT = "projects/issue-68";

	public void testConfigureNonAndroidProject() throws Exception {
		IProject project = importAndroidProject(SIMPLE_PROJECT_NAME);

	    assertFalse("configurer added android nature", project.hasNature(AdtConstants.NATURE_DEFAULT));
		IJavaProject javaProject = JavaCore.create(project);
		assertFalse("output location set to android value for non-android project", javaProject.getOutputLocation().toString().equals("/"+SIMPLE_PROJECT_NAME+"/target/android-classes"));

		for(IClasspathEntry entry : javaProject.getRawClasspath()) {
			assertFalse("classpath contains reference to gen directory", entry.getPath().toOSString().contains("gen"));
		}
	}

	public void testConfigureAddsWorkspaceProjectDepsToClasspath() throws Exception {
		importAndroidProject(SIMPLE_PROJECT_NAME);
		IProject project = importAndroidProject("test-project-workspace-deps");
		assertClasspathContains(JavaCore.create(project), SIMPLE_PROJECT_NAME);
	}

	public void testNonDefaultInternalAssetsFolderCompiles() throws Exception {
        IProject[] projects = importAndroidProjects(MULTIMODULE_ROOT, new String[] { "pom.xml", "android-internaldirassets/pom.xml" });
        IProject project = projects[1];

        assertNoErrors(project);
    }

    public void testNonDefaultInternalAssetsLinkCreated() throws Exception {
        IProject[] projects = importAndroidProjects(MULTIMODULE_ROOT, new String[] { "pom.xml", "android-internaldirassets/pom.xml" });
        IProject project = projects[1];

        // TODO insufficient test, should verify linked location
        assertTrue("internal assets folder isn't linked", project.getFolder("assets").isLinked());
    }

    public void testNonDefaultExternalAssetsFolderCompiles() throws Exception {
        IProject[] projects = importAndroidProjects(MULTIMODULE_ROOT, new String[] { "pom.xml", "android-relativeoutside/pom.xml" });
        IProject project = projects[1];

        assertNoErrors(project);
    }

    public void testNonDefaultExternalAssetsLinkCreated() throws Exception {
        IProject[] projects = importAndroidProjects(MULTIMODULE_ROOT, new String[] { "pom.xml", "android-relativeoutside/pom.xml" });
        IProject project = projects[1];

        // TODO insufficient test, should verify linked location
        assertTrue("external assets folder isn't linked", project.getFolder("assets").isLinked());
    }

}
