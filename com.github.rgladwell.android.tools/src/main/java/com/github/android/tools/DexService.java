package com.github.android.tools;

import java.io.File;

import com.github.android.tools.model.DexInfo;

public interface DexService {

	DexInfo getDexInfo(File dexfile) throws AndroidToolsException;
	void convertClassFiles(File output, File... files) throws AndroidToolsException;

}
