#Android for Maven Eclipse [![Stories in Ready](http://badge.waffle.io/rgladwell/m2e-android.png)](http://waffle.io/rgladwell/m2e-android) [![Build Status](https://travis-ci.org/rgladwell/m2e-android.svg?branch=master)](https://travis-ci.org/rgladwell/m2e-android)

[![Join the chat at https://gitter.im/rgladwell/m2e-android](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/rgladwell/m2e-android?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

_Copyright (c) 2009, 2010, 2011, 2012, 2013, 2014 Ricardo Gladwell, Hugo Josefson, Anthony Dannane, Mykola Nikishov, Raphael Ackermann, Csaba Kozák, and Sonatype Inc.. All rights reserved. This program and the accompanying materials are made available under the terms of the [Eclipse Public License 1.0](http://www.eclipse.org/legal) which accompanies this distribution_

Android for Maven Eclipse (**m2e-android**) adds [Maven](http://maven.apache.org/) support to the [Android Developer Tools](http://developer.android.com/tools/sdk/eclipse-adt.html) (ADT). This is developer documentation, for user instructions on how to install please see the [project web site.](http://rgladwell.github.com/m2e-android/)

Special thanks to our patrons who crowdfunded project development through [Patreon](http://www.patreon.com/rgladwell):

 * [Raphael Ackermann](https://twitter.com/acraphae)
 * George Baker.

#Building

Before you start you need to:

 * Install the [Android SDK](http://developer.android.com/sdk/index.html).
 * Create `ANDROID_HOME` environment variable containing the Android SDK install path. 
 * Ensure you have updated the Android SDK (execute `android update sdk --no-ui --obsolete --force`).
 * Install the latest [Maven 3](http://maven.apache.org/download.html) for command line [Tycho](http://tycho.sonatype.org/) support.
 * Install the [Maven Android SDK Deployer](https://github.com/mosabua/maven-android-sdk-deployer).

To build execute the following command from the project folder:

```
$ mvn install
```
 
###Eclipse Set-up

To configure your development environment please follow these steps:

 * Install [Eclipse Luna](http://eclipse.org/downloads).
 * Install latest [Android Development Tools](http://developer.android.com/sdk/installing/installing-adt.html).
 * Install [Eclipse Plug-in Development Environment.](http://www.eclipse.org/pde/).
 * In Eclipse select _Help -> Install new software..._ and go to the _Maven Eclipse_ update site.
 * Un-check the _Group items by category_ check box.
 * You should now see the "m2e - Extensions Development Support (Optional)" plugin.
 * Select, install and restart Eclipse.

This project consists of several sub-modules, including:

 * Parent POM.
 * Core Eclipse plugin.
 * Test suite.
 * Eclipse feature.
 * Various [target definitions](https://wiki.eclipse.org/PDE/Target_Definitions).
 * and the update site.

To clone them into your Eclipse workspace follow these steps: 

 * Clone this git repository into your local workspace.
 * In Eclipse select _File_ -> _Import..._ in the menu and then select _Maven_ -> _Existing Maven Projects_.
 * Choose the parent folder you just cloned into your workspace (should be _m2e-android_), select all sub projects and click _Finish_
 * If you get a _"Plugin exception not covered by lifecycle configuration"_ error, click _"Discover new m2e connectors."_ This will allow you to install the Tycho configurator.
 * Mark all new projects in the _Package Explorer_ and right-click on them to select _Team_ -> _Share Project_.
 * Select _Git_ and click _Next_.
 * Check the _Use or create Repository in parent folder of project_ and click _Finish_.

##Further Reading

For more information on developing the m2e-android plug-in please see the following:

* [Contributing](https://github.com/rgladwell/m2e-android/blob/master/CONTRIBUTING.md)
* [Deployment](https://github.com/rgladwell/m2e-android/wiki/Deploying)
* [Travis](https://github.com/rgladwell/m2e-android/wiki/Travis)
* [Maven Eclipse](https://www.eclipse.org/m2e/)
* [Maven Android Plugin](https://code.google.com/p/maven-android-plugin)
