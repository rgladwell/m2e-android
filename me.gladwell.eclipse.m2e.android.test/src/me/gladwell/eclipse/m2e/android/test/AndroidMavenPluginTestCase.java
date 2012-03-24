/*******************************************************************************
 * Copyright (c) 2009, 2010, 2011 Ricardo Gladwell and Hugo Josefson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import static com.android.ide.eclipse.adt.AdtPlugin.getOsSdkFolder;
import me.gladwell.eclipse.m2e.android.AndroidMavenPlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;
import org.eclipse.m2e.tests.common.JobHelpers;
import org.eclipse.m2e.tests.common.JobHelpers.IJobMatcher;

import com.android.ide.eclipse.adt.AdtPlugin;
import com.android.ide.eclipse.adt.internal.preferences.AdtPrefs;
import com.android.ide.eclipse.adt.internal.sdk.Sdk;

public abstract class AndroidMavenPluginTestCase extends AbstractMavenProjectTestCase {

	static final int MAXIMUM_SECONDS_TO_LOAD_ADT = 60;

	protected AndroidMavenPlugin plugin;
	protected AdtPlugin adtPlugin;

	@Override
	@SuppressWarnings("restriction")
    protected void setUp() throws Exception {
	    super.setUp();

	    plugin = AndroidMavenPlugin.getDefault();
		plugin.getInjector().injectMembers(this);

		adtPlugin = AdtPlugin.getDefault();
	    String androidHome = System.getenv("ANDROID_HOME");
	    
	    if(androidHome != null && !androidHome.equals(getOsSdkFolder())) {
		    adtPlugin.getPreferenceStore().setValue(AdtPrefs.PREFS_SDK_DIR, androidHome);
		    adtPlugin.savePluginPreferences();
	    }

	    waitForAdtToLoad();
    }

	protected void waitForAdtToLoad() throws InterruptedException, Exception {
		JobHelpers.waitForJobs(new IJobMatcher() {
			public boolean matches(Job job) {
				return job.getClass().getName().contains(Sdk.class.getName());
			}
			
		}, MAXIMUM_SECONDS_TO_LOAD_ADT * 1000);
	}

	protected IProject importAndroidProject(String name) throws Exception {
		IProject project = importProject("projects/"+name+"/pom.xml");
		waitForJobsToComplete();
	    waitForAdtToLoad();
	    return project;
	}

	protected void buildAndroidProject(IProject project, int kind) throws CoreException, InterruptedException {
		project.build(kind, monitor);
		waitForJobsToComplete();
	}

	protected void assertClasspathContains(IJavaProject javaProject, String path) throws JavaModelException {
		for(IClasspathEntry entry : javaProject.getRawClasspath()) {
			if(entry.getPath().toOSString().contains(path)) {
				return;
			} else if(entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
				IClasspathContainer container = JavaCore.getClasspathContainer(entry.getPath(), javaProject);
                for (IClasspathEntry e : container.getClasspathEntries()) {
                	if(e.getPath().toOSString().contains(path)) {
        				return;
        			}
                }
			}
		}
		fail(path + " should be in classpath");
	}

	protected void assertClasspathDoesNotContain(IJavaProject javaProject, String path) throws JavaModelException {
		for(IClasspathEntry entry : javaProject.getRawClasspath()) {
			assertFalse(entry.getPath().toOSString().contains(path));
			if(entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
				IClasspathContainer container = JavaCore.getClasspathContainer(entry.getPath(), javaProject);
                for (IClasspathEntry e : container.getClasspathEntries()) {
        			assertFalse(path + " should not be in classpath", e.getPath().toOSString().contains(path));
                }
			}
		}
	}

	protected IClasspathEntry getClasspathContainer(IJavaProject javaProject, String id) throws JavaModelException {
		for(IClasspathEntry entry : javaProject.getRawClasspath()) {
			if(entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
            	if(entry.getPath().toOSString().equals(id)) {
    				return entry;
    			}
			}
		}
		return null;
	}
}
