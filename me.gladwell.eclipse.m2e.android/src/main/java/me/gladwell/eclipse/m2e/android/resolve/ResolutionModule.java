package me.gladwell.eclipse.m2e.android.resolve;

import org.apache.maven.cli.ConsoleMavenTransferListener;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.embedder.MavenImpl;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.repository.LocalRepository;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ResolutionModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IMavenProjectRegistry.class).toInstance(MavenPlugin.getMavenProjectRegistry());

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
        final MavenRepositorySystemSession session = new MavenRepositorySystemSession();
        session.setLocalRepositoryManager(
            system.newLocalRepositoryManager(localRepo)
        );
        session.setTransferListener(new ConsoleMavenTransferListener(System.out));
        return session;
    }

}
