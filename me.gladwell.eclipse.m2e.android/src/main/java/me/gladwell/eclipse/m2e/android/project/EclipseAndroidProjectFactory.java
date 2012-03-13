package me.gladwell.eclipse.m2e.android.project;

import org.eclipse.core.resources.IProject;


public class EclipseAndroidProjectFactory implements AndroidProjectFactory<EclipseAndroidProject, IProject> {

	public EclipseAndroidProject createAndroidProject(IProject target) {
		return new AdtEclipseAndroidProject(target);
	}

}
