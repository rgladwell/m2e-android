/*
 * Copyright (C) 2009 Jayway AB
 * Copyright (C) 2007-2008 JVending Masa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.gladwell.android.tools.drivers;

import static org.codehaus.plexus.util.StringUtils.isBlank;

import java.io.File;

import me.gladwell.android.tools.Sdk;

import org.apache.maven.plugin.MojoExecutionException;

public abstract class AndroidCommand implements Command {

    /**
     * <p>The Android SDK to use.</p>
     * <p>Looks like this:</p>
     * <pre>
     * &lt;sdk&gt;
     *     &lt;path&gt;/opt/android-sdk-linux&lt;/path&gt;
     *     &lt;platform&gt;2.1&lt;/platform&gt;
     * &lt;/sdk&gt;
     * </pre>
     * <p>The <code>&lt;platform&gt;</code> parameter is optional, and corresponds to the
     * <code>platforms/android-*</code> directories in the Android SDK directory. Default is the latest available
     * version, so you only need to set it if you for example want to use platform 1.5 but also have e.g. 2.2 installed.
     * Has no effect when used on an Android SDK 1.1. The parameter can also be coded as the API level. Therefore valid values are
     * 1.1, 1.5, 1.6, 2.0, 2.01, 2.1, 2,2 as well as 3, 4, 5, 6, 7, 8. If a platform/api level is not installed on the
     * machine an error message will be produced. </p>
     * <p>The <code>&lt;path&gt;</code> parameter is optional. The default is the setting of the ANDROID_HOME environment
     * variable. The parameter can be used to override this setting with a different environment variable like this:</p>
     * <pre>
     * &lt;sdk&gt;
     *     &lt;path&gt;${env.ANDROID_SDK}&lt;/path&gt;
     * &lt;/sdk&gt;
     * </pre>
     * <p>or just with a hardcoded absolute path. The parameters can also be configured from command-line with parameters
     * <code>-Dandroid.sdk.path</code> and <code>-Dandroid.sdk.platform</code>.</p>
     *
     * @parameter
     */
    private Sdk sdk;

    /**
     * <p>Parameter designed to pick up <code>-Dandroid.sdk.path</code> in case there is no pom with an
     * <code>&lt;sdk&gt;</code> configuration tag.</p>
     * <p>Corresponds to {@link Sdk#path}.</p>
     *
     * @parameter expression="${android.sdk.path}"
     * @readonly
     */
    private File sdkPath;

    /**
     * <p>Parameter designed to pick up <code>-Dandroid.sdk.platform</code> in case there is no pom with an
     * <code>&lt;sdk&gt;</code> configuration tag.</p>
     * <p>Corresponds to {@link Sdk#platform}.</p>
     *
     * @parameter expression="${android.sdk.platform}"
     * @readonly
     */
    private String sdkPlatform;

	public void setSdk(Sdk sdk) {
		this.sdk = sdk;
		this.sdkPath = sdk.getPath();
	}

	protected AndroidSdk getAndroidSdk() throws MojoExecutionException {
        File chosenSdkPath;
        String chosenSdkPlatform;

        if (sdk != null) {
            // An <sdk> tag exists in the pom.

            if (sdk.getPath() != null) {
                // An <sdk><path> tag is set in the pom.

                chosenSdkPath = sdk.getPath();
            } else {
                // There is no <sdk><path> tag in the pom.

                if (sdkPath != null) {
                    // -Dandroid.sdk.path is set on command line, or via <properties><sdk.path>...
                    chosenSdkPath = sdkPath;
                } else {
                    // No -Dandroid.sdk.path is set on command line, or via <properties><sdk.path>...
                    chosenSdkPath = new File(getAndroidHomeOrThrow());
                }
            }

            // Use <sdk><platform> from pom if it's there, otherwise try -Dandroid.sdk.platform from command line or <properties><sdk.platform>...
            if (!isBlank(sdk.getPlatform())) {
                chosenSdkPlatform = sdk.getPlatform();
            } else {
                chosenSdkPlatform = sdkPlatform;
            }
        } else {
            // There is no <sdk> tag in the pom.

            if (sdkPath != null) {
                // -Dandroid.sdk.path is set on command line, or via <properties><sdk.path>...
                chosenSdkPath = sdkPath;
            } else {
                // No -Dandroid.sdk.path is set on command line, or via <properties><sdk.path>...
                chosenSdkPath = new File(getAndroidHomeOrThrow());
            }

            // Use any -Dandroid.sdk.platform from command line or <properties><sdk.platform>...
            chosenSdkPlatform = sdkPlatform;
        }

        return new AndroidSdk(chosenSdkPath, chosenSdkPlatform);
    }

    private String getAndroidHomeOrThrow() throws MojoExecutionException {
        final String androidHome = System.getenv(AndroidSdk.ENV_ANDROID_HOME);
        if (isBlank(androidHome)) {
            throw new MojoExecutionException("No Android SDK path could be found. You may configure it in the pom using <sdk><path>...</path></sdk> or <properties><sdk.path>...</sdk.path></properties> or on command-line using -Dandroid.sdk.path=... or by setting environment variable " + AndroidSdk.ENV_ANDROID_HOME);
        }
        return androidHome;
    }

}