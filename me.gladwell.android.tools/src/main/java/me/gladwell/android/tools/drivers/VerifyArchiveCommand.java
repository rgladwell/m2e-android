package me.gladwell.android.tools.drivers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.gladwell.android.tools.ExecutionException;



public class VerifyArchiveCommand extends ArchiveSignCommand implements Command {

	public void execute(CommandExecutor executor) throws ExecutionException {
		try {
			File jarsigner = getJdk().getJarSignerBinary();

			List<String> commands = new ArrayList<String>();
	
			commands.add("-verbose");
			commands.add("-verify");
			commands.add(getArchive().getAbsolutePath());
	
			executor.executeCommand("" + jarsigner.getAbsolutePath(), commands , false);
		} catch (IOException e) {
			throw new ExecutionException(e);
		}
	}

}
