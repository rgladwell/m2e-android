package me.gladwell.eclipse.m2e.android.resolve;

import java.util.List;

import me.gladwell.eclipse.m2e.android.project.Dependency;

public interface DependencyResolver {

    List<org.sonatype.aether.artifact.Artifact> resolveDependencies(Dependency android, String string);

}
