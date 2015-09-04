package com.tensorwrench.jackson.hal.annotations;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@JacksonAnnotationsInside
public @interface HalResource {
	public static final String DEFAULT_URL_FORMAT="{classname.lcase}/{id}";
	public static final String DEFAULT_LINK_PREFIX_FORMAT="{prefix}/{id}";
	public static final String DEFAULT_ID_REGEX=".*/([^?#]+).*";
	
	String urlFormat() default DEFAULT_URL_FORMAT;
	String idRegex() default DEFAULT_ID_REGEX; // after the last slash, but before any query params
}
