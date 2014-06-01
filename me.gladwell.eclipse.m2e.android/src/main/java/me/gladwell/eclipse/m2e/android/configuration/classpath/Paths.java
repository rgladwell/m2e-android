package me.gladwell.eclipse.m2e.android.configuration.classpath;

import me.gladwell.eclipse.m2e.android.project.Dependency;
import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProject;

import com.google.common.base.Function;

public class Paths {

    private Paths() {
    }

    public static Function<EclipseAndroidProject, String> eclipseProjectToPathFunction() {
        return new Function<EclipseAndroidProject, String>() {
            public String apply(EclipseAndroidProject project) {
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
