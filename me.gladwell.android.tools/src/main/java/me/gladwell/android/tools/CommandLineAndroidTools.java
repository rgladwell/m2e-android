package me.gladwell.android.tools;

import java.io.File;

import com.google.inject.Inject;

import me.gladwell.android.tools.drivers.DexCommand;
import me.gladwell.android.tools.drivers.DexdumpCommand;
import me.gladwell.android.tools.drivers.MavenCommandExecutor;
import me.gladwell.android.tools.model.DexInfo;

public class CommandLineAndroidTools implements DexService {

	private DexdumpOutputParser outputParser = new JAXBDexdumpOutputParser();

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

	public void convertClassFiles(Sdk sdk, File output, File... files) throws AndroidToolsException {
	    DexCommand command = new DexCommand();
	    command.setOutput(output);
	    command.setClassFiles(files);
	    if(sdk != null) {
	    	command.setSdk(sdk);
	    }

	    MavenCommandExecutor executor = new MavenCommandExecutor();
	    try {
			command.execute(executor);
		} catch (ExecutionException e) {
	        throw new AndroidToolsException("error executing dx command=["+command+"]", e);
		}		
	}

}
