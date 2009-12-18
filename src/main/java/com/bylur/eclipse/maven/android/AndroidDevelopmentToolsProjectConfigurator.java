package com.bylur.eclipse.maven.android;

import java.util.List;

import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.maven.ide.eclipse.project.IMavenProjectFacade;
import org.maven.ide.eclipse.project.MavenProjectChangedEvent;
import org.maven.ide.eclipse.project.configurator.AbstractProjectConfigurator;
import org.maven.ide.eclipse.project.configurator.ProjectConfigurationRequest;

public class AndroidDevelopmentToolsProjectConfigurator extends AbstractProjectConfigurator {

	private static final String ANDROID_PLUGIN_GROUP_ID = "com.jayway.maven.plugins.android.generation2";
	private static final String ANDROID_PLUGIN_ARTIFACT_ID = "maven-android-plugin";
	private static final String ANDROID_NATURE_ID = "com.android.ide.eclipse.adt.AndroidNature";

	@Override
	public void configure(MavenEmbedder embedder, ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
		MavenProject mavenProject = request.getMavenProject();
        Plugin plugin = getAndroidPlugin(mavenProject);
        if (plugin == null) {
            return;
        }

        IProject project = request.getProject();
        if(!project.hasNature(ANDROID_NATURE_ID)) {
        	addNature(request.getProject(), ANDROID_NATURE_ID, monitor);
        }

        addProjectDependenciesAsExternals(JavaCore.create(project), mavenProject, monitor);
	}

	@Override
	protected void mavenProjectChanged(MavenProjectChangedEvent event, IProgressMonitor monitor) throws CoreException {
		super.mavenProjectChanged(event, monitor);
	    IMavenProjectFacade facade = event.getMavenProject();
        addProjectDependenciesAsExternals(JavaCore.create(facade.getProject()), facade.getMavenProject(monitor), monitor);
	}

	private Plugin getAndroidPlugin(MavenProject mavenProject) {
        List<Plugin> plugins = mavenProject.getBuildPlugins();

        for(Plugin plugin : plugins)
        {
            if (ANDROID_PLUGIN_GROUP_ID.equals(plugin.getGroupId())  && ANDROID_PLUGIN_ARTIFACT_ID.equals(plugin.getArtifactId()))
            {
                return plugin;
            }
        }

        return null;
	}

	private void addProjectDependenciesAsExternals(IJavaProject project, MavenProject mavenProject, IProgressMonitor monitor) {
		List<Dependency> dependencies = mavenProject.getDependencies();
		for(Dependency dependency : dependencies) {

		}
	}

}
