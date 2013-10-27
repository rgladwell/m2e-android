/*******************************************************************************
 * Copyright (c) 2015 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.embedder.IMavenConfigurationChangeListener;
import org.eclipse.m2e.core.embedder.MavenConfigurationChangeEvent;
import org.eclipse.m2e.core.project.IMavenProjectChangedListener;
import org.eclipse.m2e.core.project.MavenProjectChangedEvent;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class EclipseEventsToEventBusListener implements IMavenProjectChangedListener, IMavenConfigurationChangeListener {

    @Inject private EventBus eventBus;

    public void mavenProjectChanged(MavenProjectChangedEvent[] events, IProgressMonitor monitor) {
        for(MavenProjectChangedEvent event : events) {
            eventBus.post(event);
        }
    }

    public void mavenConfigurationChange(MavenConfigurationChangeEvent event) throws CoreException {
        eventBus.post(event);
    }

}
