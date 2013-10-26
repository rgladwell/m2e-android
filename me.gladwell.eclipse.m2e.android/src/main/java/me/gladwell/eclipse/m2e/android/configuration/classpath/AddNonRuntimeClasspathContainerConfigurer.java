package me.gladwell.eclipse.m2e.android.configuration.classpath;

import static me.gladwell.eclipse.m2e.android.AndroidMavenPlugin.CONTAINER_NONRUNTIME_DEPENDENCIES;
import static org.eclipse.jdt.core.JavaCore.newContainerEntry;
import static org.eclipse.jdt.core.JavaCore.setClasspathContainer;

import javax.inject.Inject;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

import me.gladwell.eclipse.m2e.android.configuration.ClasspathLoader;
import me.gladwell.eclipse.m2e.android.configuration.NonRuntimeDependenciesClasspathContainer;
import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;
import me.gladwell.eclipse.m2e.android.configuration.PrunePlatformProvidedDependencies;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

public class AddNonRuntimeClasspathContainerConfigurer implements ClasspathConfigurer {

    final private ClasspathLoader loader;

    @Inject
    public AddNonRuntimeClasspathContainerConfigurer(@PrunePlatformProvidedDependencies ClasspathLoader loader) {
        super();
        this.loader = loader;
    }

    public boolean shouldApplyTo(MavenAndroidProject project) {
        return true;
    }

    public void configure(Project project) {
        final IClasspathContainer nonRuntimeContainer = new NonRuntimeDependenciesClasspathContainer(loader, project.getJavaProject());
        try {
            setClasspathContainer(new Path(CONTAINER_NONRUNTIME_DEPENDENCIES),
                    new IJavaProject[] { project.getJavaProject() }, new IClasspathContainer[] { nonRuntimeContainer },
                    new NullProgressMonitor());
            project.getClasspath().addEntry(newContainerEntry(nonRuntimeContainer.getPath(), false));
        } catch (JavaModelException e) {
            throw new ProjectConfigurationException(e);
        }
    }

}
