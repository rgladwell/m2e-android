package com.googlecode.eclipse.m2e.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.core.project.configurator.AbstractCustomizableLifecycleMapping;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.MojoExecutionKey;
import org.eclipse.m2e.jdt.internal.JavaProjectConfigurator;

public class AndroidDevelopmentToolsLifecycleMapping extends AbstractCustomizableLifecycleMapping {

	public String getId() {
		return "com.googlecode.eclipse.m2e.android.androidDevelopmentToolsLifecycleMapping";
	}

	public String getName() {
		return "Android Build Lifecycle Mapping";
	}

	public List<String> getPotentialMojoExecutionsForBuildKind(IMavenProjectFacade facade, int kind, IProgressMonitor monitor) {
		return null;
	}

	@Override
	public List<AbstractProjectConfigurator> getProjectConfigurators(IMavenProjectFacade facade, IProgressMonitor monitor) {
		List<AbstractProjectConfigurator> projectConfigurators = new ArrayList<AbstractProjectConfigurator>();
		JavaProjectConfigurator javaProjectConfigurator = new JavaProjectConfigurator();
		projectConfigurators.add(javaProjectConfigurator);
		if(AndroidMavenPluginUtil.getAndroidProjectType(facade.getMavenProject()) != null) {
			projectConfigurators.add(new AndroidDevelopmentToolsProjectConfigurator());
		}
		return projectConfigurators;
	}

//	@Override
//    public Map<MojoExecutionKey, List<AbstractBuildParticipant>> getBuildParticipants(IMavenProjectFacade facade, IProgressMonitor monitor) throws CoreException {
//		Map<MojoExecutionKey, List<AbstractBuildParticipant>>  buildParticipants = new HashMap<MojoExecutionKey, List<AbstractBuildParticipant>>();
//		List<AbstractBuildParticipant> buildParticipant = new ArrayList<AbstractBuildParticipant>();
//		buildParticipant.add(new AndroidMavenBuildParticipant());
//		List<String> mojoExecutions = getPotentialMojoExecutionsForBuildKind(facade, IncrementalProjectBuilder.FULL_BUILD, monitor);
//		for(String mojoExecutino : mojoExecutions) {
//			buildParticipants.put(new MojoExecutionKey(new MojoExecution()), buildParticipant);
//			
//		}
//	    return buildParticipants;
//    }

}
