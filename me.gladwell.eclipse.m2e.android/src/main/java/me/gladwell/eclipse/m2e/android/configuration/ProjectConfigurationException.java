/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import me.gladwell.eclipse.m2e.android.AndroidMavenException;

public class ProjectConfigurationException extends AndroidMavenException {

    private static final long serialVersionUID = -4510508504367403748L;

    public ProjectConfigurationException(String message) {
        super(message);
    }

    public ProjectConfigurationException(Throwable cause) {
        super(cause);
    }

    public ProjectConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

}
