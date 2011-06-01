package com.github.android.tools.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

public class ClassDescriptor {

	String name;
	PackageInfo packageInfo;

	@XmlAttribute
	public String getName() {
    	return name;
    }

	public void setName(String name) {
    	this.name = name;
    }

	@XmlTransient
	public PackageInfo getPackageInfo() {
		return packageInfo;
	}

	public void setPackageInfo(PackageInfo packageInfo) {
		this.packageInfo = packageInfo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((packageInfo == null) ? 0 : packageInfo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClassDescriptor other = (ClassDescriptor) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (packageInfo == null) {
			if (other.packageInfo != null)
				return false;
		} else if (!packageInfo.equals(other.packageInfo))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ClassDescriptor [" + packageInfo.getName() + "." + name + "]";
	}

}
