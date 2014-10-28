/*******************************************************************************
 * Copyright (c) 2014 Csaba Koz��k
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import static java.io.File.separator;

import java.io.File;

import org.codehaus.plexus.util.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.m2e.core.MavenPlugin;

@SuppressWarnings("restriction")
public class RemoteAndroidDependencyTestCase extends AndroidMavenPluginTestCase {

    private static final String REPO_NAME = "test_repo";
    private static final String ANDROID_VERSION = "4.3.1_r3";
    private static final String ANDROID_JAR = "android-" + ANDROID_VERSION + ".jar";

    private String localAndroidDependencyPath;
    private String renamedLocalAndroidDependencyPath;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        String localRepositoryPath = MavenPlugin.getMaven().getLocalRepositoryPath();
        localAndroidDependencyPath = localRepositoryPath + separator + "android" + separator + "android" + separator
                + ANDROID_VERSION;
        renamedLocalAndroidDependencyPath = localRepositoryPath + separator + "android" + separator + "android"
                + separator + "renamed";

        String localAndroidJarPath = localAndroidDependencyPath + separator + ANDROID_JAR;
        String workspacePath = workspace.getRoot().getLocation().toOSString();
        String remoteRepositoryPathInProjects = "projects" + separator + REPO_NAME;
        String remoteRepositoryPath = workspacePath + separator + REPO_NAME;
        copyDir(new File(remoteRepositoryPathInProjects), new File(remoteRepositoryPath));

        String remoteAndroidJarPath = remoteRepositoryPath + separator + "android" + separator + "android" + separator
                + ANDROID_VERSION + separator + ANDROID_JAR;

        FileUtils.copyFile(new File(localAndroidJarPath), new File(remoteAndroidJarPath));

        FileUtils.rename(new File(localAndroidDependencyPath), new File(renamedLocalAndroidDependencyPath));
    }

    // TODO quarantined test: discover why this is failing on Travis and "un-ignore".
    public void test() {}

    public void ignoreTestConfigureResolvesRemoteAndroidDependency() throws Exception {
        IProject project = importAndroidProject("issue-178");

        assertNoErrors(project);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        FileUtils.deleteDirectory(new File(localAndroidDependencyPath));
        FileUtils.rename(new File(renamedLocalAndroidDependencyPath), new File(localAndroidDependencyPath));
    }

}
