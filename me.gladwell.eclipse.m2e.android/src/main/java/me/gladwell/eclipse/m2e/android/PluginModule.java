package me.gladwell.eclipse.m2e.android;

import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.jdt.internal.JavaProjectConfigurator;

import com.google.inject.AbstractModule;

public class PluginModule extends AbstractModule {

	@Override
	protected void configure() {
		this.bind(AbstractProjectConfigurator.class).to(JavaProjectConfigurator.class);
		this.bind(AbstractBuildParticipant.class).to(IncrementalAndroidMavenBuildParticipant.class);
	}

}
