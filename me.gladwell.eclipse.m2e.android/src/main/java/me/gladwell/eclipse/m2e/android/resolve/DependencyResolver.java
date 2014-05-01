/*******************************************************************************
 * Copyright (c) 2013 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.resolve;

import java.util.List;

import me.gladwell.eclipse.m2e.android.project.Dependency;

import org.eclipse.aether.repository.RemoteRepository;

public interface DependencyResolver {

    List<org.eclipse.aether.artifact.Artifact> resolveDependencies(Dependency android, String string, List<RemoteRepository> repositories);

}
