package me.gladwell.eclipse.m2e.android.configuration;

import me.gladwell.eclipse.m2e.android.model.AndroidProject;

import org.eclipse.m2e.jdt.IClasspathDescriptor;

public interface AndroidClasspathConfigurer {

	public void addGenFolder(AndroidProject project, IClasspathDescriptor classpath);
	public void removeNonRuntimeDependencies(AndroidProject project, IClasspathDescriptor classpath);
	public void modifySourceFolderOutput(AndroidProject project, IClasspathDescriptor classpath);
	
	/**
	 * Adds a class folder for each project dependency that is not in the provided scope.
	 * 
	 * This ensures that it is included when ADT calls Dex (dex understands class folders, but not
	 * projects), so it ends up in the target APK. 
	 * 
	 * This more or less matches the behaviour or a regular Maven build.
	 * 
	 * @param project Android project
	 * @param classpath the classpath
	 */
	public void addClassFoldersForProjectDependencies(AndroidProject project, IClasspathDescriptor classpath);

}
