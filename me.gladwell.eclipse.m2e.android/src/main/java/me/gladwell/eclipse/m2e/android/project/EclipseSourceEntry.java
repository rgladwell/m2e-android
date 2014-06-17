package me.gladwell.eclipse.m2e.android.project;

import static org.eclipse.jdt.core.IClasspathAttribute.IGNORE_OPTIONAL_PROBLEMS;

import java.io.File;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IClasspathEntryDescriptor;

public class EclipseSourceEntry implements SourceEntry {

    private final IJavaProject project;
    private final IClasspathDescriptor classpath;
    private final IClasspathEntryDescriptor entry;

    public EclipseSourceEntry(IJavaProject project, IClasspathDescriptor classpath, IClasspathEntryDescriptor entry) {
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
        File file = project.getProject().getLocation().toFile();
        File path = new File(file, entry.getPath().removeFirstSegments(1).toOSString());
        return path.getAbsolutePath();
    }

    public void ignoreOptionalWarnings() {
        entry.setClasspathAttribute(IGNORE_OPTIONAL_PROBLEMS, "true");
    }

}
