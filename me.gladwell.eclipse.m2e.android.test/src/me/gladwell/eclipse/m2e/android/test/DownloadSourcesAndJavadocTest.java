/*******************************************************************************
 * Copyright (c) 2014, 2015 Ricardo Gladwell, Csaba Kozak
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import static java.util.Arrays.asList;
import static me.gladwell.eclipse.m2e.android.test.Classpaths.findEntry;
import static org.eclipse.jdt.core.JavaCore.newLibraryEntry;
import static org.eclipse.jdt.core.JavaCore.setClasspathContainer;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.not;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.internal.preferences.MavenConfigurationImpl;
import org.eclipse.m2e.core.internal.preferences.MavenPreferenceConstants;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

@SuppressWarnings("restriction")
public class DownloadSourcesAndJavadocTest extends AndroidMavenPluginTestCase {

    private static final IPath CUSTOM_PATH = new Path("/custom-path");
    private static final String PROJECT_NAME = "android-application";

    private MavenConfigurationImpl mavenConfiguration;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mavenConfiguration = (MavenConfigurationImpl) MavenPlugin.getMavenConfiguration();
        mavenConfiguration.setDownloadSources(true);
        enableDownloadJavaDoc();
        waitForJobsToComplete(monitor);
    }

    @Override
    protected void tearDown() throws Exception {
        mavenConfiguration.setDownloadSources(false);
        disableDownloadJavaDoc();
        super.tearDown();
    }

    private void enableDownloadJavaDoc() {
        IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(IMavenConstants.PLUGIN_ID);
        preferences.putBoolean(MavenPreferenceConstants.P_DOWNLOAD_JAVADOC, true);
    }

    private void disableDownloadJavaDoc() {
        IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(IMavenConstants.PLUGIN_ID);
        preferences.putBoolean(MavenPreferenceConstants.P_DOWNLOAD_JAVADOC, false);
    }

    @Test
    public void testSourcesAttached() throws Exception {
        IProject project = importAndroidProject(PROJECT_NAME);
        IJavaProject javaProject = JavaCore.create(project);
        IClasspathEntry entry = getClasspathEntry(javaProject, CONTAINER_NONRUNTIME_DEPENDENCIES, "mockito-core-1.9.5.jar");
        assertNotNull(entry.getSourceAttachmentPath());
    }

    @Test
    public void testCustomSourcesAttached() throws Exception {
        IProject project = importAndroidProject(PROJECT_NAME);
        IJavaProject javaProject = JavaCore.create(project);

        setCustomSourceAttachment(javaProject);

        IClasspathEntry entry = getClasspathEntry(javaProject, CONTAINER_NONRUNTIME_DEPENDENCIES, "mockito-core-1.9.5.jar");

        assertEquals(CUSTOM_PATH, entry.getSourceAttachmentPath());
    }

    public void testUpdatingMavenProjectWithEntryWithCustomSourceAttachmentNotOverrideAttachment() throws Exception {
        IProject project = importAndroidProject(PROJECT_NAME);
        IJavaProject javaProject = JavaCore.create(project);

        setCustomSourceAttachment(javaProject);
        updateMavenProject(project);

        IClasspathEntry entry = getClasspathEntry(javaProject, CONTAINER_NONRUNTIME_DEPENDENCIES, "mockito-core-1.9.5.jar");

        assertEquals(CUSTOM_PATH, entry.getSourceAttachmentPath());
    }

    public void testDocumentationAttached() throws Exception {
        IProject project = importAndroidProject(PROJECT_NAME);
        IJavaProject javaProject = JavaCore.create(project);

        IClasspathEntry entry = getClasspathEntry(javaProject, CONTAINER_NONRUNTIME_DEPENDENCIES, "mockito-core-1.9.5.jar");
        assertThat(entry, hasExtraAttribute(IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME));
    }

    public void testDocumentationNotAttachedIfJavaDocDisabled() throws Exception {
        disableDownloadJavaDoc();
        IProject project = importAndroidProject(PROJECT_NAME);
        IJavaProject javaProject = JavaCore.create(project);
        IClasspathEntry entry = getClasspathEntry(javaProject, CONTAINER_NONRUNTIME_DEPENDENCIES, "mockito-core-1.9.5.jar");
        assertThat(entry, not(hasExtraAttribute(IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME)));
    }

    public void testDocumentationAttachedIfJavaDocEnabledBeforeRefresh() throws Exception {
        disableDownloadJavaDoc();
        IProject project = importAndroidProject(PROJECT_NAME);
        IJavaProject javaProject = JavaCore.create(project);
        getClasspathEntry(javaProject, CONTAINER_NONRUNTIME_DEPENDENCIES, "mockito-core-1.9.5.jar");
        enableDownloadJavaDoc();

        IClasspathEntry entry = getClasspathEntry(javaProject, CONTAINER_NONRUNTIME_DEPENDENCIES, "mockito-core-1.9.5.jar");
        assertThat(entry, hasExtraAttribute(IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME));
    }

    public void testSourcesAttachedIfEnabledBeforeRefresh() throws Exception {
        mavenConfiguration.setDownloadSources(false);
        IProject project = importAndroidProject(PROJECT_NAME);
        IJavaProject javaProject = JavaCore.create(project);
        getClasspathEntry(javaProject, CONTAINER_NONRUNTIME_DEPENDENCIES, "mockito-core-1.9.5.jar");
        mavenConfiguration.setDownloadSources(true);

        IClasspathEntry entry = getClasspathEntry(javaProject, CONTAINER_NONRUNTIME_DEPENDENCIES, "mockito-core-1.9.5.jar");
        assertNotNull(entry.getSourceAttachmentPath());
    }

    private Matcher<IClasspathEntry> hasExtraAttribute(final String expected) {
        return new BaseMatcher<IClasspathEntry>() {

            @Override
            public boolean matches(Object target) {
                IClasspathEntry entry = (IClasspathEntry) target;
                for(IClasspathAttribute attribute : entry.getExtraAttributes()) {
                    if(attribute.getName().equals(expected)) return true;
                }
                return false;
            }

            @Override
            public void describeTo(Description d) {
                d.appendText("entry with expected attribute '");
                d.appendText(expected);
                d.appendText("'");
            }
            
        };
    }

    private void setCustomSourceAttachment(IJavaProject javaProject) throws Exception {
        IClasspathContainer container = JavaCore.getClasspathContainer(new Path(CONTAINER_NONRUNTIME_DEPENDENCIES), javaProject);
        setClasspathContainer(new Path(CONTAINER_NONRUNTIME_DEPENDENCIES), new IJavaProject[] { javaProject },
                new IClasspathContainer[] { new FakeContainer(container, CUSTOM_PATH) }, monitor);
    }

    private class FakeContainer implements IClasspathContainer {

        private IClasspathContainer realContainer;
        private IPath path;

        public FakeContainer(IClasspathContainer realContainer, IPath path) {
            this.realContainer = realContainer;
            this.path = path;
        }

        @Override
        public IClasspathEntry[] getClasspathEntries() {
            List<IClasspathEntry> classpath = new ArrayList<IClasspathEntry>(asList(realContainer.getClasspathEntries()));
            IClasspathEntry mockito = findEntry(realContainer.getClasspathEntries(), "mockito-core-1.9.5.jar");

            classpath.remove(mockito);

            mockito = newLibraryEntry(mockito.getPath(), path, null,
                    mockito.getAccessRules(), mockito.getExtraAttributes(), mockito.isExported());

            classpath.add(mockito);
            return classpath.toArray(realContainer.getClasspathEntries());
        }

        @Override
        public String getDescription() {
            return realContainer.getDescription();
        }

        @Override
        public int getKind() {
            return realContainer.getKind();
        }

        @Override
        public IPath getPath() {
            return realContainer.getPath();
        }

    }

}
