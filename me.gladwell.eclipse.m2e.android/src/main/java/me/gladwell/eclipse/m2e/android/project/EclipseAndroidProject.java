/*******************************************************************************
 * Copyright (c) 2012, 2013, 2014, 2015 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.jdt.IClasspathDescriptor;

import com.android.SdkConstants;
import com.android.sdklib.internal.project.ProjectProperties;
import com.android.sdklib.internal.project.ProjectPropertiesWorkingCopy;

public class EclipseAndroidProject implements IDEAndroidProject {

    private final ToolkitAdaptor toolkit;
    private final IProject project;
    private final IWorkspace workspace;
    private final IMavenProjectRegistry registry;
    private IClasspathDescriptor classpath;

    public EclipseAndroidProject(ToolkitAdaptor toolkit, IMavenProjectRegistry registry, IWorkspace workspace, IProject project) {
        this.toolkit = toolkit;
        this.workspace = workspace;
        this.project = project;
        this.registry = registry;
    }

    public EclipseAndroidProject(ToolkitAdaptor toolkit, IMavenProjectRegistry registry, IProject project, IClasspathDescriptor classpath) {
        this(toolkit, registry, project.getWorkspace(), project);
        this.classpath = classpath;
    }

    public IProject getProject() {
        return project;
    }

    public boolean isAndroidProject() {
        try {
            return project.getProject().hasNature(toolkit.nature());
        } catch (CoreException e) {
            throw new ProjectConfigurationException(e);
        }
    }

    public void setAndroidProject(boolean androidProject) {
        try {
            if (androidProject) {
                AbstractProjectConfigurator.addNature(project, toolkit.nature(), null);
            } else {
                throw new UnsupportedOperationException();
            }
        } catch (CoreException e) {
            throw new ProjectConfigurationException(e);
        }
    }

    public boolean isLibrary() {
        return toolkit.isLibrary(project);
    }

    public void setLibrary(boolean isLibrary) {
        setAndroidProperty(ProjectProperties.PROPERTY_LIBRARY, Boolean.toString(isLibrary));
    }

    public void setLibraryDependencies(List<IDEAndroidProject> libraryDependencies) {
        int i = 1;
        for (IDEAndroidProject library : libraryDependencies) {
            toolkit.setAndroidProperty(project, library, ProjectPropertiesWorkingCopy.PROPERTY_LIB_REF + i,
                    relativizePath(library, getProject()));
            i++;
        }
    }

    private String relativizePath(IDEAndroidProject libraryProject, IProject baseProject) {
        IPath libraryProjectLocation = libraryProject.getProject().getLocation();
        IPath targetProjectLocation = baseProject.getLocation();
        return libraryProjectLocation.makeRelativeTo(targetProjectLocation).toPortableString();
    }

    private void setAndroidProperty(String property, String value) {
        toolkit.setAndroidProperty(project, null, property, value);
    }

    public void fixProject() {
        toolkit.fixProject(project);
    }

    public boolean isMavenised() {
        try {
            return project.getProject().hasNature(IMavenConstants.NATURE_ID);
        } catch (CoreException e) {
            throw new ProjectConfigurationException(e);
        }
    }

    public IFile getPom() {
        return project.getFile("pom.xml");
    }

    public void setAssetsDirectory(File assets) {
        IFolder link = project.getFolder(toolkit.assetsFolder());
        linkResource(link, assets);
    }

    public void setResourceFolder(File resourceDirectory) {
        IFolder link = project.getFolder(SdkConstants.FD_RES);
        linkResource(link, resourceDirectory);
    }

    public void setAndroidManifest(File androidManifestFile) {
        IFile link = project.getFile(SdkConstants.FN_ANDROID_MANIFEST_XML);
        linkResource(link, androidManifestFile);
    }

    public Classpath getClasspath() {
        return new EclipseClasspath(toolkit, JavaCore.create(project), classpath);
    }

    // TODO move this method to `EclipseAndroidWorkspace`.
    private void linkResource(IResource resource, File newFile) {
        try {
            if (resource.isLinked()) {
                resource.delete(0, null);
            }

            if (!resource.getLocation().toFile().equals(newFile) && newFile.exists()) {
                IPath newPath = null;
                
                if (project.getLocation().isPrefixOf(new Path(newFile.getPath()))) {
                    newPath = new Path("PROJECT_LOC").append(new Path(newFile.getPath()).makeRelativeTo(project.getLocation()));
                } else {
                    newPath = new Path(newFile.getPath());
                }
                
                IStatus status = workspace.validateLinkLocation(resource, newPath);
                if (!status.matches(Status.ERROR)) {
                    createLink(resource, newPath);
                } else {
                    throw new ProjectConfigurationException("invalid location for link=[" + resource + "]");
                }
            }

        } catch (CoreException e) {
            throw new ProjectConfigurationException("cannot update link=[" + resource + "]", e);
        }
    }

    private static void createLink(IResource resource, IPath newPath) throws CoreException {
        if (resource instanceof IFile) {
            ((IFile) resource).createLink(newPath, IResource.REPLACE, null);
        } else if (resource instanceof IFolder) {
            ((IFolder) resource).createLink(newPath, IResource.REPLACE, null);
        } else {
            throw new ProjectConfigurationException("resource is not a file or a folder=[" + resource + "]");
        }
    }

    public boolean shouldResolveWorkspaceProjects() {
        return registry.getProject(project).getResolverConfiguration().shouldResolveWorkspaceProjects();
    }

    public String getPath() {
        return project.getFullPath().toString();
    }

    // TODO refactor: method is too long and deeply nested.
    public void orderBuilders() {
        try {
            IProjectDescription description = project.getDescription();
            List<ICommand> buildCommands = Arrays.asList(description.getBuildSpec());

            Collections.sort(buildCommands, new Comparator<ICommand>() {
                public int compare(ICommand command1, ICommand command2) {
                    if (IMavenConstants.BUILDER_ID.equals(command1.getBuilderName())
                            && toolkit.builder().equals(command2.getBuilderName())) {
                        return 1;
                    } else if (toolkit.builder().equals(command1.getBuilderName())
                            && IMavenConstants.BUILDER_ID.equals(command2.getBuilderName())) {
                        return -1;
                    }

                    return 0;
                }
            });

            ICommand[] buildSpec = buildCommands.toArray(new ICommand[0]);
            description.setBuildSpec(buildSpec);
            project.setDescription(description, null);
        } catch (CoreException e) {
            throw new ProjectConfigurationException(e);
        }
    }

}
