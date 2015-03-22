/*******************************************************************************
 * Copyright (c) 2009-2014 Ricardo Gladwell and Hugo Josefson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import static java.io.File.separator;
import static me.gladwell.eclipse.m2e.android.configuration.Classpaths.findSourceEntry;
import static me.gladwell.eclipse.m2e.android.test.ClasspathMatchers.containsEntry;
import static me.gladwell.eclipse.m2e.android.test.ClasspathMatchers.containsIncludePattern;
import static me.gladwell.eclipse.m2e.android.test.ClasspathMatchers.hasAttribute;
import static org.eclipse.jdt.core.IClasspathAttribute.IGNORE_OPTIONAL_PROBLEMS;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER;
import static org.junit.Assert.assertThat;

import java.io.File;

import me.gladwell.eclipse.m2e.android.AndroidMavenPlugin;
import me.gladwell.eclipse.m2e.android.JUnitClasspathProvider;

import org.codehaus.plexus.util.FileUtils;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.junit.JUnitCore;
import org.eclipse.jdt.junit.TestRunListener;
import org.eclipse.jdt.junit.model.ITestElement;
import org.eclipse.jdt.junit.model.ITestRunSession;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.jdt.IClasspathManager;

import com.android.ide.eclipse.adt.AdtConstants;
import com.google.inject.Inject;

/**
 * Test suite for configuring and building Android applications.
 * 
 * @author Ricardo Gladwell <ricardo.gladwell@gmail.com>
 */
@SuppressWarnings("restriction")
public class ApplicationAndroidMavenPluginTest extends AndroidMavenPluginTestCase {

    private static final String ANDROID_CLASSES_FOLDER = "bin" + separator + "classes";
    private static final String ANDROID_TEST_CLASSES_FOLDER = "target" + separator + "test-classes";
    private static final String PROJECT_NAME = "android-application";

    private IProject project;
    private IJavaProject javaProject;

    private @Inject ILaunchManager launchManager;
    private @Inject JUnitClasspathProvider classpathProvider;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        project = importAndroidProject(PROJECT_NAME);
        javaProject = JavaCore.create(project);
        
        project.getFile("test.launch_renamed").move((project.getFile("test.launch").getFullPath()), true, monitor);
    }

    public void testConfigure() throws Exception {
        assertNoErrors(project);
    }

    public void testConfigureAddsAndroidNature() throws Exception {
        assertTrue("configurer failed to add android nature", project.hasNature(AdtConstants.NATURE_DEFAULT));
    }

    public void testConfigureApkBuilderBeforeMavenBuilder() throws Exception {
        boolean foundApkBuilder = false;
        for (ICommand command : project.getDescription().getBuildSpec()) {
            if ("com.android.ide.eclipse.adt.ApkBuilder".equals(command.getBuilderName())) {
                foundApkBuilder = true;
            } else if (IMavenConstants.BUILDER_ID.equals(command.getBuilderName())) {
                assertTrue("project APKBuilder not configured before maven builder", foundApkBuilder);
                return;
            }
        }

        fail("project does not contain maven builder build command");
    }

    public void testConfigureDoesNotAddTargetDirectoryToClasspath() throws Exception {
        for (IClasspathEntry entry : javaProject.getRawClasspath()) {
            assertFalse(
                    "classpath contains reference to target directory: cause infinite build loops and build conflicts",
                    entry.getPath().toOSString().contains("target"));
        }
    }

    public void testConfigureGeneratedResourcesFolderInRawClasspath() throws Exception {
        assertClasspathContains(javaProject, "gen");
    }

    public void testConfigureAddsCompileDependenciesToClasspath() throws Exception {
        assertClasspathContains(javaProject, "commons-lang-2.4.jar");
    }

    public void testConfigureDoesNotAddPlatformDependencyToClasspath() throws Exception {
        assertClasspathDoesNotContain(javaProject, "android-2.3.3.jar");
    }

    public void testConfigureDoesNotAddPlatformProvidedDependenciesToClasspath() throws Exception {
        assertClasspathDoesNotContainExported(javaProject, "commons-logging-1.1.1.jar");
    }

    public void testConfigureDoesNotAddTransitivePlatformProvidedDependenciesToClasspath() throws Exception {
        assertClasspathDoesNotContainExported(javaProject, "httpcore-4.0.1.jar");
    }

    public void testConfigureDoesRemoveJreClasspathContainer() throws Exception {
        assertClasspathDoesNotContain(javaProject, JavaRuntime.JRE_CONTAINER);
    }

    public void testBuildDirectoryContainsCompiledClasses() throws Exception {
        File outputLocation = new File(ResourcesPlugin.getWorkspace().getRoot().getRawLocation().toOSString(),
                javaProject.getPath().toOSString());
        File compiledClass = new File(outputLocation, "bin/classes/your/company/HelloAndroidActivity.class");

        buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

        assertTrue(compiledClass.exists());
    }

    public void testConfigureMarksMavenContainerExported() throws Exception {
        IClasspathEntry mavenContainer = getClasspathContainer(javaProject, IClasspathManager.CONTAINER_ID);
        assertTrue(mavenContainer.isExported());
    }

    public void testConfigureSetsCorrectSourceOutputFolder() throws Exception {
        IClasspathEntry entry = findSourceEntry(javaProject.getRawClasspath(), "src" + separator + "main" + separator
                + "java");
        assertTrue(entry.getOutputLocation().toOSString().endsWith(ANDROID_CLASSES_FOLDER));
    }

    public void testConfigureSetsCorrectTestOutputFolder() throws Exception {
        IClasspathEntry entry = findSourceEntry(javaProject.getRawClasspath(), "src" + separator + "test" + separator
                + "java");
        assertTrue(entry.getOutputLocation().toOSString().endsWith(ANDROID_TEST_CLASSES_FOLDER));
    }

    public void testConfigureMarksAndroidLibrariesContainerNotExported() throws Exception {
        IClasspathEntry androidContainer = getClasspathContainer(javaProject, AdtConstants.CONTAINER_PRIVATE_LIBRARIES);
        assertFalse(androidContainer.isExported());
    }

    public void testDoesNotLinkAssetFolder() throws Exception {
        assertFalse("default assets folder is linked", project.getFolder("assets").isLinked());
    }

    public void testConfigureAddsNonRuntimeContainer() throws Exception {
        assertTrue(hasClasspathContainer(javaProject, AndroidMavenPlugin.CONTAINER_NONRUNTIME_DEPENDENCIES));
    }

    public void testConfigureMarksNonRuntimeContainerNotExported() throws Exception {
        IClasspathEntry androidContainer = getClasspathContainer(javaProject,
                AndroidMavenPlugin.CONTAINER_NONRUNTIME_DEPENDENCIES);
        assertFalse(androidContainer.isExported());
    }

    public void testConfigureRemovesNonRuntimeDependenciesFromMavenClasspathContainer() throws Exception {
        assertFalse(classpathContainerContains(javaProject, IClasspathManager.CONTAINER_ID, "mockito-core-1.9.5.jar"));
    }

    public void testConfigureAddsNonRuntimeDependenciesToNonRuntimeContainer() throws Exception {
        assertTrue(classpathContainerContains(javaProject, AndroidMavenPlugin.CONTAINER_NONRUNTIME_DEPENDENCIES,
                "mockito-core-1.9.5.jar"));
    }

    private class TestTestRunListener extends TestRunListener {

        private ITestElement.Result result;

        @Override
            public void sessionFinished(ITestRunSession session) {
            result = session.getTestResult(true);
        }

        public ITestElement.Result launchResult() {
            return result;
        }

    }

    public void testTestRunner() throws Exception {
        // given
        buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);
        
        TestTestRunListener testListener = new TestTestRunListener();
        JUnitCore.addTestRunListener(testListener);
        ILaunchConfiguration configuration = launchManager.getLaunchConfiguration(project.getFile("test.launch"));

        // when
        ILaunch launch = configuration.launch(ILaunchManager.RUN_MODE, new NullProgressMonitor());

        // then
        while (!launch.isTerminated()) {
            Thread.sleep(500);
        }
        assertEquals(ITestElement.Result.OK, testListener.launchResult());
    }

    public void testConfigureAddsAndroidTestClasspathProviderToTestRunner() throws Exception {
        // given
        buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);
        
        ILaunchConfiguration configuration = launchManager.getLaunchConfiguration(project.getFile("test.launch"));

        // when
        configuration.launch(ILaunchManager.RUN_MODE, new NullProgressMonitor());

        // then
        assertEquals("me.gladwell.m2e.android.classpathProvider",
                configuration.getAttribute(ATTR_CLASSPATH_PROVIDER, ""));
    }

    private IRuntimeClasspathEntry[] provideClasspath(ILaunchConfiguration configuration) throws CoreException {
        project.getFile("bin/classes").getLocation().toFile().mkdirs();
        IRuntimeClasspathEntry[] unresolvedClasspath = classpathProvider.computeUnresolvedClasspath(configuration);
        IRuntimeClasspathEntry[] resolvedClasspath = classpathProvider.resolveClasspath(unresolvedClasspath,
                configuration);
        return resolvedClasspath;
    }

    public void testConfigureAddsPlatformProvidedToTestRunnerClasspath() throws Exception {
        // given
        ILaunchConfiguration configuration = launchManager.getLaunchConfiguration(project.getFile("test.launch"));

        // when
        IRuntimeClasspathEntry[] resolvedClasspath = provideClasspath(configuration);

        // then
        assertThat(resolvedClasspath, containsEntry("android-4.3.1_r3.jar"));
    }

    public void testConfigureAddsPlatformProvidedDependenciesToTestRunnerClasspath() throws Exception {
        // given
        ILaunchConfiguration configuration = launchManager.getLaunchConfiguration(project.getFile("test.launch"));

        // when
        IRuntimeClasspathEntry[] resolvedClasspath = provideClasspath(configuration);

        // then
        assertThat(resolvedClasspath, containsEntry("commons-logging-1.1.1.jar"));
    }

    public void testConfigureAddsTransitivePlatformProvidedDependenciesToTestRunnerClasspath() throws Exception {
        // given
        ILaunchConfiguration configuration = launchManager.getLaunchConfiguration(project.getFile("test.launch"));

        // when
        IRuntimeClasspathEntry[] resolvedClasspath = provideClasspath(configuration);

        // then
        assertThat(resolvedClasspath, containsEntry("httpcore-4.0.1.jar"));
    }

    public void testConfigureAddsBinaryFolderToTestRunnerClasspath() throws Exception {
        // given
        ILaunchConfiguration configuration = launchManager.getLaunchConfiguration(project.getFile("test.launch"));

        // when
        IRuntimeClasspathEntry[] resolvedClasspath = provideClasspath(configuration);

        // then
        assertThat(resolvedClasspath, containsEntry("bin/classes"));
    }

    public void testConfigureDoesNotSetIgnoreWarnings() throws Exception {
        IClasspathEntry gen = findSourceEntry(javaProject.getRawClasspath(), "gen");
        assertFalse("external assets folder isn't linked", booleanAttribute(IGNORE_OPTIONAL_PROBLEMS, gen));
    }
    
    public void testConfigureDoesNotRemoveIncludesFromEntry() throws Exception {
        IClasspathEntry mainJava = findSourceEntry(javaProject.getRawClasspath(), "src" + separator + "main" + separator + "java");
        assertThat(mainJava, containsIncludePattern("**" + separator + "*.java"));
    }
    
    public void testConfigureDoesNotRemovePomDerivedAttributeFromEntry() throws JavaModelException {
        IClasspathEntry mainJava = findSourceEntry(javaProject.getRawClasspath(), "src" + separator + "main" + separator + "java");
        assertThat(mainJava, hasAttribute(IClasspathManager.POMDERIVED_ATTRIBUTE, "true"));
    }
    
    public void testConfigureDoesNotRemoveOptionalAttributeFromEntry() throws Exception {
        IClasspathEntry mainJava = findSourceEntry(javaProject.getRawClasspath(), "src" + separator + "main" + separator + "java");
        assertThat(mainJava, hasAttribute(IClasspathAttribute.OPTIONAL, "true"));
    }
    
    public void testConfigureDoesNotRemovePomDerivedAttributeFromMavenContainer() throws Exception {
        IClasspathEntry mavenContainer = getClasspathContainer(javaProject, IClasspathManager.CONTAINER_ID);
        assertThat(mavenContainer, hasAttribute(IClasspathManager.POMDERIVED_ATTRIBUTE, "true"));
    }
}
