package com.github.android.tools.drivers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;

public class DexdumpCommand extends AndroidCommand implements Command {

	File pathToDex;

	public void setPathToDex(File pathToDex) {
		this.pathToDex = pathToDex;
	}

	public void execute(CommandExecutor executor) throws ExecutionException {
		List<String> commands = new ArrayList<String>();
		commands.add("-d");
		commands.add(pathToDex.getAbsolutePath());
		try {
			executor.executeCommand(getAndroidSdk().getPathForTool("dexdump"), commands , false);
		} catch (MojoExecutionException e) {
			throw new ExecutionException(e);
		}
	}

}
