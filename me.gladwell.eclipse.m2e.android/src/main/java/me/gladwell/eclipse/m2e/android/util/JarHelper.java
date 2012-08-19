package me.gladwell.eclipse.m2e.android.util;

import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Helper class to deal with jar files.
 * 
 * @author Johan Lindquist
 * @author Tomas Prochazka
 */
public class JarHelper
{

	/**
	 * Listener for jar extraction.
	 */
	public interface UnjarListener {
		boolean include(JarEntry jarEntry);
	}

	/**
	 * Unjars the specified jar file into the the specified directory
	 * 
	 * @param jarFile
	 * @param outputDirectory
	 * @param unjarListener
	 * @throws IOException
	 */
	public static void unjar(JarFile jarFile, File outputDirectory, UnjarListener unjarListener) throws IOException {
		for (Enumeration en = jarFile.entries(); en.hasMoreElements();) {
			JarEntry entry = (JarEntry) en.nextElement();
			File entryFile = new File(outputDirectory, entry.getName());
			if (unjarListener == null || unjarListener.include(entry)) {
				// Create the output directory if need be
				if (!entryFile.getParentFile().exists()) {
					if (!entryFile.getParentFile().mkdirs()) {
						throw new IOException("Error creating output directory: " + entryFile.getParentFile());
					}
				}

				// If the entry is an actual file, unzip that too
				if (!entry.isDirectory()) {
					final InputStream in = jarFile.getInputStream(entry);
					try {
						final OutputStream out = new FileOutputStream(entryFile);
						try {
							IOUtil.copy(in, out);
						} finally {
							IOUtil.close(out);
						}
					} finally {
						IOUtil.close(in);
					}
				}
			}
		}
	}

}
