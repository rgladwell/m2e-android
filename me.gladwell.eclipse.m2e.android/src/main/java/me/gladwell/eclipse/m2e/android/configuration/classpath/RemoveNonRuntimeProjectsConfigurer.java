package me.gladwell.eclipse.m2e.android.configuration.classpath;

import java.util.List;

import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.eclipse.core.runtime.IPath;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IClasspathEntryDescriptor;
import org.eclipse.m2e.jdt.IClasspathDescriptor.EntryFilter;

public class RemoveNonRuntimeProjectsConfigurer implements RawClasspathConfigurer {

    public void configure(MavenAndroidProject project, IClasspathDescriptor classpath) {
        if (project.shouldResolveWorkspaceProjects()) {
            final List<IPath> nonRuntimeProjects = project.getNonRuntimeProjects();
            
            classpath.removeEntry(new EntryFilter() {

                public boolean accept(IClasspathEntryDescriptor descriptor) {
                    
                    return nonRuntimeProjects.contains(descriptor.getPath());
                }
            });
        }
    }

}
