package com.github.android.tools.drivers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

public class UnpackCommand implements Command {

	private File sourceDirectory;
	private File outputDirectory;
	private List<File> relevantCompileArtifacts;
	private boolean lazyLibraryUnpack = false;

	public void setSourceDirectory(File sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}

	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public void setRelevantCompileArtifacts(List<File> relevantCompileArtifacts) {
		this.relevantCompileArtifacts = relevantCompileArtifacts;
	}

	public void setLazyLibraryUnpack(boolean lazyLibraryUnpack) {
		this.lazyLibraryUnpack = lazyLibraryUnpack;
	}

	public void execute(CommandExecutor executor) throws ExecutionException {
		if (lazyLibraryUnpack && outputDirectory.exists()) {
			// getLog().info(
			// "skip library unpacking due to lazyLibraryUnpack policy");
		} else {
			for (File artifact : relevantCompileArtifacts) {
				if (artifact.isDirectory()) {
					try {
						FileUtils.copyDirectoryStructure(artifact, outputDirectory);
					} catch (IOException e) {
						throw new ExecutionException("IOException while copying "
										+ artifact.getAbsolutePath() + " into "
										+ outputDirectory.getAbsolutePath(), e);
					}
				} else {
					try {
						unjar(new JarFile(artifact), outputDirectory);
					} catch (IOException e) {
						throw new ExecutionException(
								"IOException while unjarring "
										+ artifact.getAbsolutePath() + " into "
										+ outputDirectory.getAbsolutePath(), e);
					}
				}

			}
		}

		try {
			FileUtils.copyDirectoryStructure(sourceDirectory, outputDirectory);
		} catch (IOException e) {
			throw new ExecutionException("IOException while copying "
					+ sourceDirectory.getAbsolutePath() + " into "
					+ outputDirectory.getAbsolutePath(), e);
		}
		
		deleteNonClassFiles();
	}

	private void unjar(JarFile jarFile, File outputDirectory) throws IOException {
		for (Enumeration<JarEntry> en = jarFile.entries(); en.hasMoreElements();) {
			JarEntry entry = (JarEntry) en.nextElement();
			File entryFile = new File(outputDirectory, entry.getName());
			if (!entryFile.getParentFile().exists() && !entry.getName().startsWith("META-INF")) {
				entryFile.getParentFile().mkdirs();
			}
			if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
				final InputStream in = jarFile.getInputStream(entry);
				try {
					final OutputStream out = new FileOutputStream(entryFile);
					try {
						IOUtil.copy(in, out);
					} finally {
						if(out != null) {
							out.close();
						}
					}
				} finally {
					if(in != null) {
						in.close();
					}
				}
			}
		}
	}

	private void deleteNonClassFiles() throws ExecutionException {
		try {
			for(Object o : FileUtils.getFiles(outputDirectory, "**/**", "**/*.class")) {
				File file = (File) o;
				file.delete();
			}
		} catch (IOException e) {
			throw new ExecutionException("IOException while deleting non-class files from "
					+ outputDirectory.getAbsolutePath(), e);
		}
	}

}
