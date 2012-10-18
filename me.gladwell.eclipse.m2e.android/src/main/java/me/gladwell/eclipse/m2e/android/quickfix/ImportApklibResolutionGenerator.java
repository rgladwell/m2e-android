/*******************************************************************************
 * Copyright (c) 2012 Tomas Prochazka
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android.quickfix;

import me.gladwell.eclipse.m2e.android.AndroidMavenPlugin;
import me.gladwell.eclipse.m2e.android.project.Dependency;
import me.gladwell.eclipse.m2e.android.project.MavenDependency;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;

/**
 * Crete QuickFix feature on apklib missing marker. 
 * 
 * @author Tomáš Procházka &lt;<a href="mailto:tomas.prochazka@gmail.com">tomas.prochazka@gmail.com</a>&gt;
 */
public class ImportApklibResolutionGenerator implements IMarkerResolutionGenerator {
	public IMarkerResolution[] getResolutions(IMarker mk) {
		try {
			if (mk.getType().equals(AndroidMavenPlugin.APKLIB_ERROR_TYPE)) {
				Dependency missingApklibDependency = new MavenDependency(
						(String)mk.getAttribute("group"),
						(String)mk.getAttribute("name"),
						(String)mk.getAttribute("type"),
						(String)mk.getAttribute("version")
					);
				
				return new IMarkerResolution[] {
					new ImportApklibResolution("Create project from all missing apklibs", null),
					new ImportApklibResolution("Create project from [" + missingApklibDependency + "]", missingApklibDependency),
				};
				
			} else {
				return new IMarkerResolution[0];
			}
		} catch (CoreException e) {
			return new IMarkerResolution[0];
		}
	}
}