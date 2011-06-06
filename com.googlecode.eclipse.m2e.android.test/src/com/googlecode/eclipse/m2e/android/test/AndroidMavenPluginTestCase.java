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
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;

import com.android.ide.common.sdk.LoadStatus;
import com.android.ide.eclipse.adt.AdtPlugin;
import com.android.ide.eclipse.adt.internal.preferences.AdtPrefs;
import com.github.android.tools.AndroidToolsException;
import com.github.android.tools.CommandLineAndroidTools;
import com.github.android.tools.DexService;
import com.github.android.tools.model.ClassDescriptor;
import com.github.android.tools.model.DexInfo;
import com.googlecode.eclipse.m2e.android.AndroidMavenPluginUtil;

public abstract class AndroidMavenPluginTestCase extends AbstractMavenProjectTestCase {

	static final int MAXIMUM_SECONDS_TO_LOAD_ADT = 5;

	protected AdtPlugin adtPlugin;

	private DexService dexInfoService;

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

	    int loops = 0;
	    while(!adtPlugin.getSdkLoadStatus().equals(LoadStatus.LOADED)) {
	    	Thread.sleep(1000);
	    	loops++;
	    	if(loops == MAXIMUM_SECONDS_TO_LOAD_ADT) {
	    		throw new Exception("failed to load ADT using SDK=["+androidHome+"] - check the ANDROID_HOME envar is correct.");
	    	}
	    }
	    
	    dexInfoService = new CommandLineAndroidTools();
    }

    protected void buildAndroidProject(IProject project, int kind) throws CoreException, InterruptedException {
		project.build(kind, monitor);
		waitForJobsToComplete();
    }

	void assertApkContains(ClassDescriptor stringUtils, IProject project) throws AndroidToolsException, JavaModelException {
		DexInfo dexInfo = dexInfoService.getDexInfo(AndroidMavenPluginUtil.getApkFile(project));
		assertTrue("external dep class=["+stringUtils+"] not found in file=["+AndroidMavenPluginUtil.getApkFile(project)+"]", dexInfo.getClassDescriptors().contains(stringUtils));
	}
}
