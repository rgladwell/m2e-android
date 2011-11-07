package me.gladwell.eclipse.m2e.android.configuration;

import java.util.List;

import me.gladwell.eclipse.m2e.android.model.AndroidProject;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IClasspathDescriptor.EntryFilter;
import org.eclipse.m2e.jdt.IClasspathEntryDescriptor;

public class MavenAndroidClasspathConfigurer implements AndroidClasspathConfigurer {

	public void addGenFolder(AndroidProject project, IClasspathDescriptor classpath) {
		if (!classpath.containsPath(project.getGenFolder())) {
			classpath.addSourceEntry(project.getGenFolder(), project.getClassesOutputFolder(), true);
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

	public void modifySourceFolderOutput(AndroidProject project, IClasspathDescriptor classpath) {
		for(IClasspathEntry entry : classpath.getEntries()) {
			if(entry.getOutputLocation() != null && entry.getEntryKind() == IClasspathEntry.CPE_SOURCE
					&& !entry.getOutputLocation().equals(project.getClassesOutputFolder())) {
				classpath.removeEntry(entry.getPath());
				classpath.addSourceEntry(entry.getPath(), project.getClassesOutputFolder(), false);
			}
		}
	}

}
