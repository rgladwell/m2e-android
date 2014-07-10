/*******************************************************************************
 * Copyright (c) 2011 Ricardo Gladwell
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.inject;

import static org.eclipse.core.runtime.ContributorFactoryOSGi.resolve;

import me.gladwell.eclipse.m2e.android.AndroidMavenPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Status;

public class GuiceExtensionFactory implements IExecutableExtension, IExecutableExtensionFactory {

    private String typeName;
    private IContributor contributor;

    public void setInitializationData(final IConfigurationElement configuration, final String name, final Object data)
            throws CoreException {
        contributor = configuration.getContributor();
        typeName = data instanceof String ? (String) data : configuration.getAttribute("id");
    }

    public Object create() throws CoreException {
        if (null == typeName) {
            throw new CoreException(new Status(IStatus.ERROR, contributor.getName(),
                    "Configuration is missing class information"));
        }

        Class<?> type = null;
        try {
            type = resolve(contributor).loadClass(typeName);
        } catch (final InvalidRegistryObjectException e) {
            throw new CoreException(new Status(IStatus.ERROR, contributor.getName(), "", e));
        } catch (final ClassNotFoundException e) {
            throw new CoreException(new Status(IStatus.ERROR, contributor.getName(), "", e));
        }

        final Object instance = AndroidMavenPlugin.getDefault().getInjector().getInstance(type);
        return instance;
    }

}
