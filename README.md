#Android for Maven Eclipse [![No Maintenance Intended](http://unmaintained.tech/badge.svg)](http://unmaintained.tech/) [![Build Status](https://travis-ci.org/rgladwell/m2e-android.svg?branch=master)](https://travis-ci.org/rgladwell/m2e-android) [![Codacy Badge](https://api.codacy.com/project/badge/2174d349529848a8a7e141f044545e1d)](https://www.codacy.com/app/ricardo_3/m2e-android)

[![Join the chat at https://gitter.im/rgladwell/m2e-android](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/rgladwell/m2e-android?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

_Copyright (c) 2009, 2010, 2011, 2012, 2013, 2014, 2015 Ricardo Gladwell, Hugo Josefson, Anthony Dannane, Mykola Nikishov, Raphael Ackermann, Csaba Kozák, and Sonatype Inc.. All rights reserved. This program and the accompanying materials are made available under the terms of the [Eclipse Public License 1.0](http://www.eclipse.org/legal) which accompanies this distribution_

Android for Maven Eclipse (**m2e-android**) adds [Maven](http://maven.apache.org/) support to the [Android Developer Tools](http://developer.android.com/tools/sdk/eclipse-adt.html) (ADT). This is developer documentation, for user instructions on how to install please see the [project web site.](http://rgladwell.github.com/m2e-android/)

*Lead Maintainer*: [Ricardo Gladwell](http://gladwell.me)

Special thanks to our patrons who crowdfunded project development through Patreon:

 * [Raphael Ackermann](https://twitter.com/acraphae)
 * George Baker.
 * Weizhi Yao.
 * Stephen Buergler.

#Building

Before you start you need to:

 * Install the [Android SDK](http://developer.android.com/sdk/index.html).
 * Create `ANDROID_HOME` environment variable containing the Android SDK install path. 
 * Ensure you have installed the requried Android dependencies (execute `android update sdk --filter platform-tools,build-tools-21.1.1,android-18,addon-google_apis-google-18,android-10,addon-google_apis-google-10,android-8,addon-google_apis-google-8 --no-ui --force`).
 * Install the latest [Maven 3](http://maven.apache.org/download.html) for command line [Tycho](http://tycho.sonatype.org/) support.
 * Install the [Maven Android SDK Deployer](https://github.com/mosabua/maven-android-sdk-deployer) and deploy the 4.1 and 4.3 APIs (`mvn install -P 4.3,2.2`).

To build execute the following command from the project folder:

```
$ mvn install
```
 
###Eclipse Set-up

To configure your development environment please follow these steps:

 * Install [Eclipse Luna](http://eclipse.org/downloads).
 * Install [Eclipse Plug-in Development Environment.](http://www.eclipse.org/pde/).
 * Restart Eclipse.

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
 * Select the `m2e-android` folder you just cloned and select the parent and some of the sub-projects (excluding the targets i.e. `luna`, `mars`, etc., but *including* the `compile` target).
 * Click _Finish_.
 * This should install some additional m2e extensions if not already installed
 * Restart Eclipse when prompted.
 * In _Preferences_ -> _Plug-In Development_ -> _Target Platform_ choose `Maven for Android Eclipse` and click _OK_. It may take some time to downloads the dependencies required to compile m2e-android.

##Further Reading

For more information on developing the m2e-android plug-in please see the following:

* [Contributing](https://github.com/rgladwell/m2e-android/blob/master/CONTRIBUTING.md)
* [Deployment](https://github.com/rgladwell/m2e-android/wiki/Deploying)
* [Travis](https://github.com/rgladwell/m2e-android/wiki/Travis)
* [Maven Eclipse](https://www.eclipse.org/m2e/)
* [Maven Android Plugin](http://simpligility.github.io/android-maven-plugin)
