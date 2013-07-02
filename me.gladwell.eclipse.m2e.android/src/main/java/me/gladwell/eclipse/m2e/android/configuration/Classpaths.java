package me.gladwell.eclipse.m2e.android.configuration;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.m2e.jdt.IClasspathDescriptor;

import com.google.common.base.Predicate;

class Classpaths {

    private Classpaths() {
    }

    public static IClasspathEntry findContainerMatching(final IClasspathDescriptor classpath, final String path) {
        return matchContainer(classpath, new Predicate<IClasspathEntry>() {
            public boolean apply(IClasspathEntry entry) {
                return entry.getPath().toOSString().equals(path);
            }
        });
    }

    public static IClasspathEntry findContainerContaining(final IClasspathDescriptor classpath, final String fragment) {
        return matchContainer(classpath, new Predicate<IClasspathEntry>() {
            public boolean apply(IClasspathEntry entry) {
                return entry.getPath().toOSString().contains(fragment);
            }
        });
    }

    private static IClasspathEntry matchContainer(IClasspathDescriptor classpath, Predicate<IClasspathEntry> predicate) {
        for(IClasspathEntry entry : classpath.getEntries()) {
            if(entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
                if(predicate.apply(entry)) {
                    return entry;
                }
            }
        }
        return null;
        
    }

}
