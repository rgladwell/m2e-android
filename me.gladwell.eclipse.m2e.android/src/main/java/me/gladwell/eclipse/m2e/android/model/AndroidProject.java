package me.gladwell.eclipse.m2e.android.model;

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;

public interface AndroidProject {

	public enum Type {

		Application, Library;

	}

	IJavaProject getJavaProject();
	Type getType();
	String getPlatform();
	IPath getClassesOutputFolder();
	IPath getGenFolder();
	List<String> getProvidedDependencies();

}
