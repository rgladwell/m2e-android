/*******************************************************************************
 * Copyright (c) 2009, 2010, 2011 Ricardo Gladwell and Hugo Josefson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import me.gladwell.android.tools.AndroidBuildService;
import me.gladwell.android.tools.AndroidToolsException;
import me.gladwell.android.tools.CommandLineAndroidTools;
import me.gladwell.android.tools.MavenAndroidPluginBuildService;
import me.gladwell.android.tools.model.ClassDescriptor;
import me.gladwell.android.tools.model.DexInfo;
import me.gladwell.android.tools.model.Jdk;
import me.gladwell.android.tools.model.PackageInfo;
import me.gladwell.eclipse.m2e.android.AndroidMavenPlugin;
import me.gladwell.eclipse.m2e.android.AndroidMavenPluginUtil;
import me.gladwell.eclipse.m2e.android.BuildListenerRegistry;

import org.apache.maven.archetype.catalog.Archetype;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Developer;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.ArtifactKey;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.internal.MavenPluginActivator;
import org.eclipse.m2e.core.internal.archetype.ArchetypeManager;
import org.eclipse.m2e.core.internal.index.IMutableIndex;
import org.eclipse.m2e.core.internal.index.IndexManager;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;
import org.eclipse.m2e.tests.common.JobHelpers;
import org.eclipse.m2e.tests.common.JobHelpers.IJobMatcher;

import com.android.ide.eclipse.adt.AdtPlugin;
import com.android.ide.eclipse.adt.internal.preferences.AdtPrefs;
import com.android.ide.eclipse.adt.internal.sdk.Sdk;
import com.google.inject.Inject;

public abstract class AndroidMavenPluginTestCase extends AbstractMavenProjectTestCase {

	static final int MAXIMUM_SECONDS_TO_LOAD_ADT = 60;

	protected AndroidMavenPlugin plugin;
	protected AdtPlugin adtPlugin;
	private CommandLineAndroidTools dexInfoService;
	protected DummyAndroidBuildListener listener;
	
	@Inject
	private BuildListenerRegistry registry;

	protected AndroidBuildService buildService;

	private IProjectConfigurationManager projectConfigurationManager;

	@Override
	@SuppressWarnings("restriction")
    protected void setUp() throws Exception {
	    super.setUp();

	    plugin = AndroidMavenPlugin.getDefault();
		plugin.getInjector().injectMembers(this);

		adtPlugin = AdtPlugin.getDefault();
	    String androidHome = System.getenv("ANDROID_HOME");
	    
	    if(androidHome != null && !androidHome.equals(adtPlugin.getOsSdkFolder())) {
		    adtPlugin.getPreferenceStore().setValue(AdtPrefs.PREFS_SDK_DIR, androidHome);
		    adtPlugin.savePluginPreferences();
	    }

	    waitForAdtToLoad();

	    dexInfoService = new CommandLineAndroidTools();
	    buildService= new MavenAndroidPluginBuildService();
		Jdk jdk = new Jdk();
		jdk.setPath(JavaRuntime.getDefaultVMInstall().getInstallLocation().getAbsoluteFile());
		buildService.setJdk(jdk);
		
		listener = new DummyAndroidBuildListener();

		registry.registerBuildListener(listener);
		projectConfigurationManager = MavenPlugin.getProjectConfigurationManager();
    }

	@Override
	protected void tearDown() throws Exception {
		listener.clear();
		super.tearDown();
	}

	protected void waitForAdtToLoad() throws InterruptedException, Exception {
		JobHelpers.waitForJobs(new IJobMatcher() {
			public boolean matches(Job job) {
				return job.getClass().getName().contains(Sdk.class.getName());
			}
			
		}, MAXIMUM_SECONDS_TO_LOAD_ADT * 1000);
	}

    protected void buildAndroidProject(IProject project, int kind) throws CoreException, InterruptedException {
		project.build(kind, monitor);
		waitForJobsToComplete();
    }

	void assertApkContains(ClassDescriptor stringUtils, IProject project) throws AndroidToolsException, JavaModelException {
		DexInfo dexInfo = dexInfoService.getDexInfo(AndroidMavenPluginUtil.getApkFile(project));
		assertTrue("external dep class=["+stringUtils+"] not found in file=["+AndroidMavenPluginUtil.getApkFile(project)+"]", dexInfo.getClassDescriptors().contains(stringUtils));
	}

	protected IProject createArchetypeProject(final String projectName) throws IOException, CoreException {
		final Archetype archetype = new Archetype();
		archetype.setArtifactId("android-archetypes");
		archetype.setGroupId("de.akquinet.android.archetypes");
		archetype.setVersion("1.0.6");
		Properties properties = new Properties();
		properties.setProperty("android-plugin-version", "3.0.0-alpha-2");
		archetype.setProperties(properties);
		final ArchetypeManager archetypeManager = MavenPluginActivator.getDefault().getArchetypeManager();
		archetypeManager.getArchetypeCatalogFactory("internal").getArchetypeCatalog().addArchetype(archetype);
		if(!archetypeManager.getArchetypeCatalogFactory("internal").getArchetypeCatalog().getArchetypes().contains(archetype)) {
			downloadArchetype();
		}
		IProject archetypeProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        ResolverConfiguration resolverConfiguration = new ResolverConfiguration();
        ProjectImportConfiguration configuration = new ProjectImportConfiguration(resolverConfiguration);
        File tmp = File.createTempFile("m2e-android", "test");
		IPath location = new Path(tmp.getAbsolutePath()).append(projectName);
		projectConfigurationManager.createArchetypeProject(archetypeProject, location , archetype,
				"me.gladwell", projectName, "1.0-SNAPSHOT", "me.gladwell.android.test",
				properties, configuration, monitor);
		return archetypeProject;
	}

	private void downloadArchetype() throws CoreException {
        final IMaven maven = MavenPlugin.getMaven();
        final List<ArtifactRepository> remoteRepositories = maven.getArtifactRepositories();

        Artifact pomArtifact = maven.resolve("de.akquinet.android.archetypes", "android-archetypes", "1.0.6",
            "pom", null, remoteRepositories, monitor);

        File pomFile = pomArtifact.getFile();
        if(pomFile.exists()) {
          Artifact jarArtifact = maven.resolve("de.akquinet.android.archetypes", "android-archetypes", "1.0.6",
              "jar", null, remoteRepositories, monitor);

          File jarFile = jarArtifact.getFile();

          IndexManager indexManager = MavenPlugin.getIndexManager();
          IMutableIndex localIndex = indexManager.getLocalIndex();
          localIndex.addArtifact(jarFile, new ArtifactKey(pomArtifact));

          Archetype archetype = new Archetype();
          archetype.setGroupId("de.akquinet.android.archetypes");
          archetype.setArtifactId("android-archetypes");
          archetype.setVersion("1.0.6");
          org.apache.maven.archetype.Archetype archetyper = MavenPluginActivator.getDefault().getArchetype();
          archetyper.updateLocalCatalog(archetype);
        }
	}

	protected void addDependency(final IProject project, final Dependency dependency) throws FileNotFoundException, IOException, XmlPullParserException {
		File pom = new File(project.getLocation().toFile(), "pom.xml");
		MavenXpp3Reader reader = new MavenXpp3Reader();
		Model model = reader.read(new FileReader(pom));
		model.getDependencies().add(dependency);
		MavenXpp3Writer writer = new MavenXpp3Writer();
		writer.write(new FileWriter(pom), model);
	}

	protected void addDependency(final IProject project, final String artifactId, final String groupId, final String version) throws FileNotFoundException, IOException, XmlPullParserException {
		Dependency dependency = new Dependency();
		dependency.setArtifactId(artifactId);
		dependency.setGroupId(groupId);
		dependency.setVersion(version);
		addDependency(project, dependency);
	}

	protected void assertApkContainsDependency(IProject project, String name, String packageName) throws AndroidToolsException, JavaModelException {
		PackageInfo packageInfo = new PackageInfo();
		packageInfo.setName(packageName);
		ClassDescriptor type = new ClassDescriptor();
		type.setName(name);
		type.setPackageInfo(packageInfo);
		assertApkContains(type, project);
	}
}
