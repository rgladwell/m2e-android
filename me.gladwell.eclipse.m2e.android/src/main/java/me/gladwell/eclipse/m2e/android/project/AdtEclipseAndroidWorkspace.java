package me.gladwell.eclipse.m2e.android.project;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AdtEclipseAndroidWorkspace implements AndroidWorkspace {

	private IWorkspace workspace;
	private AndroidProjectFactory<EclipseAndroidProject, IProject> projectFactory;

	@Inject
	public AdtEclipseAndroidWorkspace(IWorkspace workspace, AndroidProjectFactory<EclipseAndroidProject, IProject> projectFactory) {
		super();
		this.workspace = workspace;
		this.projectFactory = projectFactory;
	}

	public List<AndroidProject> getAndroidLibraryProjects() {
		List<AndroidProject> libraries = new ArrayList<AndroidProject>();
		for(IProject project : workspace.getRoot().getProjects()) {
			AndroidProject androidProject = projectFactory.createAndroidProject(project);
			if(androidProject.isAndroidProject() && androidProject.isLibrary()) {
				libraries.add(androidProject);
			}
		}
		return libraries;
	}

}
