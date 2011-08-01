package me.gladwell.android.tools;

import me.gladwell.android.tools.model.DexInfo;

public interface DexdumpOutputParser {

	DexInfo parse(String standardOut) throws AndroidToolsException;

}
