/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import static java.io.File.separator;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

import me.gladwell.eclipse.m2e.android.project.AndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IClasspathDescriptor.EntryFilter;
import org.eclipse.m2e.jdt.IClasspathEntryDescriptor;
import org.eclipse.m2e.jdt.IClasspathManager;

import com.android.ide.eclipse.adt.AdtConstants;

public class MavenAndroidClasspathConfigurer implements AndroidClasspathConfigurer {

	private static final String ANDROID_GEN_FOLDER = "gen";
    public static final String ANDROID_CLASSES_FOLDER = "bin" + separator + "classes";
    
    private static final String ANDROID_MAVEN_PLUGIN_GROUP_ID = "com.jayway.maven.plugins.android.generation2"; //$NON-NLS-1$
    private static final String ANDROID_MAVEN_PLUGIN_ARTIFACT_ID = "android-maven-plugin"; //$NON-NLS-1$
    private static final String IGNORE_WARNING_CONFIGURATION_NAME = "ignoreOptionalWarningsInGenFolder"; //$NON-NLS-1$

	/**
	 * Ignore any optional warning in the "gen" folder, iff the Maven plugin configuration
	 * contains the correct flag 'ignoreOptionalWarningsInGenFolder'.
	 * 
	 * @param project
	 * @param classpathDescriptor
	 */
    private static void ignoreOptionalWarningsOnSourceFolder(AndroidProject project, IClasspathEntryDescriptor classpathDescriptor) {
    	String warningAttribute = null;
    	// Test if the Eclipse IDE is able to change the "warning" flag (only since Indigo)
    	try {
    		Field field = IClasspathAttribute.class.getField("IGNORE_OPTIONAL_PROBLEMS"); //$NON-NLS-1$
    		assert(field!=null);
    		warningAttribute = field.get(null).toString();
		}
    	catch (Throwable _) {
    		// The Eclipse platform does not provide the feature to change the warning flag.
			return ;
		}
    	
    	assert(warningAttribute!=null);
    	
		if (classpathDescriptor!=null && project instanceof MavenAndroidProject) {
			MavenAndroidProject mavenAndroidProject = (MavenAndroidProject)project;
			MavenProject mavenProject = mavenAndroidProject.getMavenProject();
			if (mavenProject!=null && mavenProject.getBuildPlugins()!=null) {
				List<Plugin> plugins = mavenProject.getBuildPlugins();
				Iterator<Plugin> pluginIterator = plugins.iterator();
				Xpp3Dom configuration = null;
				while (configuration==null && pluginIterator.hasNext()) {
					Plugin plugin = pluginIterator.next();
					if ( ANDROID_MAVEN_PLUGIN_GROUP_ID.equals( plugin.getGroupId() ) &&
						 ANDROID_MAVEN_PLUGIN_ARTIFACT_ID.equals(plugin.getArtifactId())) {
						configuration = (Xpp3Dom) plugin.getConfiguration();
					}
				}

				if (configuration!=null) {
					configuration = configuration.getChild(IGNORE_WARNING_CONFIGURATION_NAME);
					if (configuration!=null) {
						String value = configuration.getValue();
						if (value!=null && !value.isEmpty()) {
							Boolean b = Boolean.parseBoolean(value.trim());
							if (b!=null) {
								classpathDescriptor.setClasspathAttribute(warningAttribute, b.toString());
							}
						}
					}
				}
			}
		}
    }
    
    public void addGenFolder(IJavaProject javaProject, AndroidProject project, IClasspathDescriptor classpath) {
        IFolder gen = javaProject.getProject().getFolder(ANDROID_GEN_FOLDER + File.separator);
        if (!gen.exists()) {
            try {
                gen.create(true, true, new NullProgressMonitor());
            } catch (CoreException e) {
                throw new ProjectConfigurationException(e);
            }
        }

        if (!classpath.containsPath(new Path(ANDROID_GEN_FOLDER))) {
			IClasspathEntryDescriptor d = classpath.addSourceEntry(gen.getFullPath(), null, false);
			ignoreOptionalWarningsOnSourceFolder(project, d);
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

	public void removeJreClasspathContainer(IClasspathDescriptor classpath) {
		for(IClasspathEntry entry : classpath.getEntries()) {
			if(entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
            	if(entry.getPath().toOSString().contains(JavaRuntime.JRE_CONTAINER)) {
            		classpath.removeEntry(entry.getPath());
    			}
			}
		}
	}

    public void modifySourceFolderOutput(IJavaProject javaProject, AndroidProject project, IClasspathDescriptor classpath) {
        for(IClasspathEntry entry : classpath.getEntries()) {
            if(entry.getOutputLocation() != null && entry.getEntryKind() == IClasspathEntry.CPE_SOURCE
                    && !entry.getOutputLocation().equals(javaProject.getPath().append(ANDROID_CLASSES_FOLDER))) {
                classpath.removeEntry(entry.getPath());
                classpath.addSourceEntry(entry.getPath(), javaProject.getPath().append(ANDROID_CLASSES_FOLDER), true);
            }
        }
    }

	public void markMavenContainerExported(IClasspathDescriptor classpath) {
		for(IClasspathEntry entry : classpath.getEntries()) {
			if(entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
            	if(entry.getPath().toOSString().equals(IClasspathManager.CONTAINER_ID)) {
            		IClasspathEntry newEntry = JavaCore.newContainerEntry(entry.getPath(), true);
            		classpath.removeEntry(entry.getPath());
            		classpath.addEntry(newEntry);
    			}
			}
		}
	}

    public void markAndroidContainerNotExported(IClasspathDescriptor classpath) {
        for(IClasspathEntry entry : classpath.getEntries()) {
            if(entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
                if(entry.getPath().toOSString().equals(AdtConstants.CONTAINER_PRIVATE_LIBRARIES)) {
                    IClasspathEntry newEntry = JavaCore.newContainerEntry(entry.getPath(), false);
                    classpath.removeEntry(entry.getPath());
                    classpath.addEntry(newEntry);
                }
            }
        }
    }

}

