package com.urbanmania.eclipse.maven.android;

import java.io.File;
import java.util.List;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.android.ide.eclipse.adt.internal.build.ApkBuilder;
import com.android.ide.eclipse.adt.internal.project.ProjectHelper;

public class AndroidMavenPluginUtil {

	static final String ANDROID_PLUGIN_GROUP_ID = "com.jayway.maven.plugins.android.generation2";
	static final String ANDROID_PLUGIN_ARTIFACT_ID = "maven-android-plugin";

	public final static IClasspathEntry getGenSourceEntry(IClasspathEntry[] classpath) {
		for (IClasspathEntry entry : classpath) {
			if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE && entry.getPath().toOSString().endsWith(AndroidDevelopmentToolsProjectConfigurator.ANDROID_GEN_PATH)) {
				return entry;
			}
		}
		return null;
	}

	public final static File getApkFile(IProject project) throws JavaModelException {
		IJavaProject javaProject = JavaCore.create(project);
		File outputFolder = project.getWorkspace().getRoot().getFolder(javaProject.getOutputLocation()).getLocation().toFile();
		return new File(outputFolder, ProjectHelper.getApkFilename(project, null));
	}

	public static boolean isAndroidProject(MavenProject mavenProject) {
		List<Plugin> plugins = mavenProject.getBuildPlugins();

		for (Plugin plugin : plugins) {
			if (ANDROID_PLUGIN_GROUP_ID.equals(plugin.getGroupId()) && ANDROID_PLUGIN_ARTIFACT_ID.equals(plugin.getArtifactId())) {
				return true;
			}
		}

		return false;
	}

}
