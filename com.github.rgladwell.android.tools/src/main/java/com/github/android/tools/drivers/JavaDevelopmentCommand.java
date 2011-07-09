package com.github.android.tools.drivers;

public abstract class JavaDevelopmentCommand implements Command {

	private Jdk jdk;

	public Jdk getJdk() {
		return jdk;
	}

	public void setJdk(Jdk jdk) {
		this.jdk = jdk;
	}

}
