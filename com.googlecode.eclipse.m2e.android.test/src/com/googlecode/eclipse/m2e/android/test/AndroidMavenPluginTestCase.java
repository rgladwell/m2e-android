/*******************************************************************************
 * Copyright (c) 2009, 2010, 2011 Ricardo Gladwell and Hugo Josefson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.googlecode.eclipse.m2e.android.test;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;
import org.eclipse.m2e.tests.common.JobHelpers;
import org.eclipse.m2e.tests.common.JobHelpers.IJobMatcher;

import com.android.ide.eclipse.adt.AdtPlugin;
import com.android.ide.eclipse.adt.internal.preferences.AdtPrefs;
import com.android.ide.eclipse.adt.internal.sdk.Sdk;
import com.github.android.tools.AndroidBuildService;
import com.github.android.tools.AndroidToolsException;
import com.github.android.tools.CommandLineAndroidTools;
import com.github.android.tools.DexService;
import com.github.android.tools.MavenAndroidPluginBuildService;
import com.github.android.tools.model.ClassDescriptor;
import com.github.android.tools.model.DexInfo;
import com.github.android.tools.model.Jdk;
import com.googlecode.eclipse.m2e.android.AndroidMavenPluginUtil;

public abstract class AndroidMavenPluginTestCase extends AbstractMavenProjectTestCase {

	static final int MAXIMUM_SECONDS_TO_LOAD_ADT = 30;

	protected AdtPlugin adtPlugin;
	private CommandLineAndroidTools dexInfoService;
	protected TestAndroidMavenProgressMonitor androidMavenMonitor;
	protected AndroidBuildService buildService;

	@Override
	@SuppressWarnings("restriction")
    protected void setUp() throws Exception {
	    super.setUp();

	    adtPlugin = AdtPlugin.getDefault();
	    String androidHome = System.getenv("ANDROID_HOME");
	    
	    if(androidHome != null && !androidHome.equals(adtPlugin.getOsSdkFolder())) {
		    adtPlugin.getPreferenceStore().setValue(AdtPrefs.PREFS_SDK_DIR, androidHome);
		    adtPlugin.savePluginPreferences();
	    }

	    waitForAdtToLoad();

	    dexInfoService = new CommandLineAndroidTools();
	    androidMavenMonitor = new TestAndroidMavenProgressMonitor(monitor);
	    buildService= new MavenAndroidPluginBuildService();
		Jdk jdk = new Jdk();
		jdk.setPath(JavaRuntime.getDefaultVMInstall().getInstallLocation().getAbsoluteFile());
		buildService.setJdk(jdk);
    }

	protected void waitForAdtToLoad() throws InterruptedException, Exception {
		JobHelpers.waitForJobs(new IJobMatcher() {
			public boolean matches(Job job) {
				return job.getClass().getName().contains(Sdk.class.getName());
			}
			
		}, MAXIMUM_SECONDS_TO_LOAD_ADT * 1000);
	}

    protected void buildAndroidProject(IProject project, int kind) throws CoreException, InterruptedException {
		project.build(kind, androidMavenMonitor);
		waitForJobsToComplete();
    }

	void assertApkContains(ClassDescriptor stringUtils, IProject project) throws AndroidToolsException, JavaModelException {
		DexInfo dexInfo = dexInfoService.getDexInfo(AndroidMavenPluginUtil.getApkFile(project));
		assertTrue("external dep class=["+stringUtils+"] not found in file=["+AndroidMavenPluginUtil.getApkFile(project)+"]", dexInfo.getClassDescriptors().contains(stringUtils));
	}

}
