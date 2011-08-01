package me.gladwell.android.tools;

import java.io.File;
import java.util.List;

import me.gladwell.android.tools.drivers.MavenCommandExecutor;
import me.gladwell.android.tools.drivers.ResignArchiveCommand;
import me.gladwell.android.tools.drivers.UnpackCommand;
import me.gladwell.android.tools.drivers.VerifyArchiveCommand;
import me.gladwell.android.tools.model.Jdk;


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
