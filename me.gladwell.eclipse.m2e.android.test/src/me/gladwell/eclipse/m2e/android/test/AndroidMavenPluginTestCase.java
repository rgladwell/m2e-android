/*******************************************************************************
 * Copyright (c) 2009, 2010, 2011 Ricardo Gladwell and Hugo Josefson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import me.gladwell.android.tools.AndroidBuildService;
import me.gladwell.android.tools.AndroidToolsException;
import me.gladwell.android.tools.CommandLineAndroidTools;
import me.gladwell.android.tools.DexService;
import me.gladwell.android.tools.MavenAndroidPluginBuildService;
import me.gladwell.android.tools.model.ClassDescriptor;
import me.gladwell.android.tools.model.DexInfo;
import me.gladwell.android.tools.model.Jdk;
import me.gladwell.eclipse.m2e.android.AndroidMavenPluginUtil;

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

public abstract class AndroidMavenPluginTestCase extends AbstractMavenProjectTestCase {

	static final int MAXIMUM_SECONDS_TO_LOAD_ADT = 60;

	protected AdtPlugin adtPlugin;
	private CommandLineAndroidTools dexInfoService;
	protected DummyAndroidMavenProgressMonitor androidMavenMonitor;
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
	    androidMavenMonitor = new DummyAndroidMavenProgressMonitor(monitor);
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
