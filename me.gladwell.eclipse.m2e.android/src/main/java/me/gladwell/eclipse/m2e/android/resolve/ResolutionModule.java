/*******************************************************************************
 * Copyright (c) 2013, 2014 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.resolve;

import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;

import com.google.inject.AbstractModule;

public class ResolutionModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(LibraryResolver.class).to(HardCodedLibraryResolver.class);
        bind(IMaven.class).toInstance(MavenPlugin.getMaven());
    }

}
