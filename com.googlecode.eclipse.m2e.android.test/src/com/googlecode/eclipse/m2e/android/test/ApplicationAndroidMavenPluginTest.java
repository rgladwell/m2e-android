/*******************************************************************************
 * Copyright (c) 2009, 2010, 2011 Ricardo Gladwell and Hugo Josefson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.googlecode.eclipse.m2e.android.test;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.security.CodeSigner;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Developer;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
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
	    waitForAdtToLoad();
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

	public void testIncrementalBuildWithoutChangesDoesNotUpdateApk() throws Exception {
		buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

		project.refreshLocal(IProject.DEPTH_INFINITE, androidMavenMonitor);
		buildAndroidProject(project, IncrementalProjectBuilder.INCREMENTAL_BUILD);

		assertFalse("overwrite existing APK", androidMavenMonitor.getAndroidMavenBuildEvents().size() > 1);
	}

	public void testIncrementalBuildUpdatesDependencies() throws Exception {
		buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

		File pom = new File(project.getLocation().toFile(), "pom.xml");
		MavenXpp3Reader reader = new MavenXpp3Reader();
		Model model = reader.read(new FileReader(pom));
		Dependency dependency = new Dependency();
		dependency.setArtifactId("commons-io");
		dependency.setGroupId("commons-io");
		dependency.setVersion("2.0.1");
		model.getDependencies().add(dependency);
		MavenXpp3Writer writer = new MavenXpp3Writer();
		writer.write(new FileWriter(pom), model);

		project.refreshLocal(IProject.DEPTH_INFINITE, androidMavenMonitor);
		buildAndroidProject(project, IncrementalProjectBuilder.INCREMENTAL_BUILD);

		assertTrue("failed to overwrite existing APK", androidMavenMonitor.getAndroidMavenBuildEvents().size() > 1);

		PackageInfo packageInfo = new PackageInfo();
		packageInfo.setName("org.apache.commons.io");
		ClassDescriptor ioUtils = new ClassDescriptor();
		ioUtils.setName("IOUtils");
		ioUtils.setPackageInfo(packageInfo);
		assertApkContains(ioUtils, project);
	}

	public void testIncrementalBuildUpdatesLatestDependencies() throws Exception {
		buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

		IClasspathEntry[] classpath = getMavenContainerEntries(project);
		IClasspathEntry commonsLang = null;
		
		for(IClasspathEntry entry : classpath) {
			if(entry.getPath().toOSString().contains("commons-lang")) {
				commonsLang = entry;
				break;
			}
		}

		long first = System.currentTimeMillis();
		commonsLang.getPath().toFile().setLastModified(first);

		buildAndroidProject(project, IncrementalProjectBuilder.INCREMENTAL_BUILD);

		assertTrue("failed to overwrite existing APK", androidMavenMonitor.getAndroidMavenBuildEvents().size() > 1);
	}

	public void testIncrementalBuildWithoutChangesToClasspathDoesNotUpdateApk() throws Exception {
		buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

		File pom = new File(project.getLocation().toFile(), "pom.xml");
		MavenXpp3Reader reader = new MavenXpp3Reader();
		Model model = reader.read(new FileReader(pom));
		Developer developer = new Developer();
		developer.setId("developer");
		developer.setName("Developer");
		model.getDevelopers().add(developer);
		MavenXpp3Writer writer = new MavenXpp3Writer();
		writer.write(new FileWriter(pom), model);

		project.refreshLocal(IProject.DEPTH_INFINITE, androidMavenMonitor);
		buildAndroidProject(project, IncrementalProjectBuilder.INCREMENTAL_BUILD);

		assertFalse("overwrite existing APK", androidMavenMonitor.getAndroidMavenBuildEvents().size() > 1);
	}

	public void testSimultaneousIncrementalBuildWithoutChangesDoesNotUpdateApk() throws Exception {
		buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

		IProject secondProject = importProject("projects/"+AndroidMavenPluginTest.ISSUE_6_PROJECT_NAME+"/pom.xml");
		waitForJobsToComplete();
	    waitForAdtToLoad();

		buildAndroidProject(secondProject, IncrementalProjectBuilder.FULL_BUILD);

		project.refreshLocal(IProject.DEPTH_INFINITE, androidMavenMonitor);
		buildAndroidProject(project, IncrementalProjectBuilder.INCREMENTAL_BUILD);

		assertFalse("overwrite existing APK", androidMavenMonitor.getAndroidMavenBuildEvents().size() > 2);

		deleteProject(secondProject);
	}

	public void testBuildCreatesSignedApk() throws Exception {
		buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

		JarFile jar = new JarFile(AndroidMavenPluginUtil.getApkFile(project));

		try {
			Set<String> signed = new HashSet<String>();

			Set<String> entries = new HashSet<String>();
			for(Enumeration<JarEntry> entry = jar.entries(); entry.hasMoreElements(); ) {
			    JarEntry je = entry.nextElement();
			    if (!je.isDirectory()) {
			    	CodeSigner[] codeSigners = je.getCodeSigners();
			    	if(codeSigners != null) {
			    		signed.add(je.getName());
			    	}
			        entries.add(je.getName());
			        
	                InputStream is = null;
	                try {
	                    is = jar.getInputStream(je);
	                    while (is.read() != -1) {
	                    }
	                } finally {
	                    if (is != null) {
	                        is.close();
	                    }
	                }
			    }
			}

			Set<String> unsigned = new HashSet<String>(entries);
			unsigned.removeAll(signed);

			assertTrue("error unsigned elements in APK=[" + unsigned + "]", unsigned.isEmpty());
		} catch(SecurityException e) {
			fail(e.getMessage());
		}
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
		    	assertFalse("error unwanted resource=[" + name + "] added to APK", name.endsWith(".aidl"));
		    	assertFalse("error unwanted resource=[" + name + "] added to APK", name.endsWith("LICENSE.txt"));
		    	assertFalse("error unwanted resource=[" + name + "] added to APK", name.endsWith("NOTICE.txt"));
		    	assertFalse("error unwanted resource=[" + name + "] added to APK", name.contains("maven"));
		    	assertFalse("error unwanted resource=[" + name + "] added to APK", name.endsWith("pom.xml"));
		    	assertFalse("error unwanted resource=[" + name + "] added to APK", name.endsWith("pom.properties"));
		    }
		}
	}
}
