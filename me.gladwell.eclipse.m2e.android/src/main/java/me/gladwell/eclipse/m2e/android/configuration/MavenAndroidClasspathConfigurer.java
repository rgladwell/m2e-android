package me.gladwell.eclipse.m2e.android.configuration;

import java.util.List;

import me.gladwell.eclipse.m2e.android.project.AndroidProject;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IClasspathDescriptor.EntryFilter;
import org.eclipse.m2e.jdt.IClasspathEntryDescriptor;

public class MavenAndroidClasspathConfigurer implements AndroidClasspathConfigurer {

	private static final String ANDROID_GEN_PATH = "gen";
	private static final String ANDROID_CLASSES_FOLDER = "bin/classes";

	public void addGenFolder(IJavaProject javaProject, AndroidProject project, IClasspathDescriptor classpath) {
		IPath gen = javaProject.getPath().append(ANDROID_GEN_PATH);
		if (!classpath.containsPath(gen)) {
			IPath classesOutput = javaProject.getPath().append(ANDROID_CLASSES_FOLDER);
			classpath.addSourceEntry(gen, classesOutput, true);
		}
	}

	public void removeNonRuntimeDependencies(AndroidProject project, IClasspathDescriptor classpath) {
		final List<String> providedDependencies = project.getProvidedDependencies();

		classpath.removeEntry(new EntryFilter() {
			public boolean accept(IClasspathEntryDescriptor descriptor) {
				return providedDependencies.contains(descriptor.getPath().toOSString());
			}
		});
	}

	public void modifySourceFolderOutput(IJavaProject javaProject, AndroidProject project, IClasspathDescriptor classpath) {
		for(IClasspathEntry entry : classpath.getEntries()) {
			if(entry.getOutputLocation() != null && entry.getEntryKind() == IClasspathEntry.CPE_SOURCE
					&& !entry.getOutputLocation().equals(javaProject.getPath().append(ANDROID_CLASSES_FOLDER))) {
				classpath.removeEntry(entry.getPath());
				classpath.addSourceEntry(entry.getPath(), javaProject.getPath().append(ANDROID_CLASSES_FOLDER), false);
			}
		}
	}

}
