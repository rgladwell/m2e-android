package me.gladwell.eclipse.m2e.android;

import org.eclipse.core.resources.IProject;

import me.gladwell.eclipse.m2e.android.model.AdtEclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.model.EclipseAndroidProject;

public class EclipseAndroidProjectFactory implements AndroidProjectFactory<EclipseAndroidProject, IProject> {

	public EclipseAndroidProject createAndroidProject(IProject target) {
		return new AdtEclipseAndroidProject(target);
	}

}
