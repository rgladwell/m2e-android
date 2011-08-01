package me.gladwell.android.tools.drivers;

import me.gladwell.android.tools.ExecutionException;

public interface Command {

	public void execute(CommandExecutor executor) throws ExecutionException;

}
