package me.gladwell.eclipse.m2e.android.configuration;

import java.util.List;

import me.gladwell.eclipse.m2e.android.AndroidMavenException;
import me.gladwell.eclipse.m2e.android.model.AndroidProject;

import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.m2e.jdt.IClasspathDescriptor;

public class RemoveNonCompileDependenciesConfigurer implements ClasspathConfigurer {

	private final static String MAVEN2_CLASSPATH_CONTAINER_ID = "org.eclipse.m2e.MAVEN2_CLASSPATH_CONTAINER";

	public void configure(AndroidProject project, IClasspathDescriptor classpath) throws Exception {
		final List<String> compileDependencies = project.getRuntimeDependencies();
		final IClasspathContainer mavenContainer = getMavenContainer(project, classpath);
		for(IClasspathEntry entry : mavenContainer.getClasspathEntries()) {
			if(!compileDependencies.contains(entry.getPath().toOSString())) {
				classpath.removeEntry(entry.getPath());
			}
		}
	}

	private IClasspathContainer getMavenContainer(AndroidProject project, IClasspathDescriptor classpath) {
		try {
			for(IClasspathEntry entry : classpath.getEntries()) {
				if(entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
					if(entry.getPath().toString().equals(MAVEN2_CLASSPATH_CONTAINER_ID)) {
						IClasspathContainer container = JavaCore.getClasspathContainer(entry.getPath(), project.getJavaProject());
						return container;
					}
				}
			}
		} catch (JavaModelException e) {
			throw new AndroidMavenException(e);
		}
		return null;
	}
}
