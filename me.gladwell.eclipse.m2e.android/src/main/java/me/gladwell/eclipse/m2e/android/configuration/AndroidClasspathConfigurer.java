package me.gladwell.eclipse.m2e.android.configuration;

import me.gladwell.eclipse.m2e.android.project.AndroidProject;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.m2e.jdt.IClasspathDescriptor;

public interface AndroidClasspathConfigurer {

	public void addGenFolder(IJavaProject javaProject, AndroidProject project, IClasspathDescriptor classpath);
	public void removeNonRuntimeDependencies(AndroidProject project, IClasspathDescriptor classpath);
	public void modifySourceFolderOutput(IJavaProject javaProject, AndroidProject project, IClasspathDescriptor classpath);

}
