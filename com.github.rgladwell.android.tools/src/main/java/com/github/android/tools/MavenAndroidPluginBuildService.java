package com.github.android.tools;

import java.io.File;
import java.util.List;

import com.github.android.tools.drivers.MavenCommandExecutor;
import com.github.android.tools.drivers.ResignArchiveCommand;
import com.github.android.tools.drivers.UnpackCommand;
import com.github.android.tools.drivers.VerifyArchiveCommand;
import com.github.android.tools.model.Jdk;

public class MavenAndroidPluginBuildService implements AndroidBuildService {

	private Jdk jdk;

	public void setJdk(Jdk jdk) {
		this.jdk = jdk;
	}

	public void unpack(File outputDirectory, File sourceDirectory, List<File> relevantCompileArtifacts, boolean lazy) throws ExecutionException {
		UnpackCommand command = new UnpackCommand();
		command.setOutputDirectory(outputDirectory);
		command.setSourceDirectory(sourceDirectory);
		command.setRelevantCompileArtifacts(relevantCompileArtifacts);
		command.setLazyLibraryUnpack(lazy);

		command.execute(null);
	}

	public void resign(File apk) throws ExecutionException {
		ResignArchiveCommand command = new ResignArchiveCommand();
		command.setArchive(apk);
		command.setJdk(jdk);

	    MavenCommandExecutor executor = new MavenCommandExecutor();
		command.execute(executor);
	}

	public void verify(File apk) throws ExecutionException {
		VerifyArchiveCommand command = new VerifyArchiveCommand();
		command.setArchive(apk);
		command.setJdk(jdk);

	    MavenCommandExecutor executor = new MavenCommandExecutor();
		command.execute(executor);
		
	}
}
