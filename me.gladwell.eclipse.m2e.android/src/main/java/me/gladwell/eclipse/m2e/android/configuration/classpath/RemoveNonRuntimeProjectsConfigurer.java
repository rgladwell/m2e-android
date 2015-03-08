package me.gladwell.eclipse.m2e.android.configuration.classpath;

import static com.google.common.collect.FluentIterable.from;
import static me.gladwell.eclipse.m2e.android.configuration.classpath.Paths.eclipseProjectToPathFunction;

import java.util.List;

import me.gladwell.eclipse.m2e.android.project.AndroidWorkspace;
import me.gladwell.eclipse.m2e.android.project.IDEAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IClasspathEntryDescriptor;
import org.eclipse.m2e.jdt.IClasspathDescriptor.EntryFilter;

import com.google.inject.Inject;

public class RemoveNonRuntimeProjectsConfigurer implements RawClasspathConfigurer {

    private final AndroidWorkspace workspace;

    @Inject
    public RemoveNonRuntimeProjectsConfigurer(AndroidWorkspace workspace) {
        this.workspace = workspace;
    }

    public void configure(MavenAndroidProject mavenProject, IDEAndroidProject eclipseProject, IClasspathDescriptor classpath) {
        if (eclipseProject.shouldResolveWorkspaceProjects()) {
           final List<IDEAndroidProject> nonRuntimeProjects = workspace.findOpenWorkspaceDependencies(mavenProject.getNonRuntimeDependencies());

           final List<String> nonRuntimeProjectPaths = from(nonRuntimeProjects)
                   .transform(eclipseProjectToPathFunction())
                   .toList();

            classpath.removeEntry(new EntryFilter() {
                public boolean accept(IClasspathEntryDescriptor descriptor) {
                    return nonRuntimeProjectPaths.contains(descriptor.getPath().toString());
                }
            });
        }
    }

}
