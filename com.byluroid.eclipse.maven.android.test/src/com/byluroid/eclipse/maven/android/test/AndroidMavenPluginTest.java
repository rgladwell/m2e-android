package com.byluroid.eclipse.maven.android.test;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.maven.ide.eclipse.project.ResolverConfiguration;
import org.maven.ide.eclipse.tests.common.AbstractMavenProjectTestCase;

import com.android.ide.eclipse.adt.AndroidConstants;
import com.byluroid.eclipse.maven.android.AndroidMavenPluginUtil;

public class AndroidMavenPluginTest extends AbstractMavenProjectTestCase {

	private static final String ANDROID_11_PROJECT_NAME = "apidemos-11-app";
	private static final String ANDROID_15_PROJECT_NAME = "apidemos-15-app";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@SuppressWarnings("restriction")
	public void testConfigureForAndroid11() throws Exception {
		deleteProject(ANDROID_11_PROJECT_NAME);
		IProject project = importProject("projects/"+ANDROID_11_PROJECT_NAME+"/pom.xml",  new ResolverConfiguration());
		waitForJobsToComplete();

		assertTrue("failed to add android nature", project.hasNature(AndroidConstants.NATURE));

		IJavaProject javaProject = JavaCore.create(project);
		assertEquals("failed to set output location", javaProject.getOutputLocation().toString(), "/"+ANDROID_11_PROJECT_NAME+"/target/android-classes");
		IClasspathEntry genSourceEntry =  AndroidMavenPluginUtil.getGenSourceEntry(javaProject.getRawClasspath());
		assertNotNull("failed to add gen source folder", genSourceEntry);
		assertEquals("failed to set output location for gen folder", genSourceEntry.getOutputLocation().toString(), "/"+ANDROID_11_PROJECT_NAME+"/target/android-classes");
	}

	@SuppressWarnings("restriction")
	public void testConfigureForAndroid15() throws Exception {
		deleteProject(ANDROID_15_PROJECT_NAME);
		IProject project = importProject("projects/" + ANDROID_15_PROJECT_NAME+"/pom.xml",  new ResolverConfiguration());
		waitForJobsToComplete();

		assertTrue("configurer failed to add android nature", project.hasNature(AndroidConstants.NATURE));

		IJavaProject javaProject = JavaCore.create(project);
		assertEquals("failed to set output location", javaProject.getOutputLocation().toString(), "/"+ANDROID_15_PROJECT_NAME+"/target/android-classes");
		IClasspathEntry genSourceEntry =  AndroidMavenPluginUtil.getGenSourceEntry(javaProject.getRawClasspath());
		assertNotNull("failed to add gen source folder", genSourceEntry);
		assertEquals("failed to set output location for gen folder", genSourceEntry.getOutputLocation().toString(), "/"+ANDROID_15_PROJECT_NAME+"/target/android-classes");

//		project.build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);
//		waitForJobsToComplete();
	}

}
