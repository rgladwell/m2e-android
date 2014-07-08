/*******************************************************************************
 * Copyright (c) 2013, 2014 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.resolve;

import static me.gladwell.eclipse.m2e.android.Log.warn;
import me.gladwell.eclipse.m2e.android.resolve.eclipse.EclipseAetherModule;
import me.gladwell.eclipse.m2e.android.resolve.sonatype.SonatypeAetherModule;

import org.codehaus.plexus.PlexusContainer;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.embedder.MavenImpl;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.core.runtime.CoreException;

import com.google.inject.AbstractModule;

public class ResolutionModule extends AbstractModule {

    private final PlexusContainer mavenContainer;

    public ResolutionModule() {
        try {
            this.mavenContainer = ((MavenImpl) MavenPlugin.getMaven()).getPlexusContainer();
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean eclipseAetherAPIAvailable() {
        return mavenContainer.hasComponent(RepositorySystem.class);
    }

    @Override
    protected void configure() {
        bind(PlexusContainer.class).toInstance(mavenContainer);

        if(eclipseAetherAPIAvailable()) {
            install(new EclipseAetherModule());
        } else {
            warn("Eclipse Aether API not available - reverting to Sonatype Aether API");
            install(new SonatypeAetherModule());
        }
    }

}
