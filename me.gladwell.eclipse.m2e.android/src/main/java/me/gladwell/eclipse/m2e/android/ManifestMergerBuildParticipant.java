/*******************************************************************************
 * Copyright (c) 2015 Ricardo Gladwell, Csaba Kozák
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android;

import java.io.File;
import java.util.Set;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;

public class ManifestMergerBuildParticipant extends MojoExecutionBuildParticipant {

    private File destination;

    public ManifestMergerBuildParticipant(MojoExecution execution, File file) {
        super(execution, false);
        this.destination = file;
    }

    @Override
    public Set<IProject> build(int kind, IProgressMonitor monitor) throws Exception {
        if (!appliesToBuildKind(kind)) {
            return null;
        }
        
        super.build(kind, monitor);
        
        IProject project = getMavenProjectFacade().getProject();
        
        IFile from = project.getFile(new Path(destination.getAbsolutePath()).makeRelativeTo(project.getLocation()));
        from.refreshLocal(IResource.DEPTH_ZERO, monitor);
        
        IFile to = project.getFile("bin/AndroidManifest.xml");
        
        if (!from.equals(to)) {
            to.setContents(from.getContents(), IResource.FORCE, monitor);
        }
        
        return null;
    }

}
