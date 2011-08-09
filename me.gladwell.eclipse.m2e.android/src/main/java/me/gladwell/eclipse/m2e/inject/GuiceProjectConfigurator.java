package me.gladwell.eclipse.m2e.inject;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public abstract class GuiceProjectConfigurator extends AbstractProjectConfigurator implements InjectionProjectConfigurator {

	private List<Module> modules = new ArrayList<Module>();
	private Injector injector;

	public GuiceProjectConfigurator() {
		super();
		addModules(modules);
		injector = Guice.createInjector(modules);
		injector.injectMembers(this);
	}

	protected void addModules(List<Module> modules) {
	}

}
