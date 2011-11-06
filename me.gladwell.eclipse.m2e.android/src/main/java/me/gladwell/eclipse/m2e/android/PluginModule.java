package me.gladwell.eclipse.m2e.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.gladwell.eclipse.m2e.android.configuration.AddAndroidNatureProjectConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.AndroidClasspathConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.ConvertLibraryProjectConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.FixerProjectConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.MavenAndroidClasspathConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.OrderBuildersProjectConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurer;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.jdt.IJavaProjectConfigurator;
import org.eclipse.m2e.jdt.internal.JavaProjectConfigurator;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class PluginModule extends AbstractModule {

	@Override
	protected void configure() {
		this.bind(AbstractProjectConfigurator.class).to(JavaProjectConfigurator.class);
		this.bind(AndroidProjectFactory.class).to(MavenAndroidProjectFactory.class);
		this.bind(AndroidClasspathConfigurer.class).to(MavenAndroidClasspathConfigurer.class);
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
	IWorkspaceRoot provideWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

}
