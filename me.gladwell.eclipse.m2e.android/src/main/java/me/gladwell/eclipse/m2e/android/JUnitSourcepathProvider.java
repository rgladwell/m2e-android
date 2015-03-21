package me.gladwell.eclipse.m2e.android;

import static java.util.Arrays.asList;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;
import static org.eclipse.jdt.launching.JavaRuntime.newArchiveRuntimeClasspathEntry;

import java.util.ArrayList;
import java.util.List;

import me.gladwell.eclipse.m2e.android.project.AndroidProjectFactory;
import me.gladwell.eclipse.m2e.android.project.Dependency;
import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IRuntimeClasspathProvider;

import com.google.inject.Inject;

public class JUnitSourcepathProvider implements IRuntimeClasspathProvider {
    
    private final IRuntimeClasspathProvider classpathProvider;
    private final IWorkspace workspace;
    private final AndroidProjectFactory<EclipseAndroidProject, IProject> eclipseAndroidFactory;
    private final AndroidProjectFactory<MavenAndroidProject, EclipseAndroidProject> mavenAndroidFactory;
    
    @Inject
    public JUnitSourcepathProvider(@MavenSource IRuntimeClasspathProvider classpathProvider, IWorkspace workspace,
            AndroidProjectFactory<EclipseAndroidProject, IProject> eclipseAndroidFactory,
            AndroidProjectFactory<MavenAndroidProject, EclipseAndroidProject> mavenAndroidFactory) {
        this.classpathProvider = classpathProvider;
        this.workspace = workspace;
        this.eclipseAndroidFactory = eclipseAndroidFactory;
        this.mavenAndroidFactory = mavenAndroidFactory;
    }

    public IRuntimeClasspathEntry[] computeUnresolvedClasspath(ILaunchConfiguration configuration) throws CoreException {
        List<IRuntimeClasspathEntry> classpath = new ArrayList<IRuntimeClasspathEntry>(
                asList(classpathProvider.computeUnresolvedClasspath(configuration)));
        
        addNonRuntimeDependencies(configuration, classpath);
        
        return classpath.toArray(new IRuntimeClasspathEntry[classpath.size()]);
    }
    
    private void addNonRuntimeDependencies(ILaunchConfiguration config, List<IRuntimeClasspathEntry> classpath)
            throws CoreException {
        IProject project = workspace.getRoot().getProject(config.getAttribute(ATTR_PROJECT_NAME, (String) null));
        MavenAndroidProject androidProject = mavenAndroidFactory.createAndroidProject(eclipseAndroidFactory.createAndroidProject(project));
        
        for (Dependency dependency : androidProject.getNonRuntimeDependencies()) {
            classpath.add(newArchiveRuntimeClasspathEntry(new Path(dependency.getPath())));
        }
    }

    public IRuntimeClasspathEntry[] resolveClasspath(IRuntimeClasspathEntry[] entries,
            ILaunchConfiguration configuration) throws CoreException {
        return classpathProvider.resolveClasspath(entries, configuration);
    }

}
