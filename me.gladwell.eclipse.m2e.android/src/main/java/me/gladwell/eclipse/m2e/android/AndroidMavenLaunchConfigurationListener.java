/*******************************************************************************
 * Copyright (c) 2014, 2015 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android;

import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_SOURCE_PATH_PROVIDER;

import java.util.ArrayList;
import java.util.List;

import me.gladwell.eclipse.m2e.android.project.IDEAndroidProject;
import me.gladwell.eclipse.m2e.android.project.IDEAndroidProjectFactory;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationListener;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectChangedListener;
import org.eclipse.m2e.core.project.MavenProjectChangedEvent;

import com.google.inject.Inject;

public class AndroidMavenLaunchConfigurationListener implements ILaunchConfigurationListener,
        IMavenProjectChangedListener {

    private static final String ANDROID_TEST_CLASSPATH_PROVIDER = "me.gladwell.m2e.android.classpathProvider";
    private static final String ANDROID_TEST_SOURCEPATH_PROVIDER = "me.gladwell.m2e.android.sourcepathProvider";

    private final IDEAndroidProjectFactory factory;

    @Inject
    public AndroidMavenLaunchConfigurationListener(IDEAndroidProjectFactory factory) {
        this.factory = factory;
    }

    public void launchConfigurationAdded(ILaunchConfiguration configuration) {
        updateLaunchConfiguration(configuration);
    }

    public void launchConfigurationChanged(ILaunchConfiguration configuration) {
        updateLaunchConfiguration(configuration);
    }

    public void launchConfigurationRemoved(ILaunchConfiguration configuration) {
    }

    private void updateLaunchConfiguration(ILaunchConfiguration configuration) {
        try {
            String projectName = configuration.getAttribute(ATTR_PROJECT_NAME, (String) null);
            IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
            IDEAndroidProject eclipseProject = factory.createAndroidProject(project);

            if (isAndroidJUnitLaunch(configuration)
                    && eclipseProject.isAndroidProject()
                    && isMavenProject(project)) {

                final ILaunchConfigurationWorkingCopy workingCopy = addCustomClasspathProvidersTo(configuration);

                new WorkspaceJob("Update launch configuration") {
                    @Override
                    public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
                        workingCopy.doSave();
                        return Status.OK_STATUS;
                    }
                }.schedule();

            }
        } catch (CoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private ILaunchConfigurationWorkingCopy addCustomClasspathProvidersTo(ILaunchConfiguration configuration) throws CoreException {
        ILaunchConfigurationWorkingCopy workingCopy = configuration.getWorkingCopy();
        workingCopy.setAttribute(ATTR_CLASSPATH_PROVIDER, ANDROID_TEST_CLASSPATH_PROVIDER);
        workingCopy.setAttribute(ATTR_SOURCE_PATH_PROVIDER, ANDROID_TEST_SOURCEPATH_PROVIDER);
        return workingCopy;
    }

    private boolean isAndroidJUnitLaunch(ILaunchConfiguration configuration) throws CoreException {
        return !configuration.getAttributes().containsValue(ANDROID_TEST_CLASSPATH_PROVIDER)
                    && configuration.getType().getIdentifier().equals("org.eclipse.jdt.junit.launchconfig");
    }

    private boolean isMavenProject(IProject project) throws CoreException {
        return project.hasNature(IMavenConstants.NATURE_ID);
    }

    public void mavenProjectChanged(MavenProjectChangedEvent[] events, IProgressMonitor monitor) {
        for (MavenProjectChangedEvent event : events) {
            try {
                switch (event.getKind()) {
                case MavenProjectChangedEvent.KIND_ADDED:
                    updateLaunchConfiguration(event.getMavenProject().getProject());
                    break;
                default:
                    break;
                }
            } catch (Exception e) {
            }
        }
    }

    private void updateLaunchConfiguration(IProject project) throws CoreException {
        for (ILaunchConfiguration config : getLaunchConfiguration(project)) {
            updateLaunchConfiguration(config);
        }
    }

    private List<ILaunchConfiguration> getLaunchConfiguration(IProject project) throws CoreException {
        List<ILaunchConfiguration> result = new ArrayList<ILaunchConfiguration>();
        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        ILaunchConfiguration[] configurations = launchManager.getLaunchConfigurations();
        for (ILaunchConfiguration config : configurations) {
            String projectName = config.getAttribute(ATTR_PROJECT_NAME, (String) null);
            if (project.getName().equals(projectName)) {
                result.add(config);
            }
        }
        return result;
    }

}
