package me.gladwell.eclipse.m2e.android.project;

import org.eclipse.core.resources.IProject;

public interface ProjectAdaptor {

    void setAndroidProperty(Object object, String property, String value);

    void fixProject(IProject project);

    boolean isLibrary(IProject project);

}
