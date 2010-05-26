package com.urbanmania.eclipse.maven.android;

import java.io.File;

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

}
