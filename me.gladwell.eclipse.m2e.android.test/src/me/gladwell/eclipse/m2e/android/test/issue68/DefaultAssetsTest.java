package me.gladwell.eclipse.m2e.android.test.issue68;

import me.gladwell.eclipse.m2e.android.test.AndroidMavenPluginTestCase;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.junit.Assert;

public class DefaultAssetsTest extends AndroidMavenPluginTestCase {

	
	

	private static final String MULTIMODULE_ROOT = "projects/issue-68";
	private IProject project;
	private IProject rootProject;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        IProject[] projects = importAndroidProjects(MULTIMODULE_ROOT, new String[]{"pom.xml","android-defaultassets/pom.xml"} );
        
        rootProject = projects[0];
        project = projects[1];
    }

    public void testAssetLinkExists() throws Exception {
    	assertNoErrors(project);
    	IFolder assetsDir = project.getFolder("assets");
    	Assert.assertTrue(assetsDir.exists());
    	Assert.assertFalse(assetsDir.isLinked());
    	assertFileExists(project,"assets/defaultassets.data");
    }
    
   
    
    @Override
    protected void tearDown() throws Exception {
    	deleteAndroidProject(project);
        deleteAndroidProject(rootProject);

        project = null;
        rootProject = null;

        try {
            super.tearDown();
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }

}
