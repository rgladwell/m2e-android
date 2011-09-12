package me.gladwell.eclipse.m2e.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.gladwell.android.tools.AndroidBuildService;
import me.gladwell.android.tools.CommandLineAndroidTools;
import me.gladwell.android.tools.DexService;
import me.gladwell.android.tools.MavenAndroidPluginBuildService;
import me.gladwell.eclipse.m2e.android.configuration.AddAndroidNatureProjectConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.ClasspathConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.ConvertLibraryProjectConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.FixerProjectConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.GenFolderClasspathConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.OrderBuildersProjectConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurer;

import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
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
		this.bind(AbstractBuildParticipant.class).to(AndroidMavenBuildParticipant.class);
		this.bind(BuildListenerRegistry.class).to(AndroidMavenBuildParticipant.class);
		this.bind(DexService.class).to(CommandLineAndroidTools.class);
		this.bind(AndroidBuildService.class).to(MavenAndroidPluginBuildService.class);
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
		return Collections.unmodifiableList(classpathConfigurer);
	}

}
