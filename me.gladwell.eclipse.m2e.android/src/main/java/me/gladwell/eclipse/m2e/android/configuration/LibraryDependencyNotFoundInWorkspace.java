package me.gladwell.eclipse.m2e.android.configuration;

import me.gladwell.eclipse.m2e.android.AndroidMavenPlugin;

public class LibraryDependencyNotFoundInWorkspace extends ProjectConfigurationError {

	private static final long serialVersionUID = 6792571204291122201L;

	public LibraryDependencyNotFoundInWorkspace(String dependency) {
		super("library " + dependency + " not found in workspace");
	}

	public String getType() {
		return AndroidMavenPlugin.APKLIB_ERROR_TYPE;
	}

}
