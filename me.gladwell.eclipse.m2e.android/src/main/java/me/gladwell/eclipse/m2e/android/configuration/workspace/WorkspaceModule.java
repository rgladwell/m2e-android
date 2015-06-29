package me.gladwell.eclipse.m2e.android.configuration.workspace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import me.gladwell.eclipse.m2e.android.AndroidMavenProjectConfigurator;

public class WorkspaceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(LibraryDependenciesWorkspaceConfigurer.class);
        bind(MarkAndroidClasspathContainerNotExportedConfigurer.class);
    }

    @Provides
    List<WorkspaceConfigurer> provideWorkspaceConfigurers(LibraryDependenciesWorkspaceConfigurer libraryDependenciesConfigurer, 
            MarkAndroidClasspathContainerNotExportedConfigurer markAndroidClasspathContainerNotExportedConfigurer) {
        final List<WorkspaceConfigurer> workspaceConfigurers = new ArrayList<WorkspaceConfigurer>();

        workspaceConfigurers.add(new FixerWorkspaceConfigurer());
        if (AndroidMavenProjectConfigurator.usesM2E1_6OrNewer()) {
            workspaceConfigurers.add(markAndroidClasspathContainerNotExportedConfigurer);
        }
        workspaceConfigurers.add(new AddAndroidNatureWorkspaceConfigurer());
        workspaceConfigurers.add(new OrderBuildersWorkspaceConfigurer());
        workspaceConfigurers.add(new ConvertLibraryWorkspaceConfigurer());
        workspaceConfigurers.add(new ConvertLibraryWorkspaceConfigurer());
        workspaceConfigurers.add(libraryDependenciesConfigurer);
        workspaceConfigurers.add(new LinkAssetsFolderConfigurer());
        workspaceConfigurers.add(new LinkAndroidManifestConfigurer());
        workspaceConfigurers.add(new LinkResourceDirectoryConfigurer());

        return Collections.unmodifiableList(workspaceConfigurers);
    }

}
