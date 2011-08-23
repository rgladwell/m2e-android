package me.gladwell.eclipse.m2e.android.configuration;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.m2e.jdt.IClasspathDescriptor;

public interface ClasspathConfigurer {

	public void configure(IJavaProject javaProject, IClasspathDescriptor classpath) throws Exception;

}
