/*******************************************************************************
 * Copyright (c) 2012 Tomas Prochazka
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.quickfix;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

import javax.swing.ProgressMonitor;

import me.gladwell.eclipse.m2e.android.configuration.DependencyNotFoundInWorkspace;
import me.gladwell.eclipse.m2e.android.project.AndroidProjectFactory;
import me.gladwell.eclipse.m2e.android.project.AndroidWorkspace;
import me.gladwell.eclipse.m2e.android.project.Dependency;
import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.project.JaywayMavenAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenDependency;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringInputStream;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.MavenModelManager;
import org.eclipse.m2e.core.project.MavenProjectInfo;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.progress.ProgressManagerUtil;

import com.google.inject.Inject;


/**
 * This class allow unpack and import one or more apklibs dependencies to workspace.
 * 
 * @author Tomáš Procházka &lt;<a href="mailto:tomas.prochazka@gmail.com">tomas.prochazka@gmail.com</a>&gt;
 */
public class UnzipApkLibsDependciesSupport {

	private IWorkspace workspace;
	private MavenModelManager mavenModelManager;
	private AndroidProjectFactory<MavenAndroidProject, MavenProject> mavenProjectFactory;
	private AndroidProjectFactory<EclipseAndroidProject, IProject> projectFactory;
	private AndroidProjectFactory<MavenAndroidProject, EclipseAndroidProject> projectConverter;
	private AndroidWorkspace androidWorkspace;

	private File targetFolder;
	private ArrayList<MavenProjectInfo> mavenProjectsInfo;


	@Inject
	public UnzipApkLibsDependciesSupport(
		IWorkspace workspace,
		MavenModelManager mavenModelManager,
		AndroidProjectFactory<MavenAndroidProject, MavenProject> mavenProjectFactory,
		AndroidProjectFactory<EclipseAndroidProject, IProject> projectFactory,
		AndroidProjectFactory<MavenAndroidProject, EclipseAndroidProject> projectConverter,
		AndroidWorkspace androidWorkspace) {
		super();
		this.workspace = workspace;
		this.mavenModelManager = mavenModelManager;
		this.mavenProjectFactory = mavenProjectFactory;
		this.projectFactory = projectFactory;
		this.projectConverter = projectConverter;
		this.androidWorkspace = androidWorkspace;
		
		mavenProjectsInfo = new ArrayList<MavenProjectInfo>();
	}

	public void process(IProject iProject, Dependency apkLibDependency) throws CoreException, IOException {
		Artifact d = MavenPlugin.getMaven().resolve(apkLibDependency.getGroupId(), apkLibDependency.getArtifactId(), apkLibDependency.getVersion(), apkLibDependency.getType(), null, null, null);
		unpack(d);
		importProjects();
		updateProjectConfig(iProject);
	}

	protected void updateProjectConfig(IProject iProject) {
		EclipseAndroidProject androidProject = projectFactory.createAndroidProject(iProject);
		androidProject.setLibrary(true);
		androidProject.fixProject();
	}

	public void processAll(IProject iProject) throws CoreException, IOException {
		EclipseAndroidProject androidProject = projectFactory.createAndroidProject(iProject);
		Model mavenModel = mavenModelManager.readMavenModel(androidProject.getPom());
		MavenProject mavenProject = new MavenProject(mavenModel);

		List<org.apache.maven.model.Dependency> dep = mavenProject.getDependencies();

		for (org.apache.maven.model.Dependency dependency : dep) {
			if (dependency.getType().equals(JaywayMavenAndroidProject.ANDROID_LIBRARY_PACKAGE_TYPE)) {
				try {
					androidWorkspace.findOpenWorkspaceDependency(new MavenDependency(dependency));
				} catch (DependencyNotFoundInWorkspace ex) {
					Artifact d = MavenPlugin.getMaven().resolve(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), dependency.getType(), null, null, null);
					unpack(d);
					importProjects();
					updateProjectConfig(iProject);
				}
			}
		}
	}

	public void unpack(Artifact artifact) throws IOException, CoreException {
		File path = getTargetPath();
		targetFolder = new File(path, artifact.getGroupId() + "-" + artifact.getArtifactId() + "-" + artifact.getVersion());
		targetFolder.mkdirs();

		File apklibFile = artifact.getFile();
		File pomFile = new File(artifact.getFile().toString().replace(JaywayMavenAndroidProject.ANDROID_LIBRARY_PACKAGE_TYPE, "pom"));
		File targetPomFile = new File(targetFolder, "pom.xml");

		// unpack project itself
		JarHelper.unjar(new JarFile(apklibFile), targetFolder, null);
		// add pom.xml
		FileUtils.copyFile(pomFile, targetPomFile);
		// create project.properties file
		// TODO: Detect right androd API version
		int apiVersion = 16;
		FileUtils.fileWrite(new File(targetFolder, "project.properties"), "android.library=true\n# Project target.\ntarget=android-" + apiVersion);
		
		MavenProjectInfo i = new MavenProjectInfo("project", targetPomFile, mavenModelManager.readMavenModel(targetPomFile), null);
		mavenProjectsInfo.add(i);
	}

	public void importProjects() throws CoreException {
		ProjectImportConfiguration config = new ProjectImportConfiguration();
		config.setProjectNameTemplate("apklib-[groupId].[artifactId]-[version]");
		MavenPlugin.getProjectConfigurationManager().importProjects(mavenProjectsInfo, config, new NullProgressMonitor());
	}

	public File getTargetPath() {
		File target = workspace.getRoot().getLocation().toFile();
		return new File(target, "apklibs");
	}

}
