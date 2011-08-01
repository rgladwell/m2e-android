package me.gladwell.android.tools.drivers;

import java.io.File;
import java.util.List;

import me.gladwell.android.tools.ExecutionException;


public interface CommandExecutor {

	public abstract void executeCommand(String executable, List<String> commands)
			throws ExecutionException;

	public abstract void executeCommand(String executable,
			List<String> commands, boolean failsOnErrorOutput)
			throws ExecutionException;

	public abstract void executeCommand(String executable,
			List<String> commands, File workingDirectory,
			boolean failsOnErrorOutput) throws ExecutionException;

	public abstract int getResult();

	public abstract String getStandardOut();

	public abstract String getStandardError();

}