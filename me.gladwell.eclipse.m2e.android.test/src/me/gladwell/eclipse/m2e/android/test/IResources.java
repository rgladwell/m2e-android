package me.gladwell.eclipse.m2e.android.test;

import java.io.File;
import java.io.IOException;

import org.codehaus.plexus.util.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

public class IResources {

    private IResources() {}

    public static void delete(IFile file) throws CoreException {
        file.delete(true, null);
    }

    public static void rename(IFile from, IFile to) throws IOException {
        FileUtils.rename(file(from), file(to));
    }

    public static void rename(IPath from, IPath to) throws IOException {
        FileUtils.rename(file(from), file(to));
    }

    public static File file(IFile ifile) {
        return file(ifile.getRawLocation());
    }

    public static File file(IPath path) {
        return path.makeAbsolute().toFile();
    }

}
