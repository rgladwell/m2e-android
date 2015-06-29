package me.gladwell.eclipse.m2e.android;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IClasspathEntryDescriptor;
import org.eclipse.m2e.jdt.internal.ClasspathDescriptor;

import me.gladwell.eclipse.m2e.android.configuration.ProjectConfigurationException;
import me.gladwell.eclipse.m2e.android.project.Entry;
import me.gladwell.eclipse.m2e.android.project.IDEAndroidProject;
import me.gladwell.eclipse.m2e.android.project.IDEAndroidProjectFactory;

public class ClasspathChangeListener implements IElementChangedListener {

    @Inject IDEAndroidProjectFactory factory;

    public void elementChanged(ElementChangedEvent event) {
        visit(event.getDelta());
    }

    private void visit(IJavaElementDelta delta) {
        IJavaElement el = delta.getElement();
        switch (el.getElementType()) {
        case IJavaElement.JAVA_MODEL:
            visitChildren(delta);
            break;
        case IJavaElement.JAVA_PROJECT:
            if (isClasspathChanged(delta.getFlags())) {
                try {
                    final IJavaProject javaProject = (IJavaProject) delta.getElement();
                    final IClasspathDescriptor classpath = new ClasspathDescriptor(javaProject);

                    IDEAndroidProject androidProject = factory.createAndroidProject(javaProject.getProject(),
                            classpath);

                    Entry librariesContainer = androidProject.getClasspath().getAndroidClasspathContainer();

                    if (!androidProject.isAndroidProject() || !librariesContainer.isPresent()) {
                        return;
                    }

                    if (librariesContainer.isPresent() && !librariesContainer.isExported()) {
                        return;
                    }

                    librariesContainer.markNotExported();

                    new WorkspaceJob("Marking Android libraries container not exported") {

                        @Override
                        public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
                            List<IClasspathEntryDescriptor> descriptors = classpath.getEntryDescriptors();
                            IClasspathEntry[] entries = new IClasspathEntry[descriptors.size()];

                            for (int i = 0; i < descriptors.size(); ++i) {
                                entries[i] = descriptors.get(i).toClasspathEntry();
                            }

                            javaProject.setRawClasspath(entries, new NullProgressMonitor());
                            return Status.OK_STATUS;
                        }
                    }.schedule();

                } catch (JavaModelException e) {
                    throw new ProjectConfigurationException("Could not mark Android libraries container exported", e);
                }

            }
            break;
        default:
            break;
        }
    }

    private boolean isClasspathChanged(int flags) {
        return 0 != (flags & (IJavaElementDelta.F_CLASSPATH_CHANGED));
    }

    private void visitChildren(IJavaElementDelta delta) {
        for (IJavaElementDelta c : delta.getAffectedChildren()) {
            visit(c);
        }
    }

}