package com.byluroid.eclipse.maven.android;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.maven.ide.eclipse.MavenPlugin;
import org.maven.ide.eclipse.core.IMavenConstants;
import org.maven.ide.eclipse.embedder.IMaven;
import org.maven.ide.eclipse.project.IMavenProjectFacade;
import org.maven.ide.eclipse.project.MavenProjectManager;
import org.maven.ide.eclipse.project.ResolverConfiguration;
import org.maven.ide.eclipse.project.configurator.AbstractBuildParticipant;

public class AndroidMavenBuildParticipant extends AbstractBuildParticipant {

	MojoExecution execution;

	public AndroidMavenBuildParticipant (MojoExecution execution) {
		this.execution = execution;
	}

	@Override
	public Set<IProject> build(int kind, IProgressMonitor monitor) throws Exception {
		if(IncrementalProjectBuilder.CLEAN_BUILD == kind || IncrementalProjectBuilder.FULL_BUILD == kind) {
			MavenPlugin plugin = MavenPlugin.getDefault();
			MavenProjectManager projectManager = plugin.getMavenProjectManager();
			IMaven maven = plugin.getMaven();
			IProject project = getMavenProjectFacade().getProject();
			IFile pom = project.getFile(new Path(IMavenConstants.POM_FILE_NAME));
			IMavenProjectFacade projectFacade = projectManager.create(pom, false, monitor);
			ResolverConfiguration resolverConfiguration = projectFacade.getResolverConfiguration();
			MavenExecutionRequest req = projectManager.createExecutionRequest(pom, resolverConfiguration, monitor);
			List<String> goals = new ArrayList<String>();
			goals.add("android:apk");
			req.setGoals(goals);
			maven.execute(req, monitor);
		}
		return null;
	}

}
