/*******************************************************************************
 * Copyright (c) 2013 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android;

import static me.gladwell.eclipse.m2e.android.AndroidMavenPlugin.PLUGIN_ID;
import static me.gladwell.eclipse.m2e.android.AndroidMavenPlugin.getDefault;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class Logger {

    private Logger() {}

    private static ILog log = getDefault().getLog();

    private static void log(int level, String message) {
        log.log(new Status(level, PLUGIN_ID, message));
    }

    private static void log(int level, String message, Throwable t) {
        log.log(new Status(level, PLUGIN_ID, message, t));
    }

    public static void info(String message) {
        log(IStatus.INFO, message);
    }

    public static void warn(String message) {
        log(IStatus.WARNING, message);
    }

    public static void warn(String message, Throwable t) {
        log(IStatus.WARNING, message, t);
    }

    public static void error(String message, Throwable t) {
        log(IStatus.ERROR, message, t);
    }

}
