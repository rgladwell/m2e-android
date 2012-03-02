package me.gladwell.eclipse.m2e.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.gladwell.eclipse.m2e.android.configuration.AddAndroidNatureProjectConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.AndroidClasspathConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.ConvertLibraryProjectConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.FixerProjectConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.LibraryDependenciesProjectConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.MavenAndroidClasspathConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.OrderBuildersProjectConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurer;
import me.gladwell.eclipse.m2e.android.model.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.model.MavenAndroidProject;

import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.jdt.internal.JavaProjectConfigurator;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

public class PluginModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(AbstractProjectConfigurator.class).to(JavaProjectConfigurator.class);
		bind(AndroidClasspathConfigurer.class).to(MavenAndroidClasspathConfigurer.class);
		bind(new TypeLiteral<AndroidProjectFactory<MavenAndroidProject, MavenProject>>(){}).to(MavenAndroidProjectFactory.class);
		bind(new TypeLiteral<AndroidProjectFactory<EclipseAndroidProject, IProject>>(){}).to(EclipseAndroidProjectFactory.class);
	}

	@Provides
	List<ProjectConfigurer> provideProjectConfigurers() {
		final List<ProjectConfigurer> projectConfigurers = new ArrayList<ProjectConfigurer>();

		projectConfigurers.add(new FixerProjectConfigurer());
		projectConfigurers.add(new AddAndroidNatureProjectConfigurer());
		projectConfigurers.add(new OrderBuildersProjectConfigurer());
		projectConfigurers.add(new ConvertLibraryProjectConfigurer());
		projectConfigurers.add(new LibraryDependenciesProjectConfigurer());

		return Collections.unmodifiableList(projectConfigurers);
	}

}