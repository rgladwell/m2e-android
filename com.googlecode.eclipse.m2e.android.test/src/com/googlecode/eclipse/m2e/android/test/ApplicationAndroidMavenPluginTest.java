/*******************************************************************************
 * Copyright (c) 2009, 2010, 2011 Ricardo Gladwell and Hugo Josefson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.googlecode.eclipse.m2e.android.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.internal.IMavenConstants;

import com.android.ide.eclipse.adt.AdtConstants;
import com.github.android.tools.model.ClassDescriptor;
import com.github.android.tools.model.PackageInfo;
import com.googlecode.eclipse.m2e.android.AndroidMavenPluginUtil;
import com.googlecode.eclipse.m2e.android.AndroidMavenProjectConfigurator;

/**
 * Test suite for configuring and building Android applications.
 * 
 * @author Ricardo Gladwell <ricardo.gladwell@gmail.com>
 */
public class ApplicationAndroidMavenPluginTest extends AndroidMavenPluginTestCase {

	private static final String ANDROID_15_PROJECT_NAME = "apidemos-15-app";

	private IProject project;
	private IJavaProject javaProject;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		project = importProject("projects/"+ANDROID_15_PROJECT_NAME+"/pom.xml");
		javaProject = JavaCore.create(project);
		waitForJobsToComplete();
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject(ANDROID_15_PROJECT_NAME);
		project = null;
		javaProject = null;

		super.tearDown();
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
			if(AndroidMavenProjectConfigurator.APK_BUILDER_COMMAND_NAME.equals(command.getBuilderName())) {
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
		for(IClasspathEntry entry : javaProject.getRawClasspath()) {
			if(entry.getPath().toOSString().contains("gen")) {
				return;
			}
		}
		fail("gen not added to classpath");
	}

	public void testBuild() throws Exception {
		buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);
		assertTrue("destination apk not successfully built and copied", AndroidMavenPluginUtil.getApkFile(project).exists());
	}

	public void testBuildAddedClassFileToApk() throws Exception {
		buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

		PackageInfo packageInfo = new PackageInfo();
		packageInfo.setName("com.example.android.apis");
		ClassDescriptor apiDemos = new ClassDescriptor();
		apiDemos.setName("ApiDemos");
		apiDemos.setPackageInfo(packageInfo);
		assertApkContains(apiDemos, project);
	}

	public void testBuildAddedDependenciesToApk() throws Exception {
		buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

		PackageInfo packageInfo = new PackageInfo();
		packageInfo.setName("org.apache.commons.lang");
		ClassDescriptor stringUtils = new ClassDescriptor();
		stringUtils.setName("StringUtils");
		stringUtils.setPackageInfo(packageInfo);
		assertApkContains(stringUtils, project);
	}

	public void testBuildOverwritesExistingApk() throws Exception {
		buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

		long first = AndroidMavenPluginUtil.getApkFile(project).lastModified();

		File file = new File(project.getLocation().toFile(), "src/main/java/com/example/android/apis/ApiDemos.java");
		FileWriter fstream = new FileWriter(file,true);
        BufferedWriter out = new BufferedWriter(fstream);
	    out.write("\r\n");
	    out.close();

		project.refreshLocal(IProject.DEPTH_INFINITE, monitor);
		buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

		long second = AndroidMavenPluginUtil.getApkFile(project).lastModified();

		assertTrue("failed to overwrite existing APK", first < second);
	}

}
