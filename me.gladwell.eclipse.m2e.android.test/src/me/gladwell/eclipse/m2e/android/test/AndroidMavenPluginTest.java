/*******************************************************************************
 * Copyright (c) 2012, 2013 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import static java.io.File.separator;
import static me.gladwell.eclipse.m2e.android.configuration.Classpaths.findSourceEntry;
import static me.gladwell.eclipse.m2e.android.test.IResources.delete;
import static me.gladwell.eclipse.m2e.android.test.IResources.rename;
import static me.gladwell.eclipse.m2e.android.test.ProjectImporter.importAndroidTestProject;
import static org.eclipse.jdt.core.IClasspathAttribute.IGNORE_OPTIONAL_PROBLEMS;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com.android.SdkConstants;
import com.android.ide.eclipse.adt.AdtConstants;

@SuppressWarnings("restriction")
public class AndroidMavenPluginTest extends AndroidMavenPluginTestCase {

    private static final String SIMPLE_PROJECT_NAME = "simple-project";
    private static final String MULTIMODULE_ROOT = "projects/issue-68";

    public void testConfigureNonAndroidProject() throws Exception {
        IProject project = importAndroidProject(SIMPLE_PROJECT_NAME);

        assertFalse("configurer added android nature", project.hasNature(AdtConstants.NATURE_DEFAULT));
        IJavaProject javaProject = JavaCore.create(project);
        assertFalse("output location set to android value for non-android project", javaProject.getOutputLocation()
                .toString().equals("/" + SIMPLE_PROJECT_NAME + "/target/android-classes"));

        for (IClasspathEntry entry : javaProject.getRawClasspath()) {
            assertFalse("classpath contains reference to gen directory", entry.getPath().toOSString().contains("gen"));
        }
    }

    public void testConfigureAddsWorkspaceProjectDepsToClasspath() throws Exception {
        importAndroidProject(SIMPLE_PROJECT_NAME);
        IProject project = importAndroidProject("test-project-workspace-deps");
        assertClasspathContains(JavaCore.create(project), SIMPLE_PROJECT_NAME);
    }

    public void testNonDefaultInternalAssetsFolderCompiles() throws Exception {
        IProject[] projects = importAndroidProjects(MULTIMODULE_ROOT, new String[] { "pom.xml",
                "android-internaldirassets/pom.xml" });
        IProject project = projects[1];

        assertNoErrors(project);
    }

    public void testNonDefaultInternalAssetsLinkCreated() throws Exception {
        IProject[] projects = importAndroidProjects(MULTIMODULE_ROOT, new String[] { "pom.xml",
                "android-internaldirassets/pom.xml" });
        IProject project = projects[1];

        assertEquals(project.getLocation().append("assets2"), project.getFolder(AdtConstants.WS_ASSETS).getLocation());
    }

    public void testNonDefaultExternalAssetsFolderCompiles() throws Exception {
        IProject[] projects = importAndroidProjects(MULTIMODULE_ROOT, new String[] { "pom.xml",
                "android-relativeoutside/pom.xml" });
        IProject project = projects[1];

        assertNoErrors(project);
    }

    public void testNonDefaultExternalAssetsLinkCreated() throws Exception {
        IProject[] projects = importAndroidProjects(MULTIMODULE_ROOT, new String[] { "pom.xml",
                "android-relativeoutside/pom.xml" });
        IProject project = projects[1];

        assertEquals(projects[0].getLocation().append("outsideassets"), project.getFolder(AdtConstants.WS_ASSETS).getLocation());
    }
    
    public void testNonDefaultResourceFolderLinkCreated() throws Exception {
        IProject[] projects = importAndroidProjects(MULTIMODULE_ROOT, new String[] { "pom.xml",
        "android-internaldirassets/pom.xml" });
        IProject project = projects[1];
        
        assertEquals(project.getLocation().append("src/main/res"), project.getFolder(SdkConstants.FD_RES).getLocation());
    }
    
    public void testNonDefaultAndroidManifestLinkCreated() throws Exception {
        IProject[] projects = importAndroidProjects(MULTIMODULE_ROOT, new String[] { "pom.xml",
        "android-internaldirassets/pom.xml" });
        IProject project = projects[1];
        
        assertEquals(project.getLocation().append("src/main/AndroidManifest.xml"), project.getFile(SdkConstants.ANDROID_MANIFEST_XML).getLocation());
    }

    public void testConfigureSetsIgnoreWarningsForGenFolder() throws Exception {
        IJavaProject project = JavaCore.create(importAndroidProject("ignore-gen-warnings"));
        IClasspathEntry gen = findSourceEntry(project.getRawClasspath(), "gen");

        assertTrue("external assets folder isn't linked", booleanAttribute(IGNORE_OPTIONAL_PROBLEMS, gen));
    }

    private static final String ANDROID_CLASSES_FOLDER = "bin" + separator + "classes";

    public void testConfigureWhenProjectFolderAndNameMismatch() throws Exception {
        IProject project = importAndroidTestProject("android-application")
                                .withProjectFolder("mismatch")
                                .into(workspace);

        assertNoErrors(project);
    }

    public void testConfigureSetsCorrectOutputLocationWhenProjectFolderAndNameMismatch() throws Exception {
        IProject project = importAndroidTestProject("android-application")
                                .withProjectFolder("mismatch")
                                .into(workspace);

        IClasspathEntry[] classpath = JavaCore.create(project).getRawClasspath();
        IClasspathEntry entry = findSourceEntry(classpath, "src" + separator + "main" + separator + "java");

        assertTrue(entry.getOutputLocation().toOSString().endsWith(ANDROID_CLASSES_FOLDER));
    }

    public void testConfigureWithAndroidMavenPluginSimpligilityGroupId() throws Exception {
        IProject project = importAndroidTestProject("simpligility-groupid").withProjectFolder("simpligility-groupid")
                .into(workspace);

        assertNoErrors(project);
    }
    
    public void testProjectWithAndroidMavenPlugin4DefaultValuesCompiles() throws Exception {
        IProject project = importAndroidTestProject("android-maven-plugin-4").into(workspace);

        assertNoErrors(project);
    }

    public void testAssetsLinkCreatedWithAndroidMavenPlugin4DefaultValue() throws Exception {
        IProject project = importAndroidTestProject("android-maven-plugin-4").into(workspace);

        assertEquals(project.getLocation().append("src/main/assets"), project.getFolder(AdtConstants.WS_ASSETS).getLocation());
    }

    public void testManifestLinkCreatedWithAndroidMavenPlugin4DefaultValue() throws Exception {
        IProject project = importAndroidTestProject("android-maven-plugin-4").into(workspace);

        assertEquals(project.getLocation().append("src/main/AndroidManifest.xml"), project.getFile(SdkConstants.ANDROID_MANIFEST_XML).getLocation());
    }

    public void testResourceLinkCreatedWithAndroidMavenPlugin4DefaultValue() throws Exception {
        IProject project = importAndroidTestProject("android-maven-plugin-4").into(workspace);

        assertEquals(project.getLocation().append("src/main/res"), project.getFolder(SdkConstants.FD_RES).getLocation());
    }

    public void testResourceUnlinkedOnRevertToADTDefaults() throws Exception {
        IProject project = importAndroidTestProject("android-maven-plugin-4").into(workspace);

        delete(project.getFile("pom.xml"));

        rename(project.getFile("pom_old_res.xml"), project.getFile("pom.xml"));
        rename(project.getLocation().append("src").append("main").append("res"), project.getLocation().append("res"));

        updateConfiguration(project);

        assertFalse("resource folder is linked", project.getFolder("res").isLinked());
    }

    public void testAssetsUnlinkedOnRevertToADTDefaults() throws Exception {
        IProject project = importAndroidTestProject("android-maven-plugin-4").into(workspace);

        delete(project.getFile("pom.xml"));

        rename(project.getFile("pom_old_res.xml"), project.getFile("pom.xml"));
        rename(project.getLocation().append("src").append("main").append("assets"), project.getLocation().append("assets"));

        updateConfiguration(project);

        assertFalse("assets folder is linked", project.getFolder("assets").isLinked());
    }

    public void testManifestUnlinkedOnRevertToADTDefaults() throws Exception {
        IProject project = importAndroidTestProject("android-maven-plugin-4").into(workspace);

        project.getFile("pom.xml").delete(true, null);

        rename(project.getFile("pom_old_res.xml"), project.getFile("pom.xml"));
        rename(project.getLocation().append("src").append("main").append("AndroidManifest.xml"),
                project.getLocation().append("AndroidManifest.xml"));

        updateConfiguration(project);

        assertFalse("manifest is linked", project.getFile("AndroidManifest.xml").isLinked());
    }
    
    public void testNonExistingResourcesAreNotLinked() throws Exception {
        IProject project = importAndroidTestProject("simpligility-groupid").into(workspace);
        
        assertFalse("link created for non-existing assets folder", project.getFolder(AdtConstants.WS_ASSETS).exists());
    }

    public void testProjectRelativeLinksCreatedWhenTargetIsInsideProjectFolder() throws Exception {
        IProject project = importAndroidTestProject("android-maven-plugin-4").into(workspace);
        
        assertEquals("PROJECT_LOC", project.getFile(AdtConstants.WS_ASSETS).getRawLocation().segment(0));
    }
    
    public void testAbsoluteLinksCreatedWhenTargetIsOutsideProjectFolder() throws Exception {
        IProject[] projects = importAndroidProjects(MULTIMODULE_ROOT, new String[] { "pom.xml",
        "android-relativeoutside/pom.xml" });
        IProject project = projects[1];
        
        assertFalse("invalid relative link created",project.getFile(AdtConstants.WS_ASSETS).getRawLocation().segment(0).equals("PROJECT_LOC"));
    }

}
