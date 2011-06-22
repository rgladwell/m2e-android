package com.github.android.tools;

import java.io.File;
import java.util.List;

import com.github.android.tools.drivers.ExecutionException;
import com.github.android.tools.drivers.UnpackCommand;

public class MavenAndroidPluginBuildService implements AndroidBuildService {

	public void unpack(File outputDirectory, File sourceDirectory, List<File> relevantCompileArtifacts, boolean lazy) throws ExecutionException {
		UnpackCommand command = new UnpackCommand();
		command.setOutputDirectory(outputDirectory);
		command.setSourceDirectory(sourceDirectory);
		command.setRelevantCompileArtifacts(relevantCompileArtifacts);
		command.setLazyLibraryUnpack(lazy);
		
		command.execute(null);
	}

}
