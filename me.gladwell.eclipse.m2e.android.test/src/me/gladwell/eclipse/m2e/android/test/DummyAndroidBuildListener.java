package me.gladwell.eclipse.m2e.android.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;

import com.google.inject.Singleton;

import me.gladwell.eclipse.m2e.android.AndroidBuildListener;

@Singleton
public class DummyAndroidBuildListener implements AndroidBuildListener {

	List<EventObject> androidMavenBuildEvents = new ArrayList<EventObject>();

	public void onBuild(EventObject event) {
		androidMavenBuildEvents.add(event);
	}

	public List<EventObject> getAndroidMavenBuildEvents() {
		return Collections.unmodifiableList(androidMavenBuildEvents);
	}

	public void clear() {
		androidMavenBuildEvents.clear();
	}
}
