package com.github.android.tools.drivers;

import com.github.android.tools.ExecutionException;

public interface Command {

	public void execute(CommandExecutor executor) throws ExecutionException;

}
