/*******************************************************************************
 * Copyright (c) 2014 Ricardo Gladwell, Csaba Kozák
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.resolve;

import me.gladwell.eclipse.m2e.android.project.Dependency;

/*package*/ class HardCodedDependency implements Dependency {

    private final String name;
    private final String group;
    private final String version;

    public HardCodedDependency(String group, String name, String version) {
        this.group = group;
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public String getVersion() {
        return version;
    }

    public boolean isLibrary() {
        return false;
    }

    public String getPath() {
        return null;
    }

}
