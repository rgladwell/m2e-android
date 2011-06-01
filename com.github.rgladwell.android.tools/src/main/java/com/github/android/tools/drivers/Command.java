package com.github.android.tools.drivers;

public interface Command {

	public void execute(CommandExecutor executor) throws ExecutionException;

}
