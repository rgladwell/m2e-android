package me.gladwell.eclipse.m2e.android.test;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class Matchers {

    private static final String ADT_NATURE = "com.android.ide.eclipse.adt.AndroidNature";
    private static final String ANDMORE_NATURE = "org.eclipse.andmore.AndroidNature";

    private Matchers() {}

    public static Matcher<IProject> hasAndroidNature() {
        return new BaseMatcher<IProject>() {
    
            @Override
            public boolean matches(Object target) {
                IProject project = (IProject) target;
                try {
                    return project.hasNature(ADT_NATURE) || project.hasNature(ANDMORE_NATURE);
                } catch (CoreException e) {
                    e.printStackTrace();
                    return false;
                }
            }
    
            @Override
            public void describeTo(Description d) {
                d.appendText("project with android nature");
            }
            
        };
    }

}
