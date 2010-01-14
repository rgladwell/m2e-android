package com.byluroid.eclipse.maven.android;

import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.maven.ide.eclipse.jdt.IClasspathDescriptor;
import org.maven.ide.eclipse.jdt.IJavaProjectConfigurator;
import org.maven.ide.eclipse.project.IMavenProjectFacade;
import org.maven.ide.eclipse.project.MavenProjectChangedEvent;
import org.maven.ide.eclipse.project.configurator.AbstractProjectConfigurator;
import org.maven.ide.eclipse.project.configurator.ProjectConfigurationRequest;

import com.android.ide.eclipse.adt.AdtPlugin;

public class AndroidDevelopmentToolsProjectConfigurator extends AbstractProjectConfigurator implements IJavaProjectConfigurator {

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
			MavenProject mavenProject = request.getMavenProject();

			IJavaProject javaProject = JavaCore.create(request.getProject());

			// add gen source folder if it does not already exist
			if (!hasGenSourceEntry(classpath)) {
				classpath.addSourceEntry(javaProject.getPath().append(ANDROID_GEN_PATH), javaProject.getOutputLocation(), true);
			}

			// add compile dependencies to core build path so they are included in ADT APK build
			for (Artifact artifact : mavenProject.getArtifacts()) {
				if (artifact.getScope().equals(Artifact.SCOPE_COMPILE)) {
					classpath.addLibraryEntry(artifact, null, null, null);
				}
			}
		}
	}

	@Override
    protected void mavenProjectChanged(MavenProjectChangedEvent event,  IProgressMonitor monitor) throws CoreException {
	    configureAndroidMavenProject(event.getMavenProject(), monitor);
	}

	protected void configureAndroidMavenProject(IMavenProjectFacade facade, IProgressMonitor monitor) throws CoreException {
		Plugin plugin = getAndroidPlugin(facade.getMavenProject());

		if (plugin != null) {
			IProject project = facade.getProject();
			if (!project.hasNature(ANDROID_NATURE_ID)) {
				addNature(project, ANDROID_NATURE_ID, monitor);
			}

			String sdkPath = AdtPlugin.getOsSdkFolder().replaceAll("////", "//");

			if(sdkPath != null) {

				Xpp3Dom dom = (Xpp3Dom) plugin.getConfiguration();

				if(dom.getChild("sdk") == null) {
					Xpp3Dom sdk = new Xpp3Dom("sdk");
					dom.addChild(sdk);
				}

				if(dom.getChild("sdk").getChild("path") == null) {
					Xpp3Dom path = new Xpp3Dom("path");
					dom.getChild("sdk").addChild(path);
				}

				Xpp3Dom path = dom.getChild("sdk").getChild("path");
				if(!sdkPath.equals(path.getValue())) {
					path.setValue(sdkPath);
				}

				plugin.setConfiguration(dom);
			}

//			String outputLocation = mavenProject.getBasedir().getAbsolutePath() + File.separator + "target";
//			mavenProject.getBuild().setOutputDirectory(outputLocation);
		}
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
