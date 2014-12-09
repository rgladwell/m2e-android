package me.gladwell.eclipse.m2e.android.test;

import static java.io.File.separator;
import static org.eclipse.m2e.core.project.MavenProjectInfo.RENAME_NO;
import static org.eclipse.m2e.core.project.MavenProjectInfo.RENAME_REQUIRED;
import static org.eclipse.m2e.tests.common.FileHelpers.copyDir;
import static org.eclipse.m2e.tests.common.WorkspaceHelpers.findErrorMarkers;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.MavenModelManager;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectImportResult;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.MavenProjectInfo;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.m2e.tests.common.WorkspaceHelpers;

public class ProjectImporter {

    private final static IProjectConfigurationManager configurationManager = MavenPlugin.getProjectConfigurationManager();
    private final static MavenModelManager mavenModelManager = MavenPlugin.getMavenModelManager();

    private final File sourcePom;
    private File destination;
    private ResolverConfiguration configuration = new ResolverConfiguration();

    public ProjectImporter(File sourcePom) {
        this.sourcePom = sourcePom;
        this.destination = new File(sourcePom.getParentFile().getName());
    }

    public static ProjectImporter importAndroidTestProject(String name) {
        return new ProjectImporter(new File("projects" + separator + name + separator + "pom.xml"));
    }

    public ProjectImporter withProjectFolder(File file) {
        this.destination = file;
        return this;
    }

    public IProject into(IWorkspace workspace) throws Exception {
        File source = new File(sourcePom.getParentFile().getCanonicalPath());
        return importProject(workspace, source);
    }

    // TODO too big: re-factor method by extracting code into sub-methods and classes
    private IProject importProject(IWorkspace workspace, File source) throws IOException, CoreException {
        File workspaceRoot = workspace.getRoot().getLocation().toFile();
        File projectDestination = new File(workspaceRoot, destination.getName());
        
        copyDir(source, projectDestination);

        String pomName = sourcePom.getName();
        File targetPom = new File(destination, pomName);
        Model model = mavenModelManager.readMavenModel(targetPom);
        MavenProjectInfo projectInfo = new MavenProjectInfo(pomName, targetPom, model, null);
        File basedir = projectInfo.getPomFile().getParentFile().getCanonicalFile();

        projectInfo.setBasedirRename(basedir.getParentFile().equals(workspaceRoot) ? RENAME_REQUIRED : RENAME_NO);

        final ArrayList<MavenProjectInfo> projectInfos = new ArrayList<MavenProjectInfo>();
        projectInfos.add(projectInfo);

        final ProjectImportConfiguration importConfiguration = new ProjectImportConfiguration(configuration);

        final ArrayList<IMavenProjectImportResult> importResults = new ArrayList<IMavenProjectImportResult>();

        projectInfo.getPomFile().getParentFile().getAbsolutePath();

        workspace.run(new IWorkspaceRunnable() {
            public void run(IProgressMonitor monitor) throws CoreException {
                importResults.addAll(configurationManager.importProjects(projectInfos,
                        importConfiguration, monitor));
            }
        }, MavenPlugin.getProjectConfigurationManager().getRule(), IWorkspace.AVOID_UPDATE, new NullProgressMonitor());

        IProject[] projects = new IProject[projectInfos.size()];
        for (int i = 0; i < projectInfos.size(); i++) {
            IMavenProjectImportResult importResult = importResults.get(i);
            projects[i] = importResult.getProject();
            assertNotNull("Failed to import project " + projectInfos, projects[i]);

            Model model1 = projectInfos.get(0).getModel();
            IMavenProjectFacade facade = MavenPlugin.getMavenProjectRegistry().create(projects[i], new NullProgressMonitor());
            if (facade == null) {
                fail("Project " + model1.getGroupId() + "-" + model1.getArtifactId() + "-" + model1.getVersion()
                        + " was not imported. Errors: "
                        + WorkspaceHelpers.toString(findErrorMarkers(projects[i])));
            }
        }

        return projects[0];
    }
}
