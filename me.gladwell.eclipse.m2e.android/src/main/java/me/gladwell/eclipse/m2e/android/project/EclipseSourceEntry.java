package me.gladwell.eclipse.m2e.android.project;

import java.io.File;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.m2e.jdt.IClasspathDescriptor;

public class EclipseSourceEntry implements SourceEntry {

    private final IJavaProject project;
    private final IClasspathDescriptor classpath;
    private final IClasspathEntry entry;

    public EclipseSourceEntry(IJavaProject project, IClasspathDescriptor classpath, IClasspathEntry entry) {
        super();
        this.project = project;
        this.classpath = classpath;
        this.entry = entry;
    }

    public void setOutputLocation(String path) {
        classpath.removeEntry(entry.getPath());
        classpath.addSourceEntry(entry.getPath(), project.getPath().append(path), true);
    }

    public String getOutputLocation() {
        return entry.getOutputLocation().toString();
    }

    public String getPath() {
        String parent = project.getProject().getLocation().toFile().getParent();
        File path = new File(parent, entry.getPath().toOSString());
        return path.getAbsolutePath();
    }

}
