package com.byluroid.eclipse.maven.android;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class AndroidMavenPlugin extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.byluroid.eclipse.maven.android";

	// The shared instance
	private static AndroidMavenPlugin plugin;

	/**
	 * The constructor
	 */
	public AndroidMavenPlugin() {
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static AndroidMavenPlugin getDefault() {
		return plugin;
	}

}
