package com.urbanmania.eclipse.maven.android;

import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.maven.ide.eclipse.project.configurator.AbstractBuildParticipant;
import org.maven.ide.eclipse.project.configurator.AbstractProjectConfigurator;
import org.maven.ide.eclipse.project.configurator.ProjectConfigurationRequest;

import com.android.ide.eclipse.adt.AdtPlugin;
import com.android.ide.eclipse.adt.AndroidConstants;
import com.android.ide.eclipse.adt.internal.build.ApkBuilder;

public class AndroidDevelopmentToolsProjectConfigurator extends AbstractProjectConfigurator {

	static final String ANDROID_GEN_PATH = "gen";

	@Override
	public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
		if (AndroidMavenPluginUtil.isAndroidProject(request.getMavenProject())) {
			IProject project = request.getProject();
			if (!project.hasNature(AndroidConstants.NATURE)) {
				addNature(project, AndroidConstants.NATURE, monitor);
			}
			
			// issue 6: remove redundant APKBuilder build command 
			IProjectDescription description = project.getDescription();
			List<ICommand> buildCommands = new LinkedList<ICommand>();
			for(ICommand command : description.getBuildSpec()) {
				if(!ApkBuilder.ID.equals(command.getBuilderName())) {
					buildCommands.add(command);
				}
			}

			ICommand[] buildSpec = buildCommands.toArray(new ICommand[0]);
			description.setBuildSpec(buildSpec);
			project.setDescription(description, monitor);
			
			IJavaProject javaProject = JavaCore.create(project);
			// set output location to target/android-classes so APK blob is not including in APK resources
			javaProject.setOutputLocation(javaProject.getPath().append("target").append("android-classes"), monitor);
		}
	}

	@Override
    public AbstractBuildParticipant getBuildParticipant(MojoExecution execution) {
		if(execution.getGoal().equals("compile")) {
			return new AndroidMavenBuildParticipant(execution);
		}
	    return super.getBuildParticipant(execution);
    }

}
