package me.gladwell.eclipse.m2e.android.test.issue68;

import me.gladwell.eclipse.m2e.android.test.AndroidMavenPluginTestCase;

import org.eclipse.core.resources.IProject;

public class OutsideProjectClasspathTest extends AndroidMavenPluginTestCase {

	
	

	private static final String MULTIMODULE_ROOT = "projects/issue-68";
	private IProject project;
	private IProject rootProject;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

//        IProject[] projects = importAndroidProjects(PROJECT_ROOT_FOLDER, new String[]{"pom.xml","android-app-parentassets/pom.xml"} );
        IProject[] projects = importAndroidProjects(MULTIMODULE_ROOT, new String[]{"pom.xml","android-relativeoutside/pom.xml"} );
        
        
        rootProject = projects[0];
        project = projects[1];
    }

    public void testAssetLinkExists() throws Exception {
    	assertNoErrors(project);
    	assertLinkedFolderExists(project, "assets");
    	assertFileExists(project,"assets/outsideassets.data");
    }
    
   
    
    @Override
    protected void tearDown() throws Exception {
        deleteAndroidProject(rootProject);
        deleteAndroidProject(project);

        project = null;
        rootProject = null;

        try {
            super.tearDown();
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }

}
