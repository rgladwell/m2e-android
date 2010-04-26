package com.byluroid.eclipse.maven.android.test;

import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.maven.ide.eclipse.project.ResolverConfiguration;
import org.maven.ide.eclipse.tests.common.AbstractMavenProjectTestCase;

import com.android.ide.eclipse.adt.AndroidConstants;

import com.byluroid.eclipse.maven.android.AndroidMavenPluginUtil;

public class AndroidMavenPluginTest extends AbstractMavenProjectTestCase {

	private static final String ANDROID_11_PROJECT_NAME = "apidemos-11-app";
	private static final String ANDROID_15_PROJECT_NAME = "apidemos-15-app";
	private static final String ANDROID_15_DEPS_PROJECT_NAME = "test-android-15-deps";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testConfigureForAndroid11() throws Exception {
		deleteProject(ANDROID_11_PROJECT_NAME);
		IProject project = importProject("projects/"+ANDROID_11_PROJECT_NAME+"/pom.xml",  new ResolverConfiguration());
		waitForJobsToComplete();

		assertValidAndroidProject(project, ANDROID_11_PROJECT_NAME);
	}

	public void testConfigureForAndroid15() throws Exception {
		deleteProject(ANDROID_15_PROJECT_NAME);
		IProject project = importProject("projects/"+ANDROID_15_PROJECT_NAME+"/pom.xml",  new ResolverConfiguration());
		waitForJobsToComplete();

		assertValidAndroidProject(project, ANDROID_15_PROJECT_NAME);
	}

	public void testBuildForAndroid11() throws Exception {
		deleteProject(ANDROID_11_PROJECT_NAME);
		IProject project = importProject("projects/"+ANDROID_11_PROJECT_NAME+"/pom.xml",  new ResolverConfiguration());
		waitForJobsToComplete();

		try {
			project.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
			waitForJobsToComplete();
		} catch(ResourceException e) {
		}

		assertTrue("destination apk not successfully built and copied", AndroidMavenPluginUtil.getApkFile(project).exists());
	}

	public void testBuildForAndroid15() throws Exception {
		deleteProject(ANDROID_15_PROJECT_NAME);
		IProject project = importProject("projects/"+ANDROID_15_PROJECT_NAME+"/pom.xml",  new ResolverConfiguration());
		waitForJobsToComplete();

		try {
			project.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
			waitForJobsToComplete();
		} catch(ResourceException e) {
		}

		assertTrue("destination apk not successfully built and copied", AndroidMavenPluginUtil.getApkFile(project).exists());
	}

	public void testConfigureForAndroid15WithDependencies() throws Exception {
		deleteProject(ANDROID_15_DEPS_PROJECT_NAME);
		IProject project = importProject("projects/"+ANDROID_15_DEPS_PROJECT_NAME+"/pom.xml",  new ResolverConfiguration());
		waitForJobsToComplete();

		assertValidAndroidProject(project, ANDROID_15_DEPS_PROJECT_NAME);
	}

	public void testBuildForAndroid15WithDependencies() throws Exception {
		deleteProject(ANDROID_15_DEPS_PROJECT_NAME);
		IProject project = importProject("projects/"+ANDROID_15_DEPS_PROJECT_NAME+"/pom.xml",  new ResolverConfiguration());
		waitForJobsToComplete();

		try {
			project.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
			waitForJobsToComplete();
		} catch(ResourceException e) {
		}

		assertTrue("destination apk not successfully built and copied", AndroidMavenPluginUtil.getApkFile(project).exists());
	}

	@SuppressWarnings("restriction")
    protected void assertValidAndroidProject(IProject project, String projectName) throws CoreException, JavaModelException {
	    assertTrue("configurer failed to add android nature", project.hasNature(AndroidConstants.NATURE));
		IJavaProject javaProject = JavaCore.create(project);
		assertEquals("failed to set output location", javaProject.getOutputLocation().toString(), "/"+projectName+"/target/android-classes");
		IClasspathEntry genSourceEntry =  AndroidMavenPluginUtil.getGenSourceEntry(javaProject.getRawClasspath());
		assertNotNull("failed to add gen source folder", genSourceEntry);
		assertEquals("failed to set output location for gen folder", genSourceEntry.getOutputLocation().toString(), "/"+projectName+"/target/android-classes");
		assertNoErrors(project);
    }

}
