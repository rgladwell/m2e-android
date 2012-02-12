package me.gladwell.eclipse.m2e.android.configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.gladwell.eclipse.m2e.android.model.AndroidProject;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.internal.IMavenConstants;

public class OrderBuildersProjectConfigurer implements ProjectConfigurer {

	public static final String APK_BUILDER_COMMAND_NAME = "com.android.ide.eclipse.adt.ApkBuilder";

	public boolean isConfigured(IProject project) {
		return false;
	}

	public boolean isValid(AndroidProject androidProject) {
		return true;
	}

	public void configure(IProject project, AndroidProject androidProject, IProgressMonitor monitor) {
		try {
			IProjectDescription description = project.getDescription();
			List<ICommand> buildCommands = Arrays.asList(description.getBuildSpec());

			Collections.sort(buildCommands, new Comparator<ICommand>() {
				public int compare(ICommand command1, ICommand command2) {
					if(IMavenConstants.BUILDER_ID.equals(command1.getBuilderName()) && APK_BUILDER_COMMAND_NAME.equals(command2.getBuilderName())) {
						return 1;
					} else if(APK_BUILDER_COMMAND_NAME.equals(command1.getBuilderName()) && IMavenConstants.BUILDER_ID.equals(command2.getBuilderName())) {
						return -1;
					}
	
					return 0;
				}
			});
	
			ICommand[] buildSpec = buildCommands.toArray(new ICommand[0]);
			description.setBuildSpec(buildSpec);
			project.setDescription(description, monitor);	
		} catch (CoreException e) {
			throw new ProjectConfigurationException(e);
		}
	}

}
