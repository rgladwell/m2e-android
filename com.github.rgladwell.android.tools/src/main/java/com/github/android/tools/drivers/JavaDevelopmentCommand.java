package com.github.android.tools.drivers;

import com.github.android.tools.model.Jdk;

public abstract class JavaDevelopmentCommand implements Command {

	private Jdk jdk;

	Jdk getJdk() {
		return jdk;
	}

	public void setJdk(Jdk jdk) {
		this.jdk = jdk;
	}

}
