/*******************************************************************************
 * Copyright (c) 2009, 2010, 2011 Ricardo Gladwell and Hugo Josefson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import java.util.Enumeration;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import me.gladwell.android.tools.ExecutionException;
import me.gladwell.android.tools.model.ClassDescriptor;
import me.gladwell.android.tools.model.PackageInfo;
import me.gladwell.eclipse.m2e.android.AndroidMavenPluginUtil;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.internal.IMavenConstants;

import com.android.ide.eclipse.adt.AdtConstants;

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

		project = importAndroidProject(ANDROID_15_PROJECT_NAME);
		javaProject = JavaCore.create(project);
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

	public void testBuildCreatesSignedApk() throws Exception {
		buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

		try {
			buildService.verify(AndroidMavenPluginUtil.getApkFile(project));
		} catch(ExecutionException e) {
			fail(e.getMessage());
		}
	}

	public void testBuildUpdatesDependencies() throws Exception {
		buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

		addDependency(project, "commons-io", "commons-io", "2.0.1");

		project.refreshLocal(IProject.DEPTH_INFINITE, monitor);
		buildAndroidProject(project, IncrementalProjectBuilder.INCREMENTAL_BUILD);

		assertTrue("failed to overwrite existing APK", listener.getAndroidMavenBuildEvents().size() > 1);

		assertApkContainsDependency(project, "IOUtils", "org.apache.commons.io");
	}

	public void testBuildOnlyAddsRequiredResourcesToApk() throws Exception {
		buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

		JarFile jar = new JarFile(AndroidMavenPluginUtil.getApkFile(project));
		for(Enumeration<JarEntry> entry = jar.entries(); entry.hasMoreElements(); ) {
		    JarEntry je = entry.nextElement();
		    if (!je.isDirectory()) {
		    	String name = je.getName();
		    	assertFalse("error unwanted resource=[" + name + "] added to APK", name.endsWith(".ap_"));
		    	assertFalse("error unwanted resource=[" + name + "] added to APK", name.endsWith(".apk"));
		    	assertFalse("error unwanted resource=[" + name + "] added to APK", name.endsWith("LICENSE.txt"));
		    	assertFalse("error unwanted resource=[" + name + "] added to APK", name.endsWith("NOTICE.txt"));
		    	assertFalse("error unwanted resource=[" + name + "] added to APK", name.contains("maven"));
		    	assertFalse("error unwanted resource=[" + name + "] added to APK", name.endsWith("pom.xml"));
		    	assertFalse("error unwanted resource=[" + name + "] added to APK", name.endsWith("pom.properties"));
		    }
		}
	}

}
