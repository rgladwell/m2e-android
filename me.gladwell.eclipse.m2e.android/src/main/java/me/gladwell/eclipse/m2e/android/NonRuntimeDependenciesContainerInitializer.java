package me.gladwell.eclipse.m2e.android;

import static org.eclipse.jdt.core.JavaCore.setClasspathContainer;

import javax.inject.Inject;

import me.gladwell.eclipse.m2e.android.configuration.ClasspathLoader;
import me.gladwell.eclipse.m2e.android.configuration.NonRuntimeDependenciesClasspathContainer;
import me.gladwell.eclipse.m2e.android.project.AndroidProjectFactory;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.apache.maven.project.MavenProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;

public class NonRuntimeDependenciesContainerInitializer extends ClasspathContainerInitializer {

    @Inject private ClasspathLoader loader;
    @Inject private AndroidProjectFactory<MavenAndroidProject, MavenProject> mavenProjectFactory;
    @Inject private IMavenProjectRegistry projectRegistry;

    @Override
    public void initialize(IPath path, IJavaProject project) throws CoreException {
        final IClasspathContainer nonRuntimeContainer = new NonRuntimeDependenciesClasspathContainer(loader, mavenProjectFactory, project, projectRegistry);
        setClasspathContainer(path, new IJavaProject[] { project }, new IClasspathContainer[] { nonRuntimeContainer }, new NullProgressMonitor());
    }

    @Override
    public boolean canUpdateClasspathContainer(IPath containerPath, IJavaProject project) {
        return true;
    }

    @Override
    public void requestClasspathContainerUpdate(IPath containerPath, IJavaProject project, IClasspathContainer container) throws CoreException {
        super.requestClasspathContainerUpdate(containerPath, project, container);
    }

}
