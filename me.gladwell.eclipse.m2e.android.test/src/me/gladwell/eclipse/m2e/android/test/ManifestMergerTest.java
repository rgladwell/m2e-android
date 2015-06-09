/*******************************************************************************
 * Copyright (c) 2012, 2013, 2014, 2015 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import static me.gladwell.eclipse.m2e.android.test.FileMatchers.containsString;
import static me.gladwell.eclipse.m2e.android.test.ProjectImporter.importAndroidTestProject;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.lang.reflect.Field;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.m2e.core.internal.lifecyclemapping.LifecycleMappingFactory;
import org.eclipse.m2e.core.project.ResolverConfiguration;

public class ManifestMergerTest extends AndroidMavenPluginTestCase {

    private static final String PROJECT_NAME = "manifestmerger";

    @Override
    protected void tearDown() throws Exception {
        setLifecycleMappingMetadata("lifecycle-mapping-metadata-empty.xml");

        super.tearDown();
    }

    public void testBuildMergesManifest() throws Exception {
        setLifecycleMappingMetadata("lifecycle-mapping-metadata.xml");

        IProject project = importAndroidProject(PROJECT_NAME);

        buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

        IFile manifest = project.getFile("bin/AndroidManifest.xml");

        assertThat(manifest, containsString("android:versionCode=\"1000000\""));
    }

    public void testBuildMergesManifestInMavenLocation() throws Exception {
        setLifecycleMappingMetadata("lifecycle-mapping-metadata.xml");

        IProject project = importAndroidProject(PROJECT_NAME);

        buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

        IFile manifest = project.getFile("target/AndroidManifest.xml");

        assertThat(manifest, containsString("android:versionCode=\"1000000\""));
    }

    public void testBuildDoesNotMergeManifestWhenConfiguredToIgnore() throws Exception {
        setLifecycleMappingMetadata("lifecycle-mapping-metadata-ignore.xml");

        IProject project = importAndroidProject(PROJECT_NAME);

        buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

        IFile manifest = project.getFile("bin/AndroidManifest.xml");

        assertThat(manifest, containsString("android:versionCode=\"1\""));
    }

    public void testBuildDoesNotMergeManifestWhenNoMergerExecutionFound() throws Exception {
        IProject project = importAndroidProject("android-application");

        buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

        IFile manifest = project.getFile("bin/AndroidManifest.xml");

        assertThat(manifest, containsString("android:versionCode=\"1\""));
    }

    public void testBuildMergesManifestWhenDestinationManifestFileEqualsAdtManifest() throws Exception {
        setLifecycleMappingMetadata("lifecycle-mapping-metadata.xml");

        ResolverConfiguration configuration = new ResolverConfiguration();
        configuration.setSelectedProfiles("destinationManifestFileBin");

        IProject project = importAndroidTestProject(PROJECT_NAME) //
                .withResolverConfiguration(configuration) //
                .into(workspace);

        buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

        IFile manifest = project.getFile("bin/AndroidManifest.xml");

        assertThat(manifest, containsString("android:versionCode=\"1000000\""));
    }

    private void setLifecycleMappingMetadata(String fileName) throws Exception {
        File mapping = new File("projects" + File.separator + PROJECT_NAME, fileName).getAbsoluteFile();

        mavenConfiguration.setWorkspaceLifecycleMappingMetadataFile(mapping.getAbsolutePath());

        // make sure m2e reloads the workspace metadata
        Field field = LifecycleMappingFactory.class.getDeclaredField("workspaceMetadataSource");
        field.setAccessible(true);
        field.set(null, null);
    }

}
