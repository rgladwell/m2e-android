package me.gladwell.eclipse.m2e.android.configuration;

import me.gladwell.eclipse.m2e.android.model.AndroidProject;

import org.eclipse.m2e.jdt.IClasspathDescriptor;

public class GenFolderClasspathConfigurer implements ClasspathConfigurer {

	public void configure(AndroidProject project, IClasspathDescriptor classpath) throws Exception {
	    if(!classpath.containsPath(project.getGenFolder())) {
	    	classpath.addSourceEntry(project.getGenFolder(), project.getClassesOutputFolder(), true);
	    }
	}

}
