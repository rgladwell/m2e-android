package com.github.android.tools.drivers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.codehaus.plexus.util.FileUtils;

import com.github.android.tools.ExecutionException;

// TODO add optional non-debug keystore implementation
public class ResignArchiveCommand extends ArchiveSignCommand implements Command {

	public void execute(CommandExecutor executor) throws ExecutionException {
		try {
			unsignApk();
		} catch (ZipException e) {
			throw new ExecutionException("error unsigning archive=["+getArchive()+"]", e);
		} catch (IOException e) {
			throw new ExecutionException("error unsigning archive=["+getArchive()+"]", e);
		}
		try {
			signApk(executor);
		} catch (IOException e) {
			throw new ExecutionException("error signing archive=["+getArchive()+"]", e);
		}
	}

	private void unsignApk() throws ZipException, IOException {
		// remove all META-INF* from archive
		File temp = null;
        ZipInputStream zin = null;
        ZipOutputStream zout = null;;
         
        try {
        	temp = File.createTempFile(getArchive().getName(), null);
            temp.delete();

            FileUtils.copyFile(getArchive(), temp);

        	zin = new ZipInputStream(new FileInputStream(temp));
            zout = new ZipOutputStream(new FileOutputStream(getArchive()));

            byte[] buf = new byte[1024];
	        ZipEntry entry = zin.getNextEntry();
	        while (entry != null) {
	            String name = entry.getName();
	            if (!name.startsWith("META-INF")) {
	                // Add ZIP entry to output stream.
	                zout.putNextEntry(new ZipEntry(name));
	                // Transfer bytes from the ZIP file to the output file
	                int len;
	                while ((len = zin.read(buf)) > 0) {
	                    zout.write(buf, 0, len);
	                }
	            }
	            entry = zin.getNextEntry();
	        }
        } finally {   
	        if(zin != null) {
	        	zin.close();
	        }
	        if(zout != null) {
	        	zout.close();
	        }
	        if(temp != null) {
	        	temp.delete();
	        }
        }
    }

	private void signApk(CommandExecutor executor) throws IOException, ExecutionException {
		File jarsigner = getJdk().getJarSignerBinary();
		List<String> commands = new ArrayList<String>();

		commands.add("-verbose");
		commands.add("-keystore");
		commands.add(new File(System.getProperty("user.home"), ".android" + File.separator + "debug.keystore").getAbsolutePath());
		commands.add("-storepass");
		commands.add("android");
		commands.add(getArchive().getAbsolutePath());
		commands.add("androiddebugkey");

		executor.executeCommand("" + jarsigner.getAbsolutePath(), commands , false);
	}

}
