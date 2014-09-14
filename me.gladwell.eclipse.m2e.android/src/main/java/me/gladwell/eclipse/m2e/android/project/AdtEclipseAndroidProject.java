/*******************************************************************************
 * Copyright (c) 2012, 2013, 2014 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import java.io.File;
import java.io.IOException;
import java.util.List;

import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.jdt.IClasspathDescriptor;

import com.android.SdkConstants;
import com.android.ide.eclipse.adt.AdtConstants;
import com.android.ide.eclipse.adt.internal.project.ProjectHelper;
import com.android.ide.eclipse.adt.internal.sdk.ProjectState;
import com.android.ide.eclipse.adt.internal.sdk.Sdk;
import com.android.io.StreamException;
import com.android.sdklib.internal.project.ProjectProperties;
import com.android.sdklib.internal.project.ProjectProperties.PropertyType;
import com.android.sdklib.internal.project.ProjectPropertiesWorkingCopy;

public class AdtEclipseAndroidProject implements EclipseAndroidProject {

    private final IProject project;
    private final IWorkspace workspace;
    private final IMavenProjectRegistry registry;
    private IClasspathDescriptor classpath;

    public AdtEclipseAndroidProject(IMavenProjectRegistry registry, IWorkspace workspace, IProject project) {
        this.workspace = workspace;
        this.project = project;
        this.registry = registry;
    }

    public AdtEclipseAndroidProject(IMavenProjectRegistry registry, IProject project, IClasspathDescriptor classpath) {
        this(registry, project.getWorkspace(), project);
        this.classpath = classpath;
    }

    public IProject getProject() {
        return project;
    }

    public boolean isAndroidProject() {
        try {
            return project.getProject().hasNature(AdtConstants.NATURE_DEFAULT);
        } catch (CoreException e) {
            throw new ProjectConfigurationException(e);
        }
    }

    public void setAndroidProject(boolean androidProject) {
        try {
            if (androidProject) {
                AbstractProjectConfigurator.addNature(project, AdtConstants.NATURE_DEFAULT, null);
            } else {
                throw new UnsupportedOperationException();
            }
        } catch (CoreException e) {
            throw new ProjectConfigurationException(e);
        }
    }

    public boolean isLibrary() {
        ProjectState state = getProjectState();
        return state.isLibrary();
    }

    public void setLibrary(boolean isLibrary) {
        setAndroidProperty(ProjectProperties.PROPERTY_LIBRARY, Boolean.toString(isLibrary));
    }
    
    public void setPlatform(String platform) {
        setAndroidProperty(ProjectProperties.PROPERTY_TARGET, platform);
    }

    public void setLibraryDependencies(List<EclipseAndroidProject> libraryDependencies) {
        int i = 1;
        for (EclipseAndroidProject library : libraryDependencies) {
            setAndroidProperty(ProjectProperties.PROPERTY_LIB_REF + i, relativizePath(library, getProject()));
            i++;
            ProjectState state = getProjectState();
            state.reloadProperties();
            state.needs(Sdk.getProjectState(library.getProject()));
        }
    }

    private String relativizePath(EclipseAndroidProject libraryProject, IProject baseProject) {
        IPath libraryProjectLocation = libraryProject.getProject().getLocation();
        IPath targetProjectLocation = baseProject.getLocation();
        return libraryProjectLocation.makeRelativeTo(targetProjectLocation).toPortableString();
    }

    private void setAndroidProperty(String property, String value) {
        try {
            ProjectPropertiesWorkingCopy workingCopy = ProjectProperties.create(
                    getProject().getLocation().toOSString(), PropertyType.PROJECT);

            if (workingCopy.getFile().exists()) {
                workingCopy = ProjectProperties.load(getProject().getLocation().toOSString(), PropertyType.PROJECT)
                        .makeWorkingCopy();
            }

            workingCopy.setProperty(property, value);
            workingCopy.save();
        } catch (IOException e) {
            throw new ProjectConfigurationException(e);
        } catch (StreamException e) {
            throw new ProjectConfigurationException(e);
        }
    }

    private ProjectState getProjectState() {
        return Sdk.getProjectState(project);
    }

    public void fixProject() {
        try {
            ProjectHelper.fixProject(project);
        } catch (JavaModelException e) {
            throw new ProjectConfigurationException(e);
        }
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
        IFolder link = project.getFolder(AdtConstants.WS_ASSETS);
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
        return new MavenEclipseClasspath(JavaCore.create(project), classpath);
    }
    
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

}
