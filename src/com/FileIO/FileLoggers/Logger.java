package com.FileIO.FileLoggers;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.FileIO.FileCreator;
import com.FileIO.FileLoader;
import com.Main.Info;

public class Logger {

	private static final String errorLogFilePath = Info.getSavefilepath() + "error.log";

	public static String getCurrentTime() {
		LocalDateTime now = LocalDateTime.now();

		// Thursday July 18, 01:24:30 PM
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, hh:mm:ss a", Locale.ENGLISH);

		return now.format(formatter);
	}

	public static void logErrorToFile(String errorMessage) {
		errorMessage = "On " + getCurrentTime() + ", " + errorMessage;

		if (!doesFileExist(errorLogFilePath)) {
			FileCreator.createFile(errorLogFilePath, errorMessage);
			return;
		}

		appendToErrorLogFile(errorLogFilePath, errorMessage);
	}

	private static void appendToErrorLogFile(String logFileName, String errorMessage) {
		String[] fileContents = FileLoader.loadFile(logFileName);

		StringBuilder file = new StringBuilder();
		for (String string : fileContents) {
			file.append(string).append("\n");
		}
		file.append(errorMessage);

		FileCreator.createFile(logFileName, file.toString());
	}

	public static boolean doesFileExist(String path) {
		File fileToCheck = new File(path);
		return fileToCheck.exists();
	}
}