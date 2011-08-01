package me.gladwell.eclipse.m2e.android;

import java.util.EventObject;

import org.eclipse.core.runtime.IProgressMonitor;

public interface IAndroidMavenProgressMonitor extends IProgressMonitor {

	public void onAndroidMavenBuild(EventObject event);
	
}
