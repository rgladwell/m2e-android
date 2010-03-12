package com.byluroid.eclipse.maven.android;

import java.io.File;
import java.util.List;

import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
//import org.maven.ide.eclipse.MavenPlugin;
import org.maven.ide.eclipse.jdt.IClasspathDescriptor;
import org.maven.ide.eclipse.jdt.IJavaProjectConfigurator;
import org.maven.ide.eclipse.project.IMavenProjectFacade;
//import org.maven.ide.eclipse.project.MavenProjectManager;
//import org.maven.ide.eclipse.project.ResolverConfiguration;
import org.maven.ide.eclipse.project.configurator.AbstractBuildParticipant;
import org.maven.ide.eclipse.project.configurator.AbstractProjectConfigurator;
import org.maven.ide.eclipse.project.configurator.ProjectConfigurationRequest;

public class AndroidDevelopmentToolsProjectConfigurator extends AbstractProjectConfigurator implements IJavaProjectConfigurator {

//	private static final String ANDROID_APK_GOAL = "android:apk";
	private static final String ANDROID_GEN_PATH = "gen";
	private static final String ANDROID_PLUGIN_GROUP_ID = "com.jayway.maven.plugins.android.generation2";
	private static final String ANDROID_PLUGIN_ARTIFACT_ID = "maven-android-plugin";
	private static final String ANDROID_NATURE_ID = "com.android.ide.eclipse.adt.AndroidNature";

	@Override
	public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
		configureAndroidMavenProject(request.getMavenProjectFacade(), monitor);
	}

	public void configureClasspath(IMavenProjectFacade facade,  IClasspathDescriptor classpath, IProgressMonitor monitor) throws CoreException {
	}

	public void configureRawClasspath(ProjectConfigurationRequest request, IClasspathDescriptor classpath, IProgressMonitor monitor) throws CoreException {
		IProject project = request.getProject();

		if (project.hasNature(ANDROID_NATURE_ID)) {
			IJavaProject javaProject = JavaCore.create(request.getProject());

			// add gen source folder if it does not already exist
			if (!hasGenSourceEntry(classpath)) {
				final File genFolder = javaProject.getPath().append(ANDROID_GEN_PATH).toFile();
				if (!genFolder.exists()){
					genFolder.mkdirs();
					request.getProject().refreshLocal(IProject.DEPTH_INFINITE, monitor);
				}
				classpath.addSourceEntry(javaProject.getPath().append(ANDROID_GEN_PATH), javaProject.getOutputLocation(), true);
			}
		}
	}

	protected void configureAndroidMavenProject(IMavenProjectFacade facade, IProgressMonitor monitor) throws CoreException {
		Plugin plugin = getAndroidPlugin(facade.getMavenProject());

		if (plugin != null) {
			IProject project = facade.getProject();
			if (!project.hasNature(ANDROID_NATURE_ID)) {
				addNature(project, ANDROID_NATURE_ID, monitor);
			}

//			MavenProjectManager projectManager = MavenPlugin.getDefault().getMavenProjectManager();
//			ResolverConfiguration configuration = facade.getResolverConfiguration();
//
//			if(!configuration.getFullBuildGoals().contains(ANDROID_APK_GOAL)) {
//				configuration.setFullBuildGoals(configuration.getFullBuildGoals() + " "+ ANDROID_APK_GOAL);
//				projectManager.setResolverConfiguration(project, configuration);
//			}

//			String outputLocation = mavenProject.getBasedir().getAbsolutePath() + File.separator + "target";
//			mavenProject.getBuild().setOutputDirectory(outputLocation);
		}
    }

	@Override
    public AbstractBuildParticipant getBuildParticipant(MojoExecution execution) {
		if(execution.getGoal().equals("compile")) {
			return new AndroidMavenBuildParticipant(execution);
		}
	    return super.getBuildParticipant(execution);
    }

	private Plugin getAndroidPlugin(MavenProject mavenProject) {
		List<Plugin> plugins = mavenProject.getBuildPlugins();

		for (Plugin plugin : plugins) {
			if (ANDROID_PLUGIN_GROUP_ID.equals(plugin.getGroupId()) && ANDROID_PLUGIN_ARTIFACT_ID.equals(plugin.getArtifactId())) {
				return plugin;
			}
		}

		return null;
	}

	private boolean hasGenSourceEntry(IClasspathDescriptor classpath) {
		for (IClasspathEntry entry : classpath.getEntries()) {
			if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE && entry.getPath().toOSString().endsWith(ANDROID_GEN_PATH)) {
				return true;
			}
		}
		return false;
	}

}
