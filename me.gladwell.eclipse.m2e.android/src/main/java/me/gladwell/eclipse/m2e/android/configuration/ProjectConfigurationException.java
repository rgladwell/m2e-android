package me.gladwell.eclipse.m2e.android.configuration;

import me.gladwell.eclipse.m2e.android.AndroidMavenException;

public class ProjectConfigurationException extends AndroidMavenException {

	private static final long serialVersionUID = -4510508504367403748L;

	public ProjectConfigurationException(String message) {
		super(message);
	}

	public ProjectConfigurationException(Throwable cause) {
		super(cause);
	}

	public ProjectConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

}
