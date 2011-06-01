package com.github.android.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.android.tools.drivers.DexdumpCommand;
import com.github.android.tools.drivers.ExecutionException;
import com.github.android.tools.drivers.MavenCommandExecutor;

public class CommandLineAndroidTools implements DexService {

	public DexInfo getDexInfo(File dexfile) throws AndroidToolsException {
	    DexdumpCommand command = new DexdumpCommand();
	    command.setPathToDex(dexfile);

	    MavenCommandExecutor executor = new MavenCommandExecutor();
	    try {
	        command.execute(executor);
        } catch (ExecutionException e) {
	        throw new AndroidToolsException(e);
        }

        Pattern p = Pattern.compile("Class descriptor\\w*\\:\\w*\\\'(.*)\\\'");
        String output = executor.getStandardOut();
        Matcher m = p.matcher(output);

        List<ClassDescriptor> classDescriptors = new ArrayList<ClassDescriptor>();

        while(m.find()) {
        	String type = m.group(1);
        	ClassDescriptor classDescriptor = new ClassDescriptor();
        	classDescriptor.setType(type);
        	classDescriptors.add(classDescriptor);
        }

        DexInfo dexInfo = new DexInfo();
        dexInfo.setSource(dexfile);
        dexInfo.setClassDescriptors(classDescriptors);
	    return dexInfo;
    }

}
