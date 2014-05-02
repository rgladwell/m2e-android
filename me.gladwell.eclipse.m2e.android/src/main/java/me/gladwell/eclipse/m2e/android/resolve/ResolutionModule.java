/*******************************************************************************
 * Copyright (c) 2013, 2014 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.resolve;

import org.apache.maven.cli.transfer.ConsoleMavenTransferListener;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.embedder.MavenImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ResolutionModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ArtifactResolver.class).to(AetherArtifactResolver.class);
        bind(DependencyResolver.class).to(AetherDependencyResolver.class);
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
        final DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
        session.setTransferListener(new ConsoleMavenTransferListener(System.out));
        return session;
    }

}
