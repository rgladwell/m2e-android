package me.gladwell.eclipse.m2e.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.gladwell.eclipse.m2e.android.configuration.AddAndroidNatureProjectConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.ClasspathConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.ConvertLibraryProjectConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.FixerProjectConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.GenFolderClasspathConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.OrderBuildersProjectConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.RemoveNonCompileDependenciesConfigurer;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.jdt.IJavaProjectConfigurator;
import org.eclipse.m2e.jdt.internal.JavaProjectConfigurator;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class PluginModule extends AbstractModule {

	@Override
	protected void configure() {
		this.bind(IJavaProjectConfigurator.class).to(AndroidMavenProjectConfigurator.class);
		this.bind(AbstractProjectConfigurator.class).to(JavaProjectConfigurator.class);
		this.bind(AndroidProjectFactory.class).to(MavenAndroidProjectFactory.class);
		this.bind(IWorkspace.class).toInstance(ResourcesPlugin.getWorkspace());
	}

	@Provides
	List<ProjectConfigurer> provideProjectConfigurers() {
		final List<ProjectConfigurer> projectConfigurers = new ArrayList<ProjectConfigurer>();

		projectConfigurers.add(new FixerProjectConfigurer());
		projectConfigurers.add(new AddAndroidNatureProjectConfigurer());
		projectConfigurers.add(new OrderBuildersProjectConfigurer());
		projectConfigurers.add(new ConvertLibraryProjectConfigurer());

		return Collections.unmodifiableList(projectConfigurers);
	}

	@Provides
	List<ClasspathConfigurer> provideClasspathConfigurers() {
		final List<ClasspathConfigurer> classpathConfigurer = new ArrayList<ClasspathConfigurer>();
		classpathConfigurer.add(new GenFolderClasspathConfigurer());
		classpathConfigurer.add(new RemoveNonCompileDependenciesConfigurer());
		return Collections.unmodifiableList(classpathConfigurer);
	}

}
