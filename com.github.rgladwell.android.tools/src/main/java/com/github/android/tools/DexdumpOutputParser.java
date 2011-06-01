package com.github.android.tools;

import com.github.android.tools.model.DexInfo;

public interface DexdumpOutputParser {

	DexInfo parse(String standardOut) throws AndroidToolsException;

}
