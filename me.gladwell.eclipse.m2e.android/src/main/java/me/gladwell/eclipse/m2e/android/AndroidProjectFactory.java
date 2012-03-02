package me.gladwell.eclipse.m2e.android;

public interface AndroidProjectFactory<T, CONTEXT> {

	T createAndroidProject(CONTEXT target);

}
