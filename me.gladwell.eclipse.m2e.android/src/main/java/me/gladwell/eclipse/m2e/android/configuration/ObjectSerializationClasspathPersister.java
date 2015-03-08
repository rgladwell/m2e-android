/*******************************************************************************
 * Copyright (c) 2013, 2015 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import static com.google.common.collect.Lists.newArrayList;
import static me.gladwell.eclipse.m2e.android.Log.debug;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import me.gladwell.eclipse.m2e.android.project.IDEAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Adapted from:
 * http://git.eclipse.org/c/m2e/m2e-core.git/tree/org.eclipse.m2e.jdt/src/org/eclipse/m2e/jdt/internal/MavenClasspathContainerSaveHelper.java
 * 
 * @TODO re-factor into smaller classes
 */
public @Singleton
class ObjectSerializationClasspathPersister implements ClasspathPersister, ClasspathLoader {

    static final class LibraryEntryReplace implements Serializable {
        private static final long serialVersionUID = 3901667379326978799L;

        private final IPath path;

        private final IPath sourceAttachmentPath;

        private final IPath sourceAttachmentRootPath;

        private final IClasspathAttribute[] extraAttributes;

        private final boolean exported;

        private final IAccessRule[] accessRules;

        LibraryEntryReplace(IClasspathEntry entry) {
            this.path = entry.getPath();
            this.sourceAttachmentPath = entry.getSourceAttachmentPath();
            this.sourceAttachmentRootPath = entry.getSourceAttachmentRootPath();
            this.accessRules = entry.getAccessRules();
            this.extraAttributes = entry.getExtraAttributes();
            this.exported = entry.isExported();
        }

        IClasspathEntry getEntry() {
            return JavaCore.newLibraryEntry(path, sourceAttachmentPath, sourceAttachmentRootPath, //
                    accessRules, extraAttributes, exported);
        }
    }

    /**
     * A project IClasspathEntry replacement used for object serialization
     */
    static final class ProjectEntryReplace implements Serializable {
        private static final long serialVersionUID = -2397483865904288762L;

        private final IPath path;

        private final IClasspathAttribute[] extraAttributes;

        private final IAccessRule[] accessRules;

        private final boolean exported;

        private final boolean combineAccessRules;

        ProjectEntryReplace(IClasspathEntry entry) {
            this.path = entry.getPath();
            this.accessRules = entry.getAccessRules();
            this.extraAttributes = entry.getExtraAttributes();
            this.exported = entry.isExported();
            this.combineAccessRules = entry.combineAccessRules();
        }

        IClasspathEntry getEntry() {
            return JavaCore.newProjectEntry(path, accessRules, //
                    combineAccessRules, extraAttributes, exported);
        }
    }

    /**
     * An IClasspathAttribute replacement used for object serialization
     */
    static final class ClasspathAttributeReplace implements Serializable {
        private static final long serialVersionUID = 6370039352012628029L;

        private final String name;

        private final String value;

        ClasspathAttributeReplace(IClasspathAttribute attribute) {
            this.name = attribute.getName();
            this.value = attribute.getValue();
        }

        IClasspathAttribute getAttribute() {
            return JavaCore.newClasspathAttribute(name, value);
        }
    }

    /**
     * An IAccessRule replacement used for object serialization
     */
    static final class AccessRuleReplace implements Serializable {
        private static final long serialVersionUID = 7315582893941374715L;

        private final IPath pattern;

        private final int kind;

        AccessRuleReplace(IAccessRule accessRule) {
            pattern = accessRule.getPattern();
            kind = accessRule.getKind();
        }

        IAccessRule getAccessRule() {
            return JavaCore.newAccessRule(pattern, kind);
        }
    }

    /**
     * An IPath replacement used for object serialization
     */
    static final class PathReplace implements Serializable {
        private static final long serialVersionUID = -2361259525684491181L;

        private final String path;

        PathReplace(IPath path) {
            this.path = path.toPortableString();
        }

        IPath getPath() {
            return Path.fromPortableString(path);
        }
    }

    private final File stateLocation;

    @Inject
    public ObjectSerializationClasspathPersister(File stateLocation) {
        this.stateLocation = stateLocation;
    }

    public void save(MavenAndroidProject mavenProject, IDEAndroidProject eclipseProject, Iterable<IClasspathEntry> classpath) {
        ObjectOutputStream os = null;
        try {
            File file = new File(stateLocation, eclipseProject.getProject().getName());
            os = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file))) {
                {
                    enableReplaceObject(true);
                }

                protected Object replaceObject(Object o) throws IOException {
                    if (o instanceof IClasspathEntry) {
                        IClasspathEntry e = (IClasspathEntry) o;
                        if (e.getEntryKind() == IClasspathEntry.CPE_PROJECT) {
                            return new ProjectEntryReplace(e);
                        } else if (e.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
                            return new LibraryEntryReplace(e);
                        }
                    } else if (o instanceof IClasspathAttribute) {
                        return new ClasspathAttributeReplace((IClasspathAttribute) o);
                    } else if (o instanceof IAccessRule) {
                        return new AccessRuleReplace((IAccessRule) o);
                    } else if (o instanceof IPath) {
                        return new PathReplace((IPath) o);
                    }
                    return super.replaceObject(o);
                }
            };
            os.writeObject(newArrayList(classpath));
            os.flush();
        } catch (IOException e) {
            throw new ProjectConfigurationException(e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    throw new ProjectConfigurationException(e);
                }
            }
        }
    }

    public List<IClasspathEntry> load(IJavaProject project) throws FileNotFoundException {
        List<IClasspathEntry> classpath = null;
        ObjectInputStream is = null;
        try {
            File file = new File(stateLocation, project.getProject().getName());
            debug("loading classpath from file=[" + file + "]");
            if (!file.exists()) {
                throw new FileNotFoundException(file.getAbsolutePath());
            }
            is = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file))) {
                {
                    enableResolveObject(true);
                }

                protected Object resolveObject(Object o) throws IOException {
                    if (o instanceof ProjectEntryReplace) {
                        return ((ProjectEntryReplace) o).getEntry();
                    } else if (o instanceof LibraryEntryReplace) {
                        return ((LibraryEntryReplace) o).getEntry();
                    } else if (o instanceof ClasspathAttributeReplace) {
                        return ((ClasspathAttributeReplace) o).getAttribute();
                    } else if (o instanceof AccessRuleReplace) {
                        return ((AccessRuleReplace) o).getAccessRule();
                    } else if (o instanceof PathReplace) {
                        return ((PathReplace) o).getPath();
                    }
                    return super.resolveObject(o);
                }
            };
            classpath = (List<IClasspathEntry>) is.readObject();
            return classpath;
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw new ProjectConfigurationException(e);
        } catch (ClassNotFoundException e) {
            throw new ProjectConfigurationException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new ProjectConfigurationException(e);
                }
            }
        }
    }

}

