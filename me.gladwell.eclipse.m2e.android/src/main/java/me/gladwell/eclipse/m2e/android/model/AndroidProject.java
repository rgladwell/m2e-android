package me.gladwell.eclipse.m2e.android.model;

import java.util.List;

public interface AndroidProject {

	public boolean isAndroidProject();

	public boolean isLibrary();

	public List<String> getProvidedDependencies();

	public List<String> getLibraryDependencies();

}