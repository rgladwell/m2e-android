package me.gladwell.eclipse.m2e.android.configuration.classpath;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.gladwell.eclipse.m2e.android.configuration.DependencyNotFoundInWorkspace;
import me.gladwell.eclipse.m2e.android.project.AndroidWorkspace;
import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;
import me.gladwell.eclipse.m2e.android.project.Dependency;

import org.apache.maven.artifact.Artifact;
import org.eclipse.core.runtime.IPath;
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

    public void configure(MavenAndroidProject mavenProject, EclipseAndroidProject eclipseProject, IClasspathDescriptor classpath) {
        if (eclipseProject.shouldResolveWorkspaceProjects()) {
            final List<IPath> nonRuntimeProjects = getNonRuntimeProjects(mavenProject.getNonRuntimeDependencies());
            
            classpath.removeEntry(new EntryFilter() {

                public boolean accept(IClasspathEntryDescriptor descriptor) {
                    
                    return nonRuntimeProjects.contains(descriptor.getPath());
                }
            });
        }
    }
    
    private List<IPath> getNonRuntimeProjects(List<Dependency> dependencies) {
        Set<IPath> nonRuntimeProjects = new HashSet<IPath>();
        
        for (Dependency dependency : dependencies) {
            
            if (!Artifact.SCOPE_COMPILE.equals(dependency.getScope()) && !Artifact.SCOPE_RUNTIME.equals(dependency.getScope())) {
                EclipseAndroidProject workspaceDependency = null;
                
                try {
                    workspaceDependency = workspace.findOpenWorkspaceDependency(dependency);
                } catch (DependencyNotFoundInWorkspace e) {
                    continue;
                }
                
                nonRuntimeProjects.add(workspaceDependency.getProject().getFullPath());
            }
        }

        return new ArrayList<IPath>(nonRuntimeProjects);
    }

}
