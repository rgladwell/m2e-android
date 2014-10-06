package me.gladwell.eclipse.m2e.android.project;

import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

public class ProjectModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AndroidWorkspace.class).to(AdtEclipseAndroidWorkspace.class);

        bind(new TypeLiteral<AndroidProjectFactory<MavenAndroidProject, MavenProject>>(){}).to(MavenAndroidProjectFactory.class);
        bind(new TypeLiteral<AndroidProjectFactory<EclipseAndroidProject, IProject>>(){}).to(EclipseAndroidProjectFactory.class);
        bind(new TypeLiteral<AndroidProjectFactory<MavenAndroidProject, EclipseAndroidProject>>(){}).to(MavenToEclipseAndroidProjectConverter.class);
    
        bind(IMaven.class).toInstance(MavenPlugin.getMaven());
    }

}
