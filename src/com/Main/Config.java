package com.Main;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

	private static String getApiKey() throws IOException {
		Properties properties = new Properties();

		// Use try-with-resources to ensure the FileInputStream is closed automatically
		try (FileInputStream fileInput = new FileInputStream("config.properties")) {
			properties.load(fileInput);
		}

		return properties.getProperty("apiKey");
	}

	public static String getSafeApiKey() {
		String apiKey;
		try {
			apiKey = getApiKey();
		} catch (IOException e) {
			// Handle the specific case of missing or unreadable config file
			throw new RuntimeException("Failed to load API key from config.properties", e);
		}

		if (apiKey == null || apiKey.isBlank()) {
			// Provide a clear error message if the API key is missing or empty
			throw new RuntimeException("API key is missing or empty. Please check config.properties.");
		}

		return apiKey;
	}
}