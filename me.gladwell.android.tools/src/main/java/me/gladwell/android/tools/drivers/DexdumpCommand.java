package me.gladwell.android.tools.drivers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.gladwell.android.tools.ExecutionException;

import org.apache.maven.plugin.MojoExecutionException;

public class DexdumpCommand extends AndroidCommand implements Command {

	private static final String DEXDUMP_COMMAND = "dexdump";
	private static final String DISASSEMBLE_ARG = "-d";
	private static final String OUTPUT_LAYOUT_ARG = "-l";

	public enum OutputLayout {
		Plain("plain"),
		Xml("xml");
		
		private final String name;
		
		OutputLayout(String name) {
			this.name = name;
		}
		
		public String toString() {
			return name;
		}
	}

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
		
		commands.add(DISASSEMBLE_ARG);
		if(outputLayout.equals(OutputLayout.Xml)) {
			commands.add(OUTPUT_LAYOUT_ARG);
			commands.add(outputLayout.toString());
		}
		commands.add(pathToDex.getAbsolutePath());

		try {
			executor.executeCommand(getAndroidSdk().getPathForTool(DEXDUMP_COMMAND), commands , false);
		} catch (MojoExecutionException e) {
			throw new ExecutionException(e);
		}
	}

}
