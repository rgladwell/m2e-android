/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import static com.android.ide.eclipse.adt.internal.sdk.Sdk.getProjectState;
import static org.eclipse.m2e.core.MavenPlugin.getProjectConfigurationManager;

import me.gladwell.eclipse.m2e.android.AndroidMavenException;
import me.gladwell.eclipse.m2e.android.AndroidMavenPlugin;
import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;

import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;

import junit.framework.Assert;
import java.io.File;

public class LibraryAndroidMavenPluginTest extends AndroidMavenPluginTestCase {

    private static final String ANDROID_LIB_PROJECT_NAME = "apklib-project";

    private IProject libraryProject, javaProject;
    private IJavaProject closedJavaProject;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		deleteProject(ANDROID_LIB_PROJECT_NAME);
		libraryProject = importAndroidProject(ANDROID_LIB_PROJECT_NAME);
		javaProject = importAndroidProject("closed-java-project");
		closedJavaProject = JavaCore.create(javaProject);
		closedJavaProject.close();
		javaProject.close(null);
	}

	public void testConfigure() throws Exception {
		assertNoErrors(libraryProject);
	}

	public void testConfigureAppliesLibraryState() throws Exception {
		assertTrue(getProjectState(libraryProject).isLibrary());
	}

	public void testConfigureAddsWorkspaceLibraryProjectToProjectProperties() throws Exception {
		IProject project = importAndroidProject("test-project-apklib-deps");

		assertTrue(getProjectState(project).getFullLibraryProjects().contains(libraryProject));
	}

	public void testConfigureAddsErrorForNonExistentLibraryProject() throws Exception {
		deleteProject(ANDROID_LIB_PROJECT_NAME);
		IProject project = importAndroidProject("test-project-apklib-deps");

		assertErrorMarker(project, AndroidMavenPlugin.APKLIB_ERROR_TYPE);
	}

	public void testConfigureWithAClosedProjectInTheWorkspace() throws Exception {
		try{
			IProject project = importAndroidProject("test-project-apklib-deps");
			
		} catch (CoreException ex){
			if (ex.getCause() instanceof ProjectConfigurationException){
				Assert.fail("Closed project");
			}
		}
	}

	public void testConfigureAddsWorkspaceLibraryProjectWithDifferentArtifactId() throws Exception {
		IProject project = importAndroidProject("test-project-apklib-deps-diff-artifact-id");

		assertTrue(getProjectState(project).getFullLibraryProjects().contains(libraryProject));
	}

	public void testConfigureClearsOldErrors() throws Exception {
		deleteProject(ANDROID_LIB_PROJECT_NAME);
		IProject project = importAndroidProject("test-project-apklib-deps");
		importAndroidProject(ANDROID_LIB_PROJECT_NAME);
		getProjectConfigurationManager().updateProjectConfiguration(project, monitor);

		assertNoErrors(project);
	}
	
	public void testConfigureDoNotAddErrorMarkerForNonMavenizedLibraryProjectPresentInWorkspace() throws Exception {
		
		IProject project = importAndroidProject("test-project-non-mvn-apklib-deps");
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		File src = new File("projects/non-mvn-apklib-project");
	    File dst = new File(root.getLocation().toFile(), src.getName());
	    copyDir(src, dst);
		IProject nonMvnApkLibProject = root.getProject("non-mvn-apklib-project");
		nonMvnApkLibProject.create(null);
		nonMvnApkLibProject.open(null);
		
		getProjectConfigurationManager().updateProjectConfiguration(project, monitor);
		
		assertNoErrors(project);
	}

}
