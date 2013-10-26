package me.gladwell.eclipse.m2e.android;

import static org.eclipse.jdt.core.JavaCore.setClasspathContainer;

import javax.inject.Inject;

import me.gladwell.eclipse.m2e.android.configuration.ClasspathLoader;
import me.gladwell.eclipse.m2e.android.configuration.NonRuntimeDependenciesClasspathContainer;
import me.gladwell.eclipse.m2e.android.configuration.PrunePlatformProvidedDependencies;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;

public class NonRuntimeDependenciesContainerInitializer extends ClasspathContainerInitializer {

    @Inject @PrunePlatformProvidedDependencies private ClasspathLoader loader;

    @Override
    public void initialize(IPath path, IJavaProject project) throws CoreException {
        final IClasspathContainer nonRuntimeContainer = new NonRuntimeDependenciesClasspathContainer(loader, project);
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
