/*******************************************************************************
 * Copyright (c) 2012 Tomas Prochazka
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.quickfix;

import java.io.File;
import java.io.IOException;

import me.gladwell.eclipse.m2e.android.AndroidMavenPlugin;
import me.gladwell.eclipse.m2e.android.project.Dependency;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IMarkerResolution;

/**
 * Quick fixed for missing apklib error marker.
 * 
 * @author Tomáš Procházka &lt;<a href="mailto:tomas.prochazka@gmail.com">tomas.prochazka@gmail.com</a>&gt;
 */
public class ImportApklibResolution implements IMarkerResolution {

	private String label;
	private Dependency missingApklibDependency;

	ImportApklibResolution(String label, Dependency missingApklibDependency) {
		this.label = label;
		this.missingApklibDependency = missingApklibDependency;
	}

	public String getLabel() {
		return label;
	}

	public void run(IMarker marker) {

		try {
			UnzipApkLibsDependciesSupport e = AndroidMavenPlugin.getDefault().getInjector().getInstance(UnzipApkLibsDependciesSupport.class);
			File targetFolder = e.getTargetPath();

			boolean r = MessageDialog.openQuestion(null,
				"Android Library Projects Importer",
				"Current version of ADT require to have all dependecies of apklib type imported in workspace.\n\n" +
					"This tool will download one or all of them to the " + targetFolder + " folder and will import them to the workspace.\n\n" +
					"This project will be used only in Eclipse, if you will make maven build, it will use apklibs directly from maven repository.\n\n" +
					"Do you want to continue?");

			if (r) {
				if (missingApklibDependency == null) {
					e.processAll(marker.getResource().getProject());
				} else {
					e.process(marker.getResource().getProject(), missingApklibDependency);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}
}