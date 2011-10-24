package me.gladwell.eclipse.m2e.android.configuration;

import me.gladwell.eclipse.m2e.android.model.AndroidProject;

import org.eclipse.m2e.jdt.IClasspathDescriptor;

public interface ClasspathConfigurer {

	public void configure(AndroidProject project, IClasspathDescriptor classpath) throws Exception;

}
