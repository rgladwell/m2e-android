package me.gladwell.eclipse.m2e.android.configuration;

public class ProjectConfigurationException extends RuntimeException {

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
