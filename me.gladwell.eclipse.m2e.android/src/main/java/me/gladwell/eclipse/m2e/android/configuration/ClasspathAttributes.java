/*******************************************************************************
 * Copyright (c) 2015 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.configuration;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IClasspathAttribute;

class ClasspathAttributes {

    private final List<IClasspathAttribute> attributes;

    public ClasspathAttributes(IClasspathAttribute[] attributes) {
        this.attributes = new ArrayList<IClasspathAttribute>(asList(attributes));
    }

    public boolean hasAttribute(String name) {
        for(IClasspathAttribute attribute: attributes) {
            if(attribute.getName().equals(name)) return true;
        }
        return false;
    }

    public void set(IClasspathAttribute toset) {
        int index = 0;
        for(IClasspathAttribute attribute : attributes) {
            if(attribute.getName().equals(toset.getName())) {
                attributes.set(index, toset);
                return;
            }
            index ++;
        }

        attributes.add(toset);
    }

    public IClasspathAttribute[] toArray() {
        return attributes.toArray(new IClasspathAttribute[attributes.size()]);
    }

}