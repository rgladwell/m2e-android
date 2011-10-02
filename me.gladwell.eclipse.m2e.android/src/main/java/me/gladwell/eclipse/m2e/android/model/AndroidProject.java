package me.gladwell.eclipse.m2e.android.model;

public interface AndroidProject {

	public enum Type {

		Application, Library;

	}

	Type getType();
	String getPlatform();

}
