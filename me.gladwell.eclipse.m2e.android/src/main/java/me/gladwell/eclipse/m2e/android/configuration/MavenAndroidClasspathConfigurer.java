package me.gladwell.eclipse.m2e.android.configuration;

import java.util.List;

import me.gladwell.eclipse.m2e.android.model.AndroidProject;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IClasspathDescriptor.EntryFilter;
import org.eclipse.m2e.jdt.IClasspathEntryDescriptor;

import com.google.inject.Inject;

public class MavenAndroidClasspathConfigurer implements AndroidClasspathConfigurer {
	
	@Inject 
	private IWorkspaceRoot workspaceRoot;

	public void addGenFolder(AndroidProject project, IClasspathDescriptor classpath) {
		if (!classpath.containsPath(project.getGenFolder())) {
			classpath.addSourceEntry(project.getGenFolder(), project.getClassesOutputFolder(), true);
		}
	}

	public void removeNonRuntimeDependencies(AndroidProject project, IClasspathDescriptor classpath) {
		final List<String> providedDependencies = project.getProvidedDependencies();

		classpath.removeEntry(new EntryFilter() {
			public boolean accept(IClasspathEntryDescriptor descriptor) {
				return providedDependencies.contains(descriptor.getPath().toOSString());
			}
		});
	}

	public void modifySourceFolderOutput(AndroidProject project, IClasspathDescriptor classpath) {
		for(IClasspathEntry entry : classpath.getEntries()) {
			if(entry.getOutputLocation() != null && entry.getEntryKind() == IClasspathEntry.CPE_SOURCE
					&& !entry.getOutputLocation().equals(project.getClassesOutputFolder())) {
				classpath.removeEntry(entry.getPath());
				classpath.addSourceEntry(entry.getPath(), project.getClassesOutputFolder(), false);
			}
		}
	}

	public void addClassFoldersForProjectDependencies(AndroidProject project, IClasspathDescriptor classpath) {
		final List<String> providedDependencies = project.getProvidedDependencies();
		workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		
		for (IClasspathEntry entry : classpath.getEntries()) {
			if (entry.getEntryKind() == IClasspathEntry.CPE_PROJECT) {
				
				IPath path = entry.getPath();
				
				// Maven seems to resolve it's artifacts of the dependency project to ./target/classes. 
				// However right now our output ends up in /bin/classes due to ADT bugs.
				// Furthermore, the ./target/classes directory is never actually added to the classpath because 
				// it's resolved to a project.
				//
				// So instead, if it's not in the PROVIDED scope, we'll add a class folder the Maven provided 
				// classpath, which Dex will understand and include in the APK.
				
				String potentialPathToProject = path.append("target/classes").toOSString();
				IResource projectWithTargetClasses = workspaceRoot.findMember(potentialPathToProject);

				if (projectWithTargetClasses != null) {
					String fullPath = projectWithTargetClasses.getRawLocation().toOSString();
				
					if (providedDependencies.contains(fullPath))
						continue;
				}
				
				path = path.append(new Path("bin/classes"));
				IResource resource = workspaceRoot.findMember(path);
				if (resource != null) {
					classpath.addLibraryEntry(path);
				}
			}
		}
	}
}
