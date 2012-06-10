package me.gladwell.eclipse.m2e.android.configuration;

public abstract class ProjectConfigurationError extends ProjectConfigurationException {

	private static final long serialVersionUID = 4104424860840533651L;

	public ProjectConfigurationError(String message, Throwable cause) {
		super(message, cause);
	}

	public ProjectConfigurationError(String message) {
		super(message);
	}

	public ProjectConfigurationError(Throwable cause) {
		super(cause);
	}

	public abstract String getType();
}
