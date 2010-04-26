package com.byluroid.eclipse.maven.android;

import org.eclipse.jdt.core.IClasspathEntry;

public class AndroidMavenPluginUtil {

	public final static IClasspathEntry getGenSourceEntry(IClasspathEntry[] classpath) {
		for (IClasspathEntry entry : classpath) {
			if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE && entry.getPath().toOSString().endsWith(AndroidDevelopmentToolsProjectConfigurator.ANDROID_GEN_PATH)) {
				return entry;
			}
		}
		return null;
	}

}
