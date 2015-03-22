package me.gladwell.eclipse.m2e.android;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import com.google.inject.BindingAnnotation;

@Retention(RUNTIME)
@BindingAnnotation
public @interface MavenSource {

}
