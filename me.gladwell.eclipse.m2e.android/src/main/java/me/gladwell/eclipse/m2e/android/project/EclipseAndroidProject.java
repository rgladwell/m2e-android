package me.gladwell.eclipse.m2e.android.project;

import java.util.List;

import org.eclipse.core.resources.IProject;

public interface EclipseAndroidProject extends AndroidProject {

	public IProject getProject();

	public void setAndroidProject(boolean androidProject);

	public void setLibrary(boolean isLibrary);

	public void setProvidedDependencies(List<String> providedDependencies);

	public void setLibraryDependencies(List<String> libraryDependencies);

	public void fixProject();

}
