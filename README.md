#Android for Maven Eclipse [![Stories in Ready](http://badge.waffle.io/rgladwell/m2e-android.png)](http://waffle.io/rgladwell/m2e-android) [![Build Status](https://travis-ci.org/rgladwell/m2e-android.svg?branch=master)](https://travis-ci.org/rgladwell/m2e-android)

_Copyright (c) 2009, 2010, 2011, 2012, 2013, 2014 Ricardo Gladwell, Hugo Josefson, Anthony Dannane, Mykola Nikishov, Raphael Ackermann, Csaba Kozák, and Sonatype Inc.. All rights reserved. This program and the accompanying materials are made available under the terms of the [Eclipse Public License 1.0](http://www.eclipse.org/legal) which accompanies this distribution_

Android for Maven Eclipse (**m2e-android**) is a [Maven Eclipse](http://www.eclipse.org/m2e/) extension that adds Maven support to the [Android Developer Tools](http://developer.android.com/tools/sdk/eclipse-adt.html) (ADT). It brings the power of Maven dependency management into the ADT.

For instructions on how to get started using the m2e-android please see the [project web site.](http://rgladwell.github.com/m2e-android/)

Special thanks to our patrons who crowdfunded project development through [Patreon](http://www.patreon.com/rgladwell):

 * [Raphael Ackermann](https://twitter.com/acraphae)

#Building

Before you start you need to:

 * Install the [Android SDK](http://developer.android.com/sdk/index.html).
 * Create `ANDROID_HOME` environment variable containing the Android SDK install path. 
 * Ensure you have the latest update on the Android SDK (execute `android update sdk --no-ui --obsolete --force`) 
 * Install [Maven 3](http://maven.apache.org/download.html) for command line [Tycho](http://tycho.sonatype.org/) support.
 * Install the [Maven Android SDK Deployer](https://github.com/mosabua/maven-android-sdk-deployer)

To build execute the following commands from within the project folder:

```
$ mvn --file org.sonatype.aether/pom.xml install
$ mvn install
```
 
###Eclipse Set-up

This section details how to set-up your Eclipse development environment to make code changes to the **m2e-android** code base:

 * Install [Eclipse Luna](http://eclipse.org/downloads).
 * Install latest [Android Development Tools.](http://developer.android.com/sdk/installing/installing-adt.html)
 * Install [Eclipse Plug-in Development Environment.](http://www.eclipse.org/pde/)
 * Install [M2E Plugin.](http://www.eclipse.org/m2e/download)
 * In Eclipse select _Help -> Install new software..._ and go to the m2eclipse update site.
 * Un-check the _Group items by category_ check box.
 * You should now see the "m2e - Extensions Development Support (Optional)" plugin.
 * Select, install and restart Eclipse.

The **m2e-android** project is made up of several sub-modules: the parent POM project, the core plugin, a test suite, an Android tools plugin, the feature project and an update site project.

To clone them into your Eclipse workspace follow these steps: 

 * Clone this git repository into your local workspace.
 * In Eclipse select _File_ -> _Import..._ in the menu and then select _Maven_ -> _Existing Maven Projects_.
 * Choose the parent folder you just cloned into your workspace (should be _m2e-android_), select all sub projects and click _Finish_
 * If you get a "Plugin exception not covered by lifecycle configuration" error in your POM click "Discover new m2e connectors" which should allow you to install the Tycho configurator.
 * Mark all new projects in the _Package Explorer_ and right-click on them to select _Team_ -> _Share Project_.
 * Select _Git_ and click _Next_.
 * Check the _Use or create Repository in parent folder of project_ and click _Finish_.

##Further Reading

For more information on developing the m2e-android plug-in please see the following:

* [Contributing](https://github.com/rgladwell/m2e-android/blob/master/CONTRIBUTING.md)
* [Maven Android Plugin](https://code.google.com/p/maven-android-plugin)
* [Deployment](https://github.com/rgladwell/m2e-android/wiki/Deploying)
* [Travis](https://github.com/rgladwell/m2e-android/wiki/Travis)
