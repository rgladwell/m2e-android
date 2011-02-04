package com.googlecode.eclipse.m2e.android;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.maven.ide.eclipse.MavenPlugin;
import org.maven.ide.eclipse.jdt.internal.JavaProjectConfigurator;
import org.maven.ide.eclipse.project.IMavenProjectFacade;
import org.maven.ide.eclipse.project.configurator.AbstractBuildParticipant;
import org.maven.ide.eclipse.project.configurator.AbstractLifecycleMapping;
import org.maven.ide.eclipse.project.configurator.AbstractProjectConfigurator;

public class AndroidDevelopmentToolsLifecycleMapping extends AbstractLifecycleMapping {

	public String getId() {
		return "com.googlecode.eclipse.m2e.android.androidDevelopmentToolsLifecycleMapping";
	}

	public String getName() {
		return "Android Build Lifecycle Mapping";
	}

	public List<AbstractBuildParticipant> getBuildParticipants(IMavenProjectFacade facade, IProgressMonitor monitor) throws CoreException {
	    return this.getBuildParticipants(facade, getProjectConfigurators(facade, monitor), monitor);
	}

	public List<String> getPotentialMojoExecutionsForBuildKind(IMavenProjectFacade facade, int kind, IProgressMonitor monitor) {
		return null;
	}

	public List<AbstractProjectConfigurator> getProjectConfigurators(IMavenProjectFacade facade, IProgressMonitor monitor) throws CoreException {
		List<AbstractProjectConfigurator> projectConfigurators = new ArrayList<AbstractProjectConfigurator>();
		JavaProjectConfigurator javaProjectConfigurator = new JavaProjectConfigurator();
		javaProjectConfigurator.setConsole(MavenPlugin.getDefault().getConsole());
		projectConfigurators.add(javaProjectConfigurator);
		if(AndroidMavenPluginUtil.isAndroidProject(facade.getMavenProject())) {
			projectConfigurators.add(new AndroidDevelopmentToolsProjectConfigurator());
		}
		return projectConfigurators;
	}

}
