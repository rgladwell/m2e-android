package com.github.android.tools.drivers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;

public class DexdumpCommand extends AndroidCommand implements Command {

	public enum OutputLayout { Plain, Xml }

	private File pathToDex;
	private OutputLayout outputLayout = OutputLayout.Plain;

	public void setPathToDex(File pathToDex) {
		this.pathToDex = pathToDex;
	}

	public void setOutputLayout(OutputLayout outputLayout) {
		this.outputLayout = outputLayout;
	}

	public void execute(CommandExecutor executor) throws ExecutionException {
		List<String> commands = new ArrayList<String>();
		
		commands.add("-d");
		commands.add(pathToDex.getAbsolutePath());

		if(outputLayout.equals(OutputLayout.Xml)) {
			commands.add("-l");
			commands.add("xml");
		}

		try {
			executor.executeCommand(getAndroidSdk().getPathForTool("dexdump"), commands , false);
		} catch (MojoExecutionException e) {
			throw new ExecutionException(e);
		}
	}

}
