/*******************************************************************************
 * Copyright (c) 2009-2015 Ricardo Gladwell and Hugo Josefson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import static java.io.File.separator;
import static me.gladwell.eclipse.m2e.android.test.ClasspathMatchers.containsEntry;
import static me.gladwell.eclipse.m2e.android.test.ClasspathMatchers.containsIncludePattern;
import static me.gladwell.eclipse.m2e.android.test.ClasspathMatchers.hasAttribute;
import static org.eclipse.jdt.core.IClasspathAttribute.IGNORE_OPTIONAL_PROBLEMS;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_SOURCE_PATH_PROVIDER;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.not;

import static me.gladwell.eclipse.m2e.android.test.Classpaths.findSourceEntry;
import static me.gladwell.eclipse.m2e.android.test.Matchers.hasAndroidNature;

import java.io.File;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
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
import org.eclipse.jdt.launching.IRuntimeClasspathProvider;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.jdt.IClasspathManager;

/**
 * Test suite for configuring and building Android applications.
 * 
 * @author Ricardo Gladwell <ricardo.gladwell@gmail.com>
 */
@SuppressWarnings("restriction")
public class ApplicationAndroidMavenPluginTest extends AndroidMavenPluginTestCase {

    private static final String APK_ADT_BUILDER = "com.android.ide.eclipse.adt.ApkBuilder";
    private static final String APK_ANDMORE_BUILDER = "org.eclipse.andmore.ApkBuilder";
    private static final String ANDROID_CLASSES_FOLDER = "bin" + separator + "classes";
    private static final String ANDROID_TEST_CLASSES_FOLDER = "target" + separator + "test-classes";
    private static final String PROJECT_NAME = "android-application";

    private IProject project;
    private IJavaProject javaProject;

    // TODO move to unit test suite in me.gladwell.eclipse.m2e.android
//    private @Inject JUnitClasspathProvider classpathProvider;
//    private @Inject JUnitSourcepathProvider sourcepathProvider;

    private ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();

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
        assertThat(project, hasAndroidNature());
    }

    public void testConfigureApkBuilderBeforeMavenBuilder() throws Exception {
        boolean foundApkBuilder = false;
        for (ICommand command : project.getDescription().getBuildSpec()) {
            if (APK_ADT_BUILDER.equals(command.getBuilderName())) {
                foundApkBuilder = true;
            } else if (APK_ANDMORE_BUILDER.equals(command.getBuilderName())) {
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
        TestAndroidProject project = new TestAndroidProject(javaProject);
        assertFalse("Android libraries contain should not be exported", project.getAndroidClasspathContainer().isExported());
    }

    public void testDoesNotLinkAssetFolder() throws Exception {
        assertFalse("default assets folder is linked", project.getFolder("assets").isLinked());
    }

    public void testConfigureAddsNonRuntimeContainer() throws Exception {
        assertTrue(hasClasspathContainer(javaProject, CONTAINER_NONRUNTIME_DEPENDENCIES));
    }

    public void testConfigureMarksNonRuntimeContainerNotExported() throws Exception {
        IClasspathEntry androidContainer = getClasspathContainer(javaProject, CONTAINER_NONRUNTIME_DEPENDENCIES);
        assertFalse(androidContainer.isExported());
    }

    public void testConfigureRemovesNonRuntimeDependenciesFromMavenClasspathContainer() throws Exception {
        assertFalse(classpathContainerContains(javaProject, IClasspathManager.CONTAINER_ID, "mockito-core-1.9.5.jar"));
    }

    public void testConfigureAddsNonRuntimeDependenciesToNonRuntimeContainer() throws Exception {
        assertTrue(classpathContainerContains(javaProject, CONTAINER_NONRUNTIME_DEPENDENCIES,
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

    // TODO move to unit test suite in me.gladwell.eclipse.m2e.android
    private IRuntimeClasspathEntry[] provideClasspath(IRuntimeClasspathProvider provider, ILaunchConfiguration configuration) throws CoreException {
//    project.getFile("bin/classes").getLocation().toFile().mkdirs();
//    IRuntimeClasspathEntry[] unresolvedClasspath = provider.computeUnresolvedClasspath(configuration);
//    IRuntimeClasspathEntry[] resolvedClasspath = classpathProvider.resolveClasspath(unresolvedClasspath,
//            configuration);
//    return resolvedClasspath;
        return null;
    }

    // TODO move to unit test suite in me.gladwell.eclipse.m2e.android
    private IRuntimeClasspathEntry[] provideJUnitClasspath(ILaunchConfiguration configuration) throws CoreException {
//        return provideClasspath(classpathProvider, configuration);
        return null;
    }

    // TODO Quarantined: move to unit test suite in me.gladwell.eclipse.m2e.android
    public void ignoreConfigureAddsPlatformProvidedToTestRunnerClasspath() throws Exception {
        // given
        ILaunchConfiguration configuration = launchManager.getLaunchConfiguration(project.getFile("test.launch"));

        // when
        IRuntimeClasspathEntry[] resolvedClasspath = provideJUnitClasspath(configuration);

        // then
        assertThat(resolvedClasspath, containsEntry("android-4.3.1_r3.jar"));
    }

    // TODO Quarantined: move to unit test suite in me.gladwell.eclipse.m2e.android
    public void ignoreConfigureAddsPlatformProvidedDependenciesToTestRunnerClasspath() throws Exception {
        // given
        ILaunchConfiguration configuration = launchManager.getLaunchConfiguration(project.getFile("test.launch"));

        // when
        IRuntimeClasspathEntry[] resolvedClasspath = provideJUnitClasspath(configuration);

        // then
        assertThat(resolvedClasspath, containsEntry("commons-logging-1.1.1.jar"));
    }

    // TODO Quarantined: move to unit test suite in me.gladwell.eclipse.m2e.android
    public void ignoreConfigureAddsTransitivePlatformProvidedDependenciesToTestRunnerClasspath() throws Exception {
        // given
        ILaunchConfiguration configuration = launchManager.getLaunchConfiguration(project.getFile("test.launch"));

        // when
        IRuntimeClasspathEntry[] resolvedClasspath = provideJUnitClasspath(configuration);

        // then
        assertThat(resolvedClasspath, containsEntry("httpcore-4.0.1.jar"));
    }

    // TODO Quarantined: move to unit test suite in me.gladwell.eclipse.m2e.android
    public void ignoreConfigureAddsBinaryFolderToTestRunnerClasspath() throws Exception {
        // given
        ILaunchConfiguration configuration = launchManager.getLaunchConfiguration(project.getFile("test.launch"));

        // when
        IRuntimeClasspathEntry[] resolvedClasspath = provideJUnitClasspath(configuration);

        // then
        assertThat(resolvedClasspath, containsEntry("bin/classes"));
    }

    // TODO Quarantined: move to unit test suite in me.gladwell.eclipse.m2e.android
    public void ignoreConfigureRemovesTargetFolderFromTestRunnerClasspath() throws Exception {
        // given
        ILaunchConfiguration configuration = launchManager.getLaunchConfiguration(project.getFile("test.launch"));

        // when
        IRuntimeClasspathEntry[] resolvedClasspath = provideJUnitClasspath(configuration);

        // then
        assertThat(resolvedClasspath, not(containsEntry("target/classes")));
    }

    // TODO Quarantined: move to unit test suite in me.gladwell.eclipse.m2e.android
    public void ignorejConfigureAddsAndroidTestSourcepathProviderToTestRunner() throws Exception {
        // given
        buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);
        
        ILaunchConfiguration configuration = launchManager.getLaunchConfiguration(project.getFile("test.launch"));

        // when
        configuration.launch(ILaunchManager.RUN_MODE, new NullProgressMonitor());

        // then
        assertEquals("me.gladwell.m2e.android.sourcepathProvider", configuration.getAttribute(ATTR_SOURCE_PATH_PROVIDER, ""));
    }
    
    private IRuntimeClasspathEntry[] provideJUnitSourcepath(ILaunchConfiguration configuration) throws CoreException {
//        return provideClasspath(sourcepathProvider, configuration);
        return null;
    }

    // TODO Quarantined: move to unit test suite in me.gladwell.eclipse.m2e.android
    public void ignoreConfigureAddsNonRuntimeDependenciesToTestRunnerSourcepath() throws Exception {
        // given
        ILaunchConfiguration configuration = launchManager.getLaunchConfiguration(project.getFile("test.launch"));

        // when
        IRuntimeClasspathEntry[] resolvedClasspath = provideJUnitSourcepath(configuration);

        // then
        assertThat(resolvedClasspath, containsEntry("mockito-core-1.9.5.jar"));
    }

    // TODO Quarantined: move to unit test suite in me.gladwell.eclipse.m2e.android
    public void ignoreConfigureAddsRuntimeDependenciesToTestRunnerSourcepath() throws Exception {
        // given
        ILaunchConfiguration configuration = launchManager.getLaunchConfiguration(project.getFile("test.launch"));

        // when
        IRuntimeClasspathEntry[] resolvedClasspath = provideJUnitSourcepath(configuration);

        // then
        assertThat(resolvedClasspath, containsEntry("commons-lang-2.4.jar"));
    }

    // TODO Quarantined: move to unit test suite in me.gladwell.eclipse.m2e.android
    public void ignoreConfigureAddsTransitiveNonRuntimeDependenciesToTestRunnerSourcepath() throws Exception {
        // given
        ILaunchConfiguration configuration = launchManager.getLaunchConfiguration(project.getFile("test.launch"));

        // when
        IRuntimeClasspathEntry[] resolvedClasspath = provideJUnitSourcepath(configuration);

        // then
        assertThat(resolvedClasspath, containsEntry("hamcrest-core-1.3.jar"));
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
