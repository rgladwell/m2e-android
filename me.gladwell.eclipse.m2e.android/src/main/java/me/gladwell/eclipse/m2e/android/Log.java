package me.gladwell.eclipse.m2e.android;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;

public class Log {

    private final static ILog log = AndroidMavenPlugin.getDefault().getLog();

    private Log() {}

    public static void warn(String message) {
        log.log(new Status(Status.WARNING, AndroidMavenPlugin.PLUGIN_ID, message));
    }

}
