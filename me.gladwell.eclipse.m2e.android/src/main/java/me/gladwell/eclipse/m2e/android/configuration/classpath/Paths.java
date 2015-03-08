package me.gladwell.eclipse.m2e.android.configuration.classpath;

import me.gladwell.eclipse.m2e.android.project.Dependency;
import me.gladwell.eclipse.m2e.android.project.IDEAndroidProject;

import com.google.common.base.Function;

public class Paths {

    private Paths() {
    }

    public static Function<IDEAndroidProject, String> eclipseProjectToPathFunction() {
        return new Function<IDEAndroidProject, String>() {
            public String apply(IDEAndroidProject project) {
                return project.getPath().toString();
            }
            
        };
    }

    public static Function<Dependency, String> dependencyToPathFunction() {
        return new Function<Dependency, String>() {
            public String apply(Dependency dependency) {
                return dependency.getPath();
            }
            
        };
    }

}
