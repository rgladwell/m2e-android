package me.gladwell.android.tools.drivers;

import me.gladwell.android.tools.model.Jdk;

public abstract class JavaDevelopmentCommand implements Command {

	private Jdk jdk;

	Jdk getJdk() {
		return jdk;
	}

	public void setJdk(Jdk jdk) {
		this.jdk = jdk;
	}

}
