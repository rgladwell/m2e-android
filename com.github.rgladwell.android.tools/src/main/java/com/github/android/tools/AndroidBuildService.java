package com.github.android.tools;

import java.io.File;
import java.util.List;

import com.github.android.tools.drivers.ExecutionException;

public interface AndroidBuildService {

	public void unpack(File outputDirectory, File sourceDirectory, List<File> relevantCompileArtifacts, boolean lazy) throws ExecutionException;

}
