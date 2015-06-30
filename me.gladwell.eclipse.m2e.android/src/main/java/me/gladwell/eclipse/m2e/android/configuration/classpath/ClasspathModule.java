package me.gladwell.eclipse.m2e.android.configuration.classpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.embedder.IMavenConfiguration;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import me.gladwell.eclipse.m2e.android.AndroidMavenProjectConfigurator;

public class ClasspathModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IMavenConfiguration.class).toInstance(MavenPlugin.getMavenConfiguration());
        bind(PersistNonRuntimeClasspathConfigurer.class);
        bind(AddNonRuntimeClasspathContainerConfigurer.class);
        bind(RemoveNonRuntimeProjectsConfigurer.class);
        // TODO move to root module
        bind(IMaven.class).toInstance(MavenPlugin.getMaven());
    }

    @Provides
    List<RawClasspathConfigurer> provideRawClasspathConfigurers(PersistNonRuntimeClasspathConfigurer persistConfigurer,
            RemoveNonRuntimeProjectsConfigurer removeProjectsConfigurer) {
        final List<RawClasspathConfigurer> rawClasspathConfigurers = new ArrayList<RawClasspathConfigurer>();

        rawClasspathConfigurers.add(persistConfigurer);
        rawClasspathConfigurers.add(new RemoveNonRuntimeDependenciesConfigurer());
        rawClasspathConfigurers.add(removeProjectsConfigurer);

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
        if (!AndroidMavenProjectConfigurator.usesM2E1_6OrNewer()) {
            classpathConfigurers.add(new MarkAndroidClasspathContainerNotExportedConfigurer());
        }
        classpathConfigurers.add(new IgnoreOptionalWarningsConfigurer());

        return Collections.unmodifiableList(classpathConfigurers);
    }

}
