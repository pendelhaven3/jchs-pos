package com.pj.magic;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class ApplicationProperties {

	private static final ResourceBundle properties = ResourceBundle.getBundle("application");
	
	public static String getProperty(String propertyName) {
		return properties.getString(propertyName);
	}

	public static boolean isServer() {
		return Files.exists(Paths.get("magic-lib", "httpserver"));
	}
	
}