/*******************************************************************************
 * Copyright (c) 2011 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.googlecode.eclipse.m2e.android;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;

public class IncrementalAndroidMavenBuildParticipant extends AbstractBuildParticipant {

	@Override
	public Set<IProject> build(int kind, IProgressMonitor monitor) throws Exception {
		// TODO determine if POM dependencies have changed, or if SNAPSHOTS have updated since last build, if so:
		// TODO create new classes.dex in existing APK
		// TODO regenerate classes.dex security signature if enabled
		return null;
	}

}
