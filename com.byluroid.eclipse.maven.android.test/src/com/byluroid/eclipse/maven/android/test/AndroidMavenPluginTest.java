package com.byluroid.eclipse.maven.android.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.maven.ide.eclipse.project.ResolverConfiguration;
import org.maven.ide.eclipse.tests.common.AbstractMavenProjectTestCase;

import com.android.ide.eclipse.adt.AdtPlugin;
import com.android.ide.eclipse.adt.AndroidConstants;
import com.android.ide.eclipse.adt.internal.preferences.AdtPrefs;
import com.android.ide.eclipse.adt.internal.sdk.LoadStatus;

import com.byluroid.eclipse.maven.android.AndroidMavenPluginUtil;

public class AndroidMavenPluginTest extends AbstractMavenProjectTestCase {

	private static final String ANDROID_11_PROJECT_NAME = "apidemos-11-app";
	private static final String ANDROID_15_PROJECT_NAME = "apidemos-15-app";
	private static final String ANDROID_15_DEPS_PROJECT_NAME = "test-android-15-deps";
	private static final String SIMPLE_PROJECT_NAME = "simple-project";
	private static final String ISSUE_7_PROJECT_NAME = "issue-7";
	private static final String ISSUE_6_PROJECT_NAME = "issue-6";

	protected AdtPlugin adtPlugin;

    @Override
	@SuppressWarnings("restriction")
    protected void setUp() throws Exception {
	    adtPlugin = AdtPlugin.getDefault();
	    String androidHome = System.getenv("ANDROID_HOME");
	    
	    if(androidHome != null && !androidHome.equals(adtPlugin.getOsSdkFolder())) {
		    adtPlugin.getPreferenceStore().setValue(AdtPrefs.PREFS_SDK_DIR, androidHome);
		    adtPlugin.savePluginPreferences();
	    }
	    
	    while(!adtPlugin.getSdkLoadStatus().equals(LoadStatus.LOADED)) {
	    	Thread.sleep(1000);
	    }

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

		buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

		assertTrue("destination apk not successfully built and copied", AndroidMavenPluginUtil.getApkFile(project).exists());
	}

	public void testBuildForAndroid15() throws Exception {
		deleteProject(ANDROID_15_PROJECT_NAME);
		IProject project = importProject("projects/"+ANDROID_15_PROJECT_NAME+"/pom.xml",  new ResolverConfiguration());
		waitForJobsToComplete();

		buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

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

		buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

		assertTrue("destination apk not successfully built and copied", AndroidMavenPluginUtil.getApkFile(project).exists());
	}

	public void testConfigureNonAndroidProject() throws Exception {
		deleteProject(SIMPLE_PROJECT_NAME);
		IProject project = importProject("projects/"+SIMPLE_PROJECT_NAME+"/pom.xml",  new ResolverConfiguration());
		waitForJobsToComplete();

	    assertFalse("configurer added android nature", project.hasNature(AndroidConstants.NATURE));
		IJavaProject javaProject = JavaCore.create(project);
		assertFalse("set output location", javaProject.getOutputLocation().toString().equals("/"+SIMPLE_PROJECT_NAME+"/target/android-classes"));
		assertNull("added gen source folder", AndroidMavenPluginUtil.getGenSourceEntry(javaProject.getRawClasspath()));
	}

	/**
	 * @see http://code.google.com/p/m2eclipse-android-integration/issues/detail?id=7
	 */
	public void testConfigureDoesNotAffectNonAndroidProjects() throws Exception {
		deleteProject(ISSUE_7_PROJECT_NAME);
		IProject project = importProject("projects/"+ISSUE_7_PROJECT_NAME+"/pom.xml",  new ResolverConfiguration());
		waitForJobsToComplete();

	    assertFalse("configurer added android nature", project.hasNature(AndroidConstants.NATURE));
		IJavaProject javaProject = JavaCore.create(project);
		assertFalse("set output location", javaProject.getOutputLocation().toString().equals("/"+ISSUE_7_PROJECT_NAME+"/target/android-classes"));
		assertNull("added gen source folder", AndroidMavenPluginUtil.getGenSourceEntry(javaProject.getRawClasspath()));

		boolean suplementarySourceExists = false;
		for(IClasspathEntry entry : javaProject.getRawClasspath()) {
			if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE && entry.getPath().toOSString().endsWith("src-sup")) {
				suplementarySourceExists = true;
			}
		}

		assertTrue("supplementary source folder not added", suplementarySourceExists);
	}

	public void testBuildOverwritesExistingApk() throws Exception {
		deleteProject(ANDROID_11_PROJECT_NAME);
		IProject project = importProject("projects/"+ANDROID_11_PROJECT_NAME+"/pom.xml",  new ResolverConfiguration());
		waitForJobsToComplete();

		buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

		long first = AndroidMavenPluginUtil.getApkFile(project).lastModified();

		File file = new File(project.getLocation().toFile(), "src/main/java/com/example/android/apis/ApiDemos.java");
		FileWriter fstream = new FileWriter(file,true);
        BufferedWriter out = new BufferedWriter(fstream);
	    out.write("\r\n");
	    out.close();

		buildAndroidProject(project, IncrementalProjectBuilder.AUTO_BUILD);

		long second = AndroidMavenPluginUtil.getApkFile(project).lastModified();

		assertTrue("failed to overwrite existing APK", first < second);
	}

	/**
	 * @see http://code.google.com/p/m2eclipse-android-integration/issues/detail?id=6
	 */
	public void testInfiniteBuildWithAndroid7() throws Exception {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
	    IWorkspaceDescription description = workspace.getDescription();
	    description.setAutoBuilding(true);
	    workspace.setDescription(description);

		deleteProject(ISSUE_6_PROJECT_NAME);
		IProject project = importProject("projects/"+ISSUE_6_PROJECT_NAME+"/pom.xml",  new ResolverConfiguration());
		waitForJobsToComplete();

		buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

		long first = AndroidMavenPluginUtil.getApkFile(project).lastModified();
		long second = first + 1;
		int builds = 0;

		while(first < second) {
			builds++;
			first = second;
			waitForJobsToComplete();
			second = AndroidMavenPluginUtil.getApkFile(project).lastModified();

			assertTrue(builds <= 3);
		}
	}

	@SuppressWarnings("restriction")
    protected void buildAndroidProject(IProject project, int kind) throws CoreException, InterruptedException {
	    try {
			project.build(kind, monitor);
			waitForJobsToComplete();
		} catch(ResourceException e) {
			e.printStackTrace();
		}
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
