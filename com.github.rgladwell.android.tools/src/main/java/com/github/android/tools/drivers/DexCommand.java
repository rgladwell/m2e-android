package com.github.android.tools.drivers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;

import com.github.android.tools.ExecutionException;

public class DexCommand extends AndroidCommand implements Command {

	private static final String DEX_COMMAND = "dx";

	private static final String DEX_ARG = "--dex";
	private static final String OUTPUT_ARG = "--output";

	private File output;
	private File[] classFiles;

	public void setOutput(File output) {
		this.output = output;
	}

	public void setClassFiles(File[] classFiles) {
		this.classFiles = classFiles;
	}

	public void execute(CommandExecutor executor) throws ExecutionException {
		List<String> commands = new ArrayList<String>();
		
		commands.add(DEX_ARG);
		commands.add(OUTPUT_ARG + "=" + output.getAbsolutePath());

		for(File file : classFiles) {
			commands.add(file.getAbsolutePath());
		}

		try {
			executor.executeCommand(getAndroidSdk().getPathForTool(DEX_COMMAND), commands , false);
		} catch (MojoExecutionException e) {
			throw new ExecutionException(e);
		}

	}

}
