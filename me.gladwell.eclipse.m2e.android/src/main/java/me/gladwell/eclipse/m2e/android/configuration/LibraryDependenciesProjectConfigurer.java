/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.InputLocation;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.internal.markers.IMavenMarkerManager;
import org.eclipse.m2e.core.internal.markers.MarkerUtils;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.ui.internal.views.markers.MarkerLocationField;

import com.google.inject.Inject;

import me.gladwell.eclipse.m2e.android.AndroidMavenPlugin;
import me.gladwell.eclipse.m2e.android.project.AndroidWorkspace;
import me.gladwell.eclipse.m2e.android.project.Dependency;
import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

public class LibraryDependenciesProjectConfigurer implements ProjectConfigurer {

	private AndroidWorkspace workspace;
	private IMavenMarkerManager markerManager;
	private ProjectConfigurationRequest request;

	@Inject
	public LibraryDependenciesProjectConfigurer(AndroidWorkspace workspace) {
		super();
		this.workspace = workspace;
	}

	public boolean isConfigured(EclipseAndroidProject project) {
		return false;
	}

	public boolean isValid(MavenAndroidProject project) {
		return !project.getLibraryDependencies().isEmpty();
	}

	public void setupEnvironment(ProjectConfigurationRequest request, IMavenMarkerManager markerManager) {
		this.markerManager = markerManager;
		this.request = request;
	}

	public void configure(EclipseAndroidProject eclipseProject, MavenAndroidProject mavenProject) {
		try {
			markerManager.deleteMarkers(request.getPom(), AndroidMavenPlugin.APKLIB_ERROR_TYPE);
		} catch (CoreException ex) {
			ex.printStackTrace(); // TODO replace by Eclipse logging system
		}

		List<Dependency> libraryDependencies = mavenProject.getLibraryDependencies();
		List<EclipseAndroidProject> workspaceDependencies = new ArrayList<EclipseAndroidProject>();

		for (Dependency dependency : libraryDependencies) {
			try {
				EclipseAndroidProject workspaceDependency = workspace.findOpenWorkspaceDependency(dependency);
				workspaceDependencies.add(workspaceDependency);

			} catch (DependencyNotFoundInWorkspace e) {
				// looking for line number of missing dependency
				InputLocation location = null;
				List<org.apache.maven.model.Dependency> deps = request.getMavenProject().getModel().getDependencies();
				for (org.apache.maven.model.Dependency d : deps) {
					if (d.getGroupId().equals(e.getDependency().getGroupId()) &&
						d.getArtifactId().equals(e.getDependency().getArtifactId()) &&
						d.getVersion().equals(e.getDependency().getVersion())) {

						location = d.getLocation("groupId");
					}
				}
				if (location != null) {
					IMarker marker = markerManager.addMarker(
						request.getPom(), e.getType(), e.getMessage(), location.getLineNumber(), IMarker.SEVERITY_ERROR);
					
					try {
						marker.setAttribute("group", e.getDependency().getGroupId());
						marker.setAttribute("name", e.getDependency().getArtifactId());
						marker.setAttribute("type", e.getDependency().getType());
						marker.setAttribute("version", e.getDependency().getVersion());
						marker.setAttribute(IMavenConstants.MARKER_COLUMN_START, location.getColumnNumber());
						int endColumn = location.getColumnNumber() + e.getDependency().getGroupId().length();
						marker.setAttribute(IMavenConstants.MARKER_COLUMN_END, endColumn);
						MarkerUtils.decorateMarker(marker);

					} catch (CoreException ex) {
						ex.printStackTrace(); // TODO replace by Eclipse logging system
					}
				}
			}
		}

		eclipseProject.setLibraryDependencies(workspaceDependencies);
	}

}
