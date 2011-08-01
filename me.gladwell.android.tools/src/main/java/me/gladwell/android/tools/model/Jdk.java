package me.gladwell.android.tools.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Jdk {

	private File path;
	private File binariesPath;

	public File getPath() {
		return path;
	}

	public void setPath(File path) {
		this.path = path;
	}

	public File getBinariesPath() {
		if(binariesPath != null) {
			return binariesPath;
		}
		binariesPath = new File(path, "bin");
		return binariesPath;
	}

	public File getJarSignerBinary() throws IOException {
		for(String file : getBinariesPath().list()) {
			if(file.startsWith("jarsigner")) {
				return new File(getBinariesPath(), file);
			}
		}
		
		throw new FileNotFoundException("could not find jarsigner binary - check your path is pointing to correct location and not a JRE");
	}
}
