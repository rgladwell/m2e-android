package me.gladwell.eclipse.m2e.android.project;

import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;

import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.m2e.core.embedder.MavenModelManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MavenToEclipseAndroidProjectConverter implements AndroidProjectFactory<MavenAndroidProject, EclipseAndroidProject> {

	private AndroidProjectFactory<MavenAndroidProject, MavenProject> mavenProjectFactory;
	private MavenModelManager mavenModelManager;

	@Inject
	public MavenToEclipseAndroidProjectConverter(
			AndroidProjectFactory<MavenAndroidProject, MavenProject> mavenProjectFactory,
			MavenModelManager mavenModelManager
	) {
		super();
		this.mavenProjectFactory = mavenProjectFactory;
		this.mavenModelManager = mavenModelManager;
	}

	public MavenAndroidProject createAndroidProject(EclipseAndroidProject androidProject) {
		Model model;
		try {
			model = mavenModelManager.readMavenModel(androidProject.getPom());
		} catch (CoreException e) {
			throw new ProjectConfigurationException(e);
		}
		MavenProject project = new MavenProject(model);
		return mavenProjectFactory.createAndroidProject(project);
	}

}
