package me.gladwell.eclipse.m2e.android.configuration.classpath;

import static com.google.common.collect.Lists.transform;

import java.util.List;

import me.gladwell.eclipse.m2e.android.project.AndroidWorkspace;
import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IClasspathEntryDescriptor;
import org.eclipse.m2e.jdt.IClasspathDescriptor.EntryFilter;

import com.google.common.base.Function;
import com.google.inject.Inject;

public class RemoveNonRuntimeProjectsConfigurer implements RawClasspathConfigurer {

    private final AndroidWorkspace workspace;

    @Inject
    public RemoveNonRuntimeProjectsConfigurer(AndroidWorkspace workspace) {
        this.workspace = workspace;
    }

    public void configure(MavenAndroidProject mavenProject, EclipseAndroidProject eclipseProject, IClasspathDescriptor classpath) {
        if (eclipseProject.shouldResolveWorkspaceProjects()) {
           final List<EclipseAndroidProject> nonRuntimeProjects = workspace.findOpenWorkspaceDependencies(mavenProject.getNonRuntimeDependencies());

           final List<String> nonRuntimeProjectPaths = transform(nonRuntimeProjects, new Function<EclipseAndroidProject, String>() {
               public String apply(EclipseAndroidProject project) {
                  return project.getPath();
               }
           });

            classpath.removeEntry(new EntryFilter() {
                public boolean accept(IClasspathEntryDescriptor descriptor) {
                    return nonRuntimeProjectPaths.contains(descriptor.getPath().toString());
                }
            });
        }
    }

}
