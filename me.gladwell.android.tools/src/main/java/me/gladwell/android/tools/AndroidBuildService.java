package me.gladwell.android.tools;

import java.io.File;
import java.util.List;

import me.gladwell.android.tools.model.Jdk;


public interface AndroidBuildService {

	public void setJdk(Jdk jdk);
	public void unpack(File outputDirectory, File sourceDirectory, List<File> relevantCompileArtifacts, boolean lazy) throws ExecutionException;
	public void resign(File apk) throws ExecutionException;
	public void verify(File apk) throws ExecutionException;

}
