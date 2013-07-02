package me.gladwell.eclipse.m2e.android.configuration.classpath;

import static me.gladwell.eclipse.m2e.android.AndroidMavenPlugin.CONTAINER_NONRUNTIME_DEPENDENCIES;
import static org.eclipse.jdt.core.JavaCore.newContainerEntry;
import static org.eclipse.jdt.core.JavaCore.setClasspathContainer;

import javax.inject.Inject;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;

import me.gladwell.eclipse.m2e.android.configuration.ClasspathLoader;
import me.gladwell.eclipse.m2e.android.configuration.NonRuntimeDependenciesClasspathContainer;
import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;
import me.gladwell.eclipse.m2e.android.project.AndroidProjectFactory;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

public class AddNonRuntimeClasspathContainerConfigurer implements ClasspathConfigurer {

    final private ClasspathLoader loader;
    final private RepositorySystem repository;
    final private AndroidProjectFactory<MavenAndroidProject, MavenProject> mavenProjectFactory;
    final private RepositorySystemSession session;
    final private IMavenProjectRegistry projectRegistry;

    @Inject
    public AddNonRuntimeClasspathContainerConfigurer(ClasspathLoader loader, RepositorySystem repository,
            AndroidProjectFactory<MavenAndroidProject, MavenProject> mavenProjectFactory,
            RepositorySystemSession session, IWorkspace workspace, IMavenProjectRegistry projectRegistry) {
        super();
        this.loader = loader;
        this.repository = repository;
        this.mavenProjectFactory = mavenProjectFactory;
        this.session = session;
        this.projectRegistry = projectRegistry;
    }

    public boolean shouldApplyTo(MavenAndroidProject project) {
        return true;
    }

    public void configure(Project project) {
        final IClasspathContainer nonRuntimeContainer = new NonRuntimeDependenciesClasspathContainer(loader, repository, mavenProjectFactory, project.getJavaProject(), session, projectRegistry);
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
