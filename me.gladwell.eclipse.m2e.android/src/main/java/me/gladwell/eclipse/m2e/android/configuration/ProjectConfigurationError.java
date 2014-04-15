/*******************************************************************************
 * Copyright (c) 2013 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

public abstract class ProjectConfigurationError extends ProjectConfigurationException {

    private static final long serialVersionUID = 4104424860840533651L;

    public ProjectConfigurationError(String message, Throwable cause) {
        super(message, cause);
    }

    public ProjectConfigurationError(String message) {
        super(message);
    }

    public ProjectConfigurationError(Throwable cause) {
        super(cause);
    }

    public abstract String getType();
}
