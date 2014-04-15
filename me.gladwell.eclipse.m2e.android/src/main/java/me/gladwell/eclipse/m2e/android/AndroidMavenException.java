/*******************************************************************************
 * Copyright (c) 2012 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android;

public class AndroidMavenException extends RuntimeException {

    private static final long serialVersionUID = 7798859495216742709L;

    public AndroidMavenException() {
        super();
    }

    public AndroidMavenException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public AndroidMavenException(String arg0) {
        super(arg0);
    }

    public AndroidMavenException(Throwable arg0) {
        super(arg0);
    }

}
