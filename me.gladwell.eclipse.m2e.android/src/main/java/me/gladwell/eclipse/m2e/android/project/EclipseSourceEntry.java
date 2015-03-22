package me.gladwell.eclipse.m2e.android.project;

import static org.eclipse.jdt.core.IClasspathAttribute.IGNORE_OPTIONAL_PROBLEMS;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.m2e.jdt.IClasspathEntryDescriptor;

public class EclipseSourceEntry implements SourceEntry {

    private final IProject project;
    private final IClasspathEntryDescriptor entry;

    public EclipseSourceEntry(IProject project, IClasspathEntryDescriptor entry) {
        super();
        this.project = project;
        this.entry = entry;
    }

    public void setOutputLocation(String path) {
        entry.setOutputLocation(project.getFullPath().append(path));
    }

    public String getOutputLocation() {
        return entry.getOutputLocation().toString();
    }

    public String getPath() {
        File file = project.getLocation().toFile();
        File path = new File(file, entryPathWithoutProjectName());
        return path.getAbsolutePath();
    }

    private String entryPathWithoutProjectName() {
        return entry.getPath().removeFirstSegments(1).toString();
    }

    public void ignoreOptionalWarnings() {
        entry.setClasspathAttribute(IGNORE_OPTIONAL_PROBLEMS, "true");
    }

}
