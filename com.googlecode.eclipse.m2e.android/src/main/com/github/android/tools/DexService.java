package com.github.android.tools;

import java.io.File;

public interface DexService {

	DexInfo getDexInfo(File dexfile) throws AndroidToolsException;

}
