/*******************************************************************************
 * Copyright (c) 2009, 2010, 2011 Ricardo Gladwell and Hugo Josefson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import static me.gladwell.eclipse.m2e.android.configuration.MavenAndroidClasspathConfigurer.ANDROID_CLASSES_FOLDER;

import java.io.File;

import me.gladwell.eclipse.m2e.android.configuration.MavenAndroidClasspathConfigurer;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.jdt.IClasspathManager;

import com.android.ide.eclipse.adt.AdtConstants;

/**
 * Test suite for configuring and building Android applications.
 * 
 * @author Ricardo Gladwell <ricardo.gladwell@gmail.com>
 */
public class ApplicationAndroidMavenPluginTest extends AndroidMavenPluginTestCase {

	private static final String ANDROID_PROJECT_NAME = "android-application";

	private IProject project;
	private IJavaProject javaProject;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		project = importAndroidProject(ANDROID_PROJECT_NAME);
		javaProject = JavaCore.create(project);
	}

	@Override
	protected void tearDown() throws Exception {
	    try {
	        deleteProject(ANDROID_PROJECT_NAME);
	    } catch(Throwable t) {
	        t.printStackTrace();
	    }

	    project = null;
		javaProject = null;

		try {
		    super.tearDown();
        } catch(Throwable t) {
            t.printStackTrace();
        }
	}

	public void testConfigure() throws Exception {
		assertNoErrors(project);
	}

	public void testConfigureAddsAndroidNature() throws Exception {
	    assertTrue("configurer failed to add android nature", project.hasNature(AdtConstants.NATURE_DEFAULT));
	}

	public void testConfigureApkBuilderBeforeMavenBuilder() throws Exception {
		boolean foundApkBuilder = false;
		for(ICommand command : project.getDescription().getBuildSpec()) {
			if("com.android.ide.eclipse.adt.ApkBuilder".equals(command.getBuilderName())) {
				foundApkBuilder = true;
			} else if(IMavenConstants.BUILDER_ID.equals(command.getBuilderName())) {
				assertTrue("project APKBuilder not configured before maven builder", foundApkBuilder);
				return;
			}
		}

		fail("project does not contain maven builder build command");
	}

	public void testConfigureDoesNotAddTargetDirectoryToClasspath() throws Exception {
		for(IClasspathEntry entry : javaProject.getRawClasspath()) {
			assertFalse("classpath contains reference to target directory: cause infinite build loops and build conflicts", entry.getPath().toOSString().contains("target"));
		}
	}

	public void testConfigureGeneratedResourcesFolderInRawClasspath() throws Exception {
		assertClasspathContains(javaProject, "gen");
	}

	public void testConfigureAddsCompileDependenciesToClasspath() throws Exception {
		assertClasspathContains(javaProject, "commons-lang-2.4.jar");
	}

	public void testConfigureDoesNotAddNonCompileDependenciesToClasspath() throws Exception {
		assertClasspathDoesNotContain(javaProject, "android-2.1.2.jar");
	}

	public void testConfigureDoesNotAddNonCompileTransitiveDependenciesToClasspath() throws Exception {
		assertClasspathDoesNotContain(javaProject, "commons-logging-1.1.1.jar");
	}

	public void testConfigureDoesNotRemoveJreClasspathContainer() throws Exception {
		assertClasspathDoesNotContain(javaProject, JavaRuntime.JRE_CONTAINER);
	}

	// TODO quarantined intermittently failing integration test
	public void ignoreTestBuildDirectoryContainsCompiledClasses() throws Exception {
		File outputLocation = new File(ResourcesPlugin.getWorkspace().getRoot().getRawLocation().toOSString(), javaProject.getPath().toOSString());
		File apiDemosApplication  = new File(outputLocation, "bin/classes/your/company/HelloAndroidActivity.class");

		buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

		assertTrue(apiDemosApplication.exists());
	}

	public void testConfigureMarksMavenContainerExported() throws Exception {
		IClasspathEntry mavenContainer = getClasspathContainer(javaProject, IClasspathManager.CONTAINER_ID);
		assertTrue(mavenContainer.isExported());
	}

    public void testConfigureSetsCorrectSourceOutputFolder() throws Exception {
        for(IClasspathEntry entry : javaProject.getRawClasspath()) {
            if(entry.getEntryKind() == IClasspathEntry.CPE_SOURCE && entry.getOutputLocation() != null) {
                assertTrue(entry.getOutputLocation().toOSString().endsWith(ANDROID_CLASSES_FOLDER));
            }
        }
    }

}
