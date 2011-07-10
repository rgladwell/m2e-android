package com.github.android.tools.drivers;

import java.io.File;


public abstract class ArchiveSignCommand extends JavaDevelopmentCommand {

	private File archive;

	File getArchive() {
		return archive;
	}

	public void setArchive(File archive) {
		this.archive = archive;
	}

}
