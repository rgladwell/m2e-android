package me.gladwell.eclipse.m2e.android.resolve.sonatype;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.sonatype.aether.repository.LocalRepository;
import org.eclipse.core.runtime.CoreException;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.util.DefaultRepositorySystemSession;
import org.eclipse.m2e.core.MavenPlugin;

import me.gladwell.eclipse.m2e.android.resolve.LibraryResolver;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class SonatypeAetherModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ArtifactResolver.class).to(SonatypeAetherArtifactResolver.class);
        bind(LibraryResolver.class).to(SonatypeAetherLibraryResolver.class);
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
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(localRepo));
        return session;
    }

}
