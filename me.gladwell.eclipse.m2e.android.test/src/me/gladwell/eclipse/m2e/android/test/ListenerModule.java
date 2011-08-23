package me.gladwell.eclipse.m2e.android.test;

import me.gladwell.eclipse.m2e.android.AndroidBuildListener;

import com.google.inject.AbstractModule;

public class ListenerModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(AndroidBuildListener.class).to(DummyAndroidBuildListener.class);
	}

}
