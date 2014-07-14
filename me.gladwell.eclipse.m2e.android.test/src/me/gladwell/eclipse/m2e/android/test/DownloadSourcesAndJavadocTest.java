/*******************************************************************************
 * Copyright (c) 2014 Ricardo Gladwell, Csaba Kozák
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import java.util.Arrays;
import java.util.List;

import me.gladwell.eclipse.m2e.android.AndroidMavenPlugin;
import me.gladwell.eclipse.m2e.android.NonRuntimeDependenciesContainerInitializer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.preferences.MavenConfigurationImpl;
import org.eclipse.m2e.tests.common.JobHelpers;
import org.eclipse.m2e.tests.common.JobHelpers.IJobMatcher;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@SuppressWarnings("restriction")
public class DownloadSourcesAndJavadocTest extends AndroidMavenPluginTestCase {

    private static final String DEPENDECY_JAR = "mockito-core-1.9.5.jar";
    private static final String PROJECT_NAME = "android-application";
    private static final int MAXIMUM_WAIT_SECONDS = 100;
    private IProject project;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        MavenConfigurationImpl mavenConfiguration = (MavenConfigurationImpl) MavenPlugin.getMavenConfiguration();
        mavenConfiguration.setDownloadSources(true);

        project = importAndroidProject(PROJECT_NAME);

        waitForDownloadSourcesJobToComplete();
    }

    @Test
    public void testSourcesAttached() throws JavaModelException {
        IJavaProject javaProject = JavaCore.create(project);

        IClasspathEntry entry = getClasspathEntry(javaProject, AndroidMavenPlugin.CONTAINER_NONRUNTIME_DEPENDENCIES,
                DEPENDECY_JAR);
        String entryPath = entry.getPath().toPortableString();
        String sourcePath = entryPath.replace(DEPENDECY_JAR, "mockito-core-1.9.5-sources.jar");
        assertEquals(sourcePath, entry.getSourceAttachmentPath().toPortableString());
    }

    @Test
    public void testCustomSourcesAttached() throws CoreException, InterruptedException {
        IJavaProject javaProject = JavaCore.create(project);

        setCustomSourceAttachment(javaProject);

        IClasspathEntry entry = getClasspathEntry(javaProject, AndroidMavenPlugin.CONTAINER_NONRUNTIME_DEPENDENCIES,
                DEPENDECY_JAR);

        assertEquals(entry.getPath().toPortableString(), entry.getSourceAttachmentPath().toPortableString());
    }

    public void testUpdatingMavenProjectWithEntryWithCustomSourceAttachmentNotOverrideAttachment()
            throws JavaModelException, CoreException, InterruptedException {
        IJavaProject javaProject = JavaCore.create(project);

        setCustomSourceAttachment(javaProject);
        updateMavenProject(project);

        waitForDownloadSourcesJobToComplete();

        IClasspathEntry entry = getClasspathEntry(javaProject, AndroidMavenPlugin.CONTAINER_NONRUNTIME_DEPENDENCIES,
                DEPENDECY_JAR);

        assertEquals(entry.getPath().toPortableString(), entry.getSourceAttachmentPath().toPortableString());
    }

    private void setCustomSourceAttachment(IJavaProject javaProject) throws JavaModelException, CoreException,
            InterruptedException {
        IClasspathContainer classpathContainer = JavaCore.getClasspathContainer(new Path(
                AndroidMavenPlugin.CONTAINER_NONRUNTIME_DEPENDENCIES), javaProject);

        ClasspathContainerInitializer initializer = JavaCore
                .getClasspathContainerInitializer(AndroidMavenPlugin.CONTAINER_NONRUNTIME_DEPENDENCIES);
        FakeContainer fakeContainer = new FakeContainer(classpathContainer);

        initializer.requestClasspathContainerUpdate(classpathContainer.getPath(), javaProject, fakeContainer);

        waitForClasspathPersisterJobToComplete();

        fakeContainer.setShouldFakeEntries(false);
    }

    private void waitForDownloadSourcesJobToComplete() {
        JobHelpers.waitForJobs(new IJobMatcher() {

            @Override
            public boolean matches(Job job) {
                return job.getName().contains("Downloading sources and JavaDoc");
            }
        }, MAXIMUM_WAIT_SECONDS * 1000);
    }

    private void waitForClasspathPersisterJobToComplete() {
        JobHelpers.waitForJobs(new IJobMatcher() {

            @Override
            public boolean matches(Job job) {
                return job.getName().contains(NonRuntimeDependenciesContainerInitializer.PERSIST_JOB_NAME);
            }
        }, MAXIMUM_WAIT_SECONDS * 1000);
    }
    
    private class FakeContainer implements IClasspathContainer {

        private IClasspathContainer realContainer;

        private boolean shouldFakeEntries = true;

        public FakeContainer(IClasspathContainer realContainer) {
            this.realContainer = realContainer;
        }

        public void setShouldFakeEntries(boolean shouldFakeEntries) {
            this.shouldFakeEntries = shouldFakeEntries;
        }

        @Override
        public IClasspathEntry[] getClasspathEntries() {
            if (!shouldFakeEntries) {
                return realContainer.getClasspathEntries();
            }

            List<IClasspathEntry> entries = Arrays.asList(realContainer.getClasspathEntries());

            List<IClasspathEntry> modifiedEntries = Lists.transform(entries,
                    new Function<IClasspathEntry, IClasspathEntry>() {

                        @Override
                        public IClasspathEntry apply(IClasspathEntry entry) {
                            if (entry.getPath().toPortableString().contains(DEPENDECY_JAR)) {
                                return JavaCore.newLibraryEntry(entry.getPath(), entry.getPath(), null,
                                        entry.getAccessRules(), entry.getExtraAttributes(), entry.isExported());
                            }
                            return entry;
                        }
                    });

            return Iterables.toArray(modifiedEntries, IClasspathEntry.class);
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
