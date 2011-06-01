
package com.googlecode.eclipse.m2e.android.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com.android.ide.eclipse.adt.AndroidConstants;
import com.github.android.tools.CommandLineAndroidTools;
import com.github.android.tools.DexService;
import com.github.android.tools.model.ClassDescriptor;
import com.github.android.tools.model.DexInfo;
import com.github.android.tools.model.PackageInfo;
import com.googlecode.eclipse.m2e.android.AndroidMavenPluginUtil;

/**
 * Test suite for configuring and building Android applications.
 * 
 * @author Ricardo Gladwell <ricardo.gladwell@gmail.com>
 */
public class ApplicationAndroidMavenPluginTest extends AndroidMavenPluginTestCase {

	private static final String ANDROID_15_PROJECT_NAME = "apidemos-15-app";

	private IProject project;
	private DexService dexInfoService;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		deleteProject(ANDROID_15_PROJECT_NAME);
		project = importProject("projects/"+ANDROID_15_PROJECT_NAME+"/pom.xml");
		waitForJobsToComplete();
	    
	    dexInfoService = new CommandLineAndroidTools();
	}

	public void testConfigure() throws Exception {
		assertNoErrors(project);
	}

	public void testConfigureAddsAndroidNature() throws Exception {
	    assertTrue("configurer failed to add android nature", project.hasNature(AndroidConstants.NATURE_DEFAULT));
	}

	public void testConfigureSetsAndroidOutputLocation() throws Exception {
		IJavaProject javaProject = JavaCore.create(project);
		assertEquals("failed to set output location", javaProject.getOutputLocation().toString(), "/"+ANDROID_15_PROJECT_NAME+"/target/android-classes");
	}

	public void testConfigureRemovesApkBuilder() throws Exception {
		assertFalse("project contains redundant APKBuilder build command", containsApkBuildCommand(project));
	}

	public void testConfigureDoesNotAddTargetDirectoryToClasspath() throws Exception {
		IJavaProject javaProject = JavaCore.create(project);
		for(IClasspathEntry entry : javaProject.getRawClasspath()) {
			assertFalse("classpath contains reference to target directory: cause infinite build loops and build conflicts", entry.getPath().toOSString().contains("target"));
		}
	}

	public void testBuild() throws Exception {
		buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);
		assertTrue("destination apk not successfully built and copied", AndroidMavenPluginUtil.getApkFile(project).exists());
	}
	
	public void testBuildAddedDependenciesToAPK() throws Exception {
		buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

		DexInfo dexInfo = dexInfoService.getDexInfo(AndroidMavenPluginUtil.getApkFile(project));
		PackageInfo packageInfo = new PackageInfo();
		packageInfo.setName("org.apache.commons.lang");
		ClassDescriptor stringUtils = new ClassDescriptor();
		stringUtils.setName("StringUtils");
		stringUtils.setPackageInfo(packageInfo);
		assertTrue("external dep class=["+stringUtils+"] not found in file=["+AndroidMavenPluginUtil.getApkFile(project)+"]", dexInfo.getClassDescriptors().contains(stringUtils));
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
