package com.github.android.tools;

import java.io.File;

import com.github.android.tools.drivers.DexCommand;
import com.github.android.tools.drivers.DexdumpCommand;
import com.github.android.tools.drivers.ExecutionException;
import com.github.android.tools.drivers.MavenCommandExecutor;
import com.github.android.tools.model.DexInfo;

public class CommandLineAndroidTools implements DexService {

	DexdumpOutputParser outputParser = new JAXBDexdumpOutputParser();

	public DexInfo getDexInfo(File dexfile) throws AndroidToolsException {
	    DexdumpCommand command = new DexdumpCommand();
	    command.setPathToDex(dexfile);
	    command.setOutputLayout(DexdumpCommand.OutputLayout.Xml);

	    MavenCommandExecutor executor = new MavenCommandExecutor();
	    try {
	        command.execute(executor);
        } catch (ExecutionException e) {
	        throw new AndroidToolsException("error executing dexdump command=["+command+"]", e);
        }

        DexInfo dexInfo = outputParser.parse(executor.getStandardOut());
        dexInfo.setSource(dexfile);
	    return dexInfo;
    }

	public void convertClassFiles(File output, File... files) throws AndroidToolsException {
	    DexCommand command = new DexCommand();
	    command.setOutput(output);
	    command.setClassFiles(files);
	    MavenCommandExecutor executor = new MavenCommandExecutor();
	    try {
			command.execute(executor);
		} catch (ExecutionException e) {
	        throw new AndroidToolsException("error executing dx command=["+command+"]", e);
		}		
	}

}
