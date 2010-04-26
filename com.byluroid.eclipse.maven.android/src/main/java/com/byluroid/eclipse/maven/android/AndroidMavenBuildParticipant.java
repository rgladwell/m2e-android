package com.byluroid.eclipse.maven.android;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
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

import com.android.ide.eclipse.adt.AndroidConstants;
import com.android.ide.eclipse.adt.internal.project.ApkInstallManager;

public class AndroidMavenBuildParticipant extends AbstractBuildParticipant {

	MojoExecution execution;

	public AndroidMavenBuildParticipant (MojoExecution execution) {
		this.execution = execution;
	}

	@Override
	@SuppressWarnings("restriction")
	public Set<IProject> build(int kind, IProgressMonitor monitor) throws Exception {
		final IProject project = getMavenProjectFacade().getProject();
		if(IncrementalProjectBuilder.AUTO_BUILD == kind || IncrementalProjectBuilder.CLEAN_BUILD == kind || IncrementalProjectBuilder.FULL_BUILD == kind) {
			try{
				MavenPlugin plugin = MavenPlugin.getDefault();
				MavenProjectManager projectManager = plugin.getMavenProjectManager();
				IMaven maven = plugin.getMaven();
				IFile pom = project.getFile(new Path(IMavenConstants.POM_FILE_NAME));
				IMavenProjectFacade projectFacade = projectManager.create(pom, false, monitor);
				ResolverConfiguration resolverConfiguration = projectFacade.getResolverConfiguration();
				MavenExecutionRequest request = projectManager.createExecutionRequest(pom, resolverConfiguration, monitor);

				List<String> goals = new ArrayList<String>();
				goals.add("package");
				request.setGoals(goals);

				Properties properties = request.getUserProperties();
				properties.setProperty("maven.test.skip", "true");
				request.setUserProperties(properties);

				MavenExecutionResult executionResult = maven.execute(request, monitor);

				if (executionResult.hasExceptions()){
					List<Throwable> exceptions = executionResult.getExceptions();
					for (Throwable throwable : exceptions) {
						throwable.printStackTrace();
					}
				}else{
					Artifact apkArtifact = executionResult.getProject().getArtifact();
					if (AndroidConstants.EXT_ANDROID_PACKAGE.equals(apkArtifact.getType())){
						FileUtils.copyFile(apkArtifact.getFile(), AndroidMavenPluginUtil.getApkFile(project));

						// reset the installation manager to force new installs of this project
						ApkInstallManager.getInstance().resetInstallationFor(project);
					}
				}

			}catch(Exception e){
				// TODO properly log this exception
				e.printStackTrace();
				throw e;
			}finally{
				project.refreshLocal(IProject.DEPTH_INFINITE, monitor);
			}
		}else{
			// reset the installation manager to force new installs of this project
			ApkInstallManager.getInstance().resetInstallationFor(project);
		}
		return null;
	}

}
