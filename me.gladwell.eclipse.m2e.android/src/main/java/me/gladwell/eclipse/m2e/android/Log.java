package me.gladwell.eclipse.m2e.android;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class Log {

    private static final boolean DEBUG = Boolean.parseBoolean(System.getenv("M2E_ANDROID_DEBUG"));
    private final static ILog log = AndroidMavenPlugin.getDefault().getLog();

    private Log() {}

    private static IStatus status(int status, String message) {
        return new Status(status, AndroidMavenPlugin.PLUGIN_ID, message);
    }

    private static IStatus status(int status, String message, Throwable t) {
        return new Status(status, AndroidMavenPlugin.PLUGIN_ID, message, t);
    }

    public static void warn(String message) {
        log.log(status(Status.WARNING, message));
    }

    public static void warn(String message, Throwable t) {
        log.log(status(Status.WARNING, message, t));
    }

    public static void debug(String message) {
        if(DEBUG) {
            log.log(status(Status.INFO, "[DEBUG] " + message));
        }
    }

}
