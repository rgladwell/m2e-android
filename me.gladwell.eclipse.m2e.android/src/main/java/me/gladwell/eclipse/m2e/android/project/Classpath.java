/*******************************************************************************
 * Copyright (c) 2014 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.project;

import org.eclipse.jdt.core.IClasspathContainer;

public interface Classpath {

    Iterable<SourceEntry> getSourceEntries();

    void addContainer(IClasspathContainer container);

    void removeContainer(String containerId);

    void addSourceEntry(String path);

    void markExported(String path);

    void markNotExported(String path);

}
