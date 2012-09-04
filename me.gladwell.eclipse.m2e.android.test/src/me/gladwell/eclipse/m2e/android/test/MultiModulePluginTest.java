package me.gladwell.eclipse.m2e.android.test;

import java.io.File;

import org.eclipse.core.resources.IProject;

import com.android.ide.eclipse.adt.AdtConstants;

public class MultiModulePluginTest extends AndroidMavenPluginTestCase {

    private static final String PARENT_PROJECT_NAME = "android-multi-module";
    private static final String CHILD_PROJECT_NAME = "android-child";

    private IProject parentProject;
    private IProject childProject;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        parentProject = importAndroidProject(PARENT_PROJECT_NAME);
        childProject = importAndroidProject(PARENT_PROJECT_NAME + File.separator + CHILD_PROJECT_NAME);
    }

    public void testConfigure() throws Exception {
        assertNoErrors(childProject);
    }

    public void testConfigureAddsAndroidNature() throws Exception {
        assertTrue("failed to add android nature to child module", childProject.hasNature(AdtConstants.NATURE_DEFAULT));
    }

    @Override
    protected void tearDown() throws Exception {
        deleteAndroidProject(PARENT_PROJECT_NAME);

        parentProject = null;

        try {
            super.tearDown();
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }

}
