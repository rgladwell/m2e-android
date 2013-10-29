package me.gladwell.eclipse.m2e.android.configuration.workspace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.gladwell.eclipse.m2e.android.project.AdtEclipseAndroidWorkspace;
import me.gladwell.eclipse.m2e.android.project.AndroidWorkspace;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class WorkspaceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(LibraryDependenciesWorkspaceConfigurer.class);
    }

    @Provides
    List<WorkspaceConfigurer> provideWorkspaceConfigurers(LibraryDependenciesWorkspaceConfigurer configurer) {
        final List<WorkspaceConfigurer> workspaceConfigurers = new ArrayList<WorkspaceConfigurer>();

        workspaceConfigurers.add(new FixerWorkspaceConfigurer());
        workspaceConfigurers.add(new AddAndroidNatureWorkspaceConfigurer());
        workspaceConfigurers.add(new OrderBuildersWorkspaceConfigurer());
        workspaceConfigurers.add(new ConvertLibraryWorkspaceConfigurer());
        workspaceConfigurers.add(new ConvertLibraryWorkspaceConfigurer());
        workspaceConfigurers.add(configurer);
        workspaceConfigurers.add(new LinkAssetsFolderConfigurer());

        return Collections.unmodifiableList(workspaceConfigurers);
    }

}
