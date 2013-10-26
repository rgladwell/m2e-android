/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.gladwell.eclipse.m2e.android.configuration.ClasspathLoader;
import me.gladwell.eclipse.m2e.android.configuration.ClasspathPersister;
import me.gladwell.eclipse.m2e.android.configuration.ObjectSerializationClasspathPersister;
import me.gladwell.eclipse.m2e.android.configuration.classpath.AddGenFolderClasspathConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.classpath.AddNonRuntimeClasspathContainerConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.classpath.ClasspathConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.classpath.MarkAndroidClasspathContainerNotExportedConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.classpath.MarkMavenClasspathContianerExportedConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.classpath.ModifySourceFolderOutputClasspathConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.classpath.PersistNonRuntimeClasspathConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.classpath.RawClasspathConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.classpath.RemoveJREClasspathContainerConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.classpath.RemoveNonRuntimeDependenciesConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.workspace.AddAndroidNatureWorkspaceConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.workspace.ConvertLibraryWorkspaceConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.workspace.FixerWorkspaceConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.workspace.LibraryDependenciesWorkspaceConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.workspace.LinkAssetsFolderConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.workspace.OrderBuildersWorkspaceConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.workspace.WorkspaceConfigurer;
import me.gladwell.eclipse.m2e.android.project.AdtEclipseAndroidWorkspace;
import me.gladwell.eclipse.m2e.android.project.AndroidProjectFactory;
import me.gladwell.eclipse.m2e.android.project.AndroidWorkspace;
import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProjectFactory;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProjectFactory;
import me.gladwell.eclipse.m2e.android.project.MavenToEclipseAndroidProjectConverter;
import me.gladwell.eclipse.m2e.android.resolve.AetherArtifactResolver;
import me.gladwell.eclipse.m2e.android.resolve.AetherDependencyResolver;
import me.gladwell.eclipse.m2e.android.resolve.ArtifactResolver;
import me.gladwell.eclipse.m2e.android.resolve.DependencyResolver;

import org.apache.maven.cli.ConsoleMavenTransferListener;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.MavenModelManager;
import org.eclipse.m2e.core.internal.embedder.MavenImpl;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.jdt.internal.JavaProjectConfigurator;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.repository.LocalRepository;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

public class PluginModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(AbstractProjectConfigurator.class).to(JavaProjectConfigurator.class);
		bind(new TypeLiteral<AndroidProjectFactory<MavenAndroidProject, MavenProject>>(){}).to(MavenAndroidProjectFactory.class);
		bind(new TypeLiteral<AndroidProjectFactory<EclipseAndroidProject, IProject>>(){}).to(EclipseAndroidProjectFactory.class);
		bind(new TypeLiteral<AndroidProjectFactory<MavenAndroidProject, EclipseAndroidProject>>(){}).to(MavenToEclipseAndroidProjectConverter.class);
		bind(IWorkspace.class).toInstance(ResourcesPlugin.getWorkspace());
		bind(AndroidWorkspace.class).to(AdtEclipseAndroidWorkspace.class);
		bind(MavenModelManager.class).toInstance(MavenPlugin.getMavenModelManager());
		bind(LibraryDependenciesWorkspaceConfigurer.class);
        bind(ClasspathPersister.class).to(ObjectSerializationClasspathPersister.class);
        bind(ClasspathLoader.class).to(ObjectSerializationClasspathPersister.class);
        bind(File.class).toInstance(AndroidMavenPlugin.getDefault().getStateLocation().toFile());
        bind(PersistNonRuntimeClasspathConfigurer.class);
        bind(AddNonRuntimeClasspathContainerConfigurer.class);
        bind(IMavenProjectRegistry.class).toInstance(MavenPlugin.getMavenProjectRegistry());
        bind(ArtifactResolver.class).to(AetherArtifactResolver.class);
        bind(DependencyResolver.class).to(AetherDependencyResolver.class);
	}

	@Provides
	List<WorkspaceConfigurer> provideWorkspaceConfigurers(LibraryDependenciesWorkspaceConfigurer configurer) {
		final List<WorkspaceConfigurer> workspaceConfigurers = new ArrayList<WorkspaceConfigurer>();

		workspaceConfigurers.add(new FixerWorkspaceConfigurer());
		workspaceConfigurers.add(new AddAndroidNatureWorkspaceConfigurer());
		workspaceConfigurers.add(new OrderBuildersWorkspaceConfigurer());
		workspaceConfigurers.add(new ConvertLibraryWorkspaceConfigurer());
		workspaceConfigurers.add(new ConvertLibraryWorkspaceConfigurer());
		workspaceConfigurers.add(configurer);
		workspaceConfigurers.add(new LinkAssetsFolderConfigurer());

		return Collections.unmodifiableList(workspaceConfigurers);
	}

    @Provides
    List<RawClasspathConfigurer> provideRawClasspathConfigurers(PersistNonRuntimeClasspathConfigurer configurer) {
        final List<RawClasspathConfigurer> rawClasspathConfigurers = new ArrayList<RawClasspathConfigurer>();

        rawClasspathConfigurers.add(configurer);
        rawClasspathConfigurers.add(new RemoveNonRuntimeDependenciesConfigurer());

        return Collections.unmodifiableList(rawClasspathConfigurers);
    }

    @Provides
    List<ClasspathConfigurer> provideClasspathConfigurers(final AddNonRuntimeClasspathContainerConfigurer configurer) {
        final List<ClasspathConfigurer> classpathConfigurers = new ArrayList<ClasspathConfigurer>();

        classpathConfigurers.add(configurer);
        classpathConfigurers.add(new AddGenFolderClasspathConfigurer());
        classpathConfigurers.add(new ModifySourceFolderOutputClasspathConfigurer());
        classpathConfigurers.add(new RemoveJREClasspathContainerConfigurer());
        classpathConfigurers.add(new MarkMavenClasspathContianerExportedConfigurer());
        classpathConfigurers.add(new MarkAndroidClasspathContainerNotExportedConfigurer());

        return Collections.unmodifiableList(classpathConfigurers);
    }

	@Provides
	RepositorySystem provideRepositorySystem() {
        try {
            return ((MavenImpl) MavenPlugin.getMaven()).getPlexusContainer().lookup(RepositorySystem.class);
        } catch (ComponentLookupException e) {
            throw new RuntimeException(e);
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
	}

    @Provides
    LocalRepository providesLocalRepository() throws CoreException {
        return new LocalRepository(MavenPlugin.getMaven().getLocalRepository().getBasedir());
    }

    @Provides
    RepositorySystemSession provideRepositorySystemSession(RepositorySystem system, LocalRepository localRepo) {
        final MavenRepositorySystemSession session = new MavenRepositorySystemSession();
        session.setLocalRepositoryManager(
            system.newLocalRepositoryManager(localRepo)
        );
        session.setTransferListener(new ConsoleMavenTransferListener(System.out));
        return session;
    }

}
