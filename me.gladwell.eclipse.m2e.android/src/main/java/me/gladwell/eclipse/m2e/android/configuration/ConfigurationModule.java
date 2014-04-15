package me.gladwell.eclipse.m2e.android.configuration;

import java.io.File;

import me.gladwell.eclipse.m2e.android.AndroidMavenPlugin;

import com.google.inject.AbstractModule;

public class ConfigurationModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(File.class).toInstance(AndroidMavenPlugin.getDefault().getStateLocation().toFile());

        bind(ClasspathPersister.class).to(ObjectSerializationClasspathPersister.class);

        bind(ClasspathLoader.class).to(ObjectSerializationClasspathPersister.class);
        bind(ClasspathLoader.class).annotatedWith(PrunePlatformProvidedDependencies.class).to(
                PrunePlatformProvidedDependenciesClasspathLoader.class);
    }

}
