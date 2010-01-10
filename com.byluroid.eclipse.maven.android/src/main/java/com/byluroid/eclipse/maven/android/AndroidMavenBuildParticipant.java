package com.byluroid.eclipse.maven.android;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.maven.ide.eclipse.project.configurator.AbstractBuildParticipant;

public class AndroidMavenBuildParticipant extends AbstractBuildParticipant {

	@Override
	public Set<IProject> build(int kind, IProgressMonitor monitor) throws Exception {
		if(IncrementalProjectBuilder.FULL_BUILD == kind) {
			System.setProperty("android.sdk.path", "/opt/android-sdk-linux/");
		}

		return null;
	}

}
