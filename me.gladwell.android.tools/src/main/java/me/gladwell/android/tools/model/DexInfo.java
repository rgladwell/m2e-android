package me.gladwell.android.tools.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "api")
public class DexInfo {

	File source;
	List<PackageInfo> packages;
	List<ClassDescriptor> classDescriptors = new ArrayList<ClassDescriptor>();

	@XmlTransient
	public File getSource() {
    	return source;
    }

	public void setSource(File source) {
    	this.source = source;
    }

	@XmlElements({
			@XmlElement(name="package")
	})
	public List<PackageInfo> getPackages() {
		return packages;
	}

	public void setPackages(List<PackageInfo> packages) {
		this.packages = packages;
	}

	@XmlTransient
	public List<ClassDescriptor> getClassDescriptors() {
		return classDescriptors;
	}

	public void setClassDescriptors(List<ClassDescriptor> classDescriptors) {
		this.classDescriptors = classDescriptors;
	}

}
