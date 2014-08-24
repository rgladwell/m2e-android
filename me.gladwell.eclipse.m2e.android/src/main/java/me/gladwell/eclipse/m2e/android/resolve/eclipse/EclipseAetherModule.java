package me.gladwell.eclipse.m2e.android.resolve.eclipse;

import me.gladwell.eclipse.m2e.android.resolve.LibraryResolver;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.m2e.core.MavenPlugin;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class EclipseAetherModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ArtifactResolver.class).to(EclipseAetherArtifactResolver.class);
        bind(LibraryResolver.class).to(EclipseAetherLibraryResolver.class);
    }

    @Provides
    RepositorySystem provideRepositorySystem(PlexusContainer mavenContainer) throws ComponentLookupException {
        return mavenContainer.lookup(RepositorySystem.class);
    }

    @Provides
    LocalRepository providesLocalRepository() throws CoreException {
        return new LocalRepository(MavenPlugin.getMaven().getLocalRepository().getBasedir());
    }

    @Provides
    RepositorySystemSession provideRepositorySystemSession(RepositorySystem system, LocalRepository localRepo) {
        final DefaultRepositorySystemSession session = new DefaultRepositorySystemSession();
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
        return session;
    }

}
