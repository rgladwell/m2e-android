package me.gladwell.eclipse.m2e.android.configuration.workspace;

import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;
import me.gladwell.eclipse.m2e.android.project.Sdk;

import com.android.sdklib.AndroidTargetHash;
import com.android.sdklib.AndroidVersion;
import com.android.sdklib.AndroidVersion.AndroidVersionException;
import com.android.sdklib.internal.project.ProjectProperties;
import com.android.sdklib.internal.project.ProjectProperties.PropertyType;

public class AddProjectPropertiesFileConfigurer implements WorkspaceConfigurer {

    @SuppressWarnings("deprecation")
    public boolean isConfigured(EclipseAndroidProject eclipseProject, MavenAndroidProject mavenProject) {
        return hasTargetInPropertyFile(eclipseProject, mavenProject, PropertyType.LEGACY_DEFAULT)
                || hasTargetInPropertyFile(eclipseProject, mavenProject, PropertyType.PROJECT);
    }

    public boolean isValid(MavenAndroidProject project) {
        Sdk sdk = project.getAndroidSdk();
        if (sdk != null) {
            return getTargetPropertyString(sdk.getPlatform()) != null;
        }
        return false;
    }

    public void configure(EclipseAndroidProject eclipseProject, MavenAndroidProject mavenProject) {
        String hashString = getTargetPropertyString(mavenProject.getAndroidSdk().getPlatform());
        eclipseProject.setPlatform(hashString);
    }

    private static boolean hasTargetInPropertyFile(EclipseAndroidProject eclipseProject,
            MavenAndroidProject mavenProject, PropertyType type) {
        ProjectProperties projectProperties = ProjectProperties.load(eclipseProject.getProject().getLocation()
                .toOSString(), type);
        if (projectProperties == null) {
            return false;
        }
        return projectProperties.getProperty(ProjectProperties.PROPERTY_TARGET).equals(
                getTargetPropertyString(mavenProject.getAndroidSdk().getPlatform()));
    }

    private static String getTargetPropertyString(String target) {
        try {
            AndroidVersion androidVersion = new AndroidVersion(target);
            return AndroidTargetHash.getPlatformHashString(androidVersion);
        } catch (AndroidVersionException e) {
            return null;
        }
    }

}
