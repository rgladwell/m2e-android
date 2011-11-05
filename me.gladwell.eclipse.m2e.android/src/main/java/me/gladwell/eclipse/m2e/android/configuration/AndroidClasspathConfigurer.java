package me.gladwell.eclipse.m2e.android.configuration;

import me.gladwell.eclipse.m2e.android.model.AndroidProject;

import org.eclipse.m2e.jdt.IClasspathDescriptor;

public interface AndroidClasspathConfigurer {

	public void addGenFolder(AndroidProject project, IClasspathDescriptor classpath);
	public void removeNonRuntimeDependencies(AndroidProject project, IClasspathDescriptor classpath);
	public void modifySourceFolderOutput(AndroidProject project, IClasspathDescriptor classpath);

}
