package me.gladwell.eclipse.m2e.android.configuration.classpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ClasspathModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(PersistNonRuntimeClasspathConfigurer.class);
        bind(AddNonRuntimeClasspathContainerConfigurer.class);
        bind(RemoveNonRuntimeProjectsConfigurer.class);
    }

    @Provides
    List<RawClasspathConfigurer> provideRawClasspathConfigurers(PersistNonRuntimeClasspathConfigurer persistConfigurer,
            RemoveNonRuntimeProjectsConfigurer removeProjectsConfigurer) {
        final List<RawClasspathConfigurer> rawClasspathConfigurers = new ArrayList<RawClasspathConfigurer>();

        rawClasspathConfigurers.add(persistConfigurer);
        rawClasspathConfigurers.add(new RemoveNonRuntimeDependenciesConfigurer());
        rawClasspathConfigurers.add(removeProjectsConfigurer);
        // The ReorderClasspathConfigurer needs to come last in the sequence
        rawClasspathConfigurers.add(new ReorderClasspathConfigurer());

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
        classpathConfigurers.add(new IgnoreOptionalWarningsConfigurer());

        return Collections.unmodifiableList(classpathConfigurers);
    }

}
