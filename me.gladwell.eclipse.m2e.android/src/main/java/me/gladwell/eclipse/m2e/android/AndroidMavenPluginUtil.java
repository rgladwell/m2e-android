/*******************************************************************************
 * Copyright (c) 2009, 2010 Ricardo Gladwell and Hugo Josefson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import me.gladwell.eclipse.m2e.android.model.ProjectType;

import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.android.ide.eclipse.adt.internal.project.ProjectHelper;

public class AndroidMavenPluginUtil {

	private static final String ANDROID_CLASSES_FOLDER = "android-classes";
	private static final String ANDROID_PACKAGE_TYPE = "apk";
	private static final String ANDROID_LIBRARY_PACKAGE_TYPE = "apklib";

	public final static File getApkFile(IProject project) throws JavaModelException {
		IJavaProject javaProject = JavaCore.create(project);
		File outputFolder = project.getWorkspace().getRoot().getFolder(javaProject.getOutputLocation()).getLocation().toFile();
		return new File(outputFolder, ProjectHelper.getApkFilename(project, null));
	}

	public static ProjectType getAndroidProjectType( MavenProject mavenProject) {
		String packaging = mavenProject.getPackaging().toLowerCase();
		if (ANDROID_PACKAGE_TYPE.equals(packaging)) {
			return ProjectType.Application;
		} else if (ANDROID_LIBRARY_PACKAGE_TYPE.equals(packaging)) {
			return ProjectType.Library;
		}
		return null;
	}

	public static IPath getAndroidClassesOutputFolder(IJavaProject javaProject) {
		return javaProject.getPath().append("target").append(ANDROID_CLASSES_FOLDER);
	}

}
