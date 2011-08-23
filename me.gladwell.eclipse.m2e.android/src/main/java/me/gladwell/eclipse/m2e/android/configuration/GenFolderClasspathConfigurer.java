package me.gladwell.eclipse.m2e.android.configuration;

import me.gladwell.eclipse.m2e.android.AndroidMavenPluginUtil;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.m2e.jdt.IClasspathDescriptor;

public class GenFolderClasspathConfigurer implements ClasspathConfigurer {

	private static final String ANDROID_GEN_PATH = "gen";

	public void configure(IJavaProject javaProject, IClasspathDescriptor classpath) throws Exception {
	    final IPath genPath = javaProject.getPath().append(ANDROID_GEN_PATH);

	    if(!classpath.containsPath(genPath)) {
	    	classpath.addSourceEntry(genPath, AndroidMavenPluginUtil.getAndroidClassesOutputFolder(javaProject), true);
	    }
	}

}
