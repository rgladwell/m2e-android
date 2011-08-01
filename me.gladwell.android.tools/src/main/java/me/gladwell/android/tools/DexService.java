package me.gladwell.android.tools;

import java.io.File;

import me.gladwell.android.tools.model.DexInfo;


public interface DexService {

	DexInfo getDexInfo(File dexfile) throws AndroidToolsException;
	void convertClassFiles(File output, File... files) throws AndroidToolsException;

}
