package com.github.android.tools;

import java.io.File;
import java.util.List;

public class DexInfo {

	File source;
	List<ClassDescriptor> classDescriptors;

	public File getSource() {
    	return source;
    }

	public void setSource(File source) {
    	this.source = source;
    }

	public List<ClassDescriptor> getClassDescriptors() {
    	return classDescriptors;
    }

	public void setClassDescriptors(List<ClassDescriptor> classDescriptors) {
    	this.classDescriptors = classDescriptors;
    }

}
