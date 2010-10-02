package com.urbanmania.eclipse.maven.android;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.ClasspathEntry;
import org.maven.ide.eclipse.project.configurator.AbstractBuildParticipant;
import org.maven.ide.eclipse.project.configurator.AbstractProjectConfigurator;
import org.maven.ide.eclipse.project.configurator.ProjectConfigurationRequest;

import com.android.ide.eclipse.adt.AdtPlugin;
import com.android.ide.eclipse.adt.AndroidConstants;
import com.android.ide.eclipse.adt.internal.build.ApkBuilder;

public class AndroidDevelopmentToolsProjectConfigurator extends AbstractProjectConfigurator {

	private static final String ANDROID_GEN_PATH = "gen";

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
			
			IClasspathEntry[] oldClasspath = javaProject.getRawClasspath();
			List<IClasspathEntry> newClasspath = new ArrayList<IClasspathEntry>();
			IPath genPath = javaProject.getPath().append(ANDROID_GEN_PATH);
			boolean foundGenPath = false;

			for(IClasspathEntry entry : oldClasspath) {
				if(!entry.getPath().toString().contains("target")) {
					newClasspath.add(entry);
				} 

				if (entry.getPath().equals(genPath)) {
					foundGenPath = true;
				}
			}

			if(!foundGenPath) {
				final File genFolder = genPath.toFile();
				if(!genFolder.exists()) {
					genFolder.mkdirs();
				}

				IClasspathEntry genClasspathEntry = new ClasspathEntry(IPackageFragmentRoot.K_SOURCE, IClasspathEntry.CPE_SOURCE, genPath, ClasspathEntry.INCLUDE_ALL, ClasspathEntry.EXCLUDE_NONE, null, null, null, false, null, false, new IClasspathAttribute[0]);
				newClasspath.add(genClasspathEntry);
			}

			javaProject.setRawClasspath(newClasspath.toArray(new IClasspathEntry[newClasspath.size()]), monitor);
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
