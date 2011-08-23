package me.gladwell.eclipse.m2e.android.configuration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import me.gladwell.eclipse.m2e.android.model.ProjectType;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import com.android.ide.eclipse.adt.internal.sdk.ProjectState;
import com.android.ide.eclipse.adt.internal.sdk.Sdk;

public class ConvertLibraryProjectConfigurer implements ProjectConfigurer {

	public boolean canHandle(ProjectType type, IProject project) {
		ProjectState state = Sdk.getProjectState(project);
		return type == ProjectType.Library && !state.isLibrary();
	}

	public void configure(IProject project, IProgressMonitor monitor) throws Exception {
		ProjectState state = Sdk.getProjectState(project);
		IFile defaultProperties = project.getFile("default.properties");

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter( new FileWriter(new File(defaultProperties.getRawLocation().toOSString())) );
			writer.newLine();
			writer.append("android.library=true");
		} finally {
			if(writer != null) {
				writer.close();
			}
		}

		state.reloadProperties();
	}

}
