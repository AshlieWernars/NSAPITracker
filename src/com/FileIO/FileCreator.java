package com.FileIO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileCreator {

	public static void createFile(String fullFilePath, String fileContents) {
		ensureDirectoriesExist(fullFilePath);

		File fileToCreate = new File(fullFilePath);

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileToCreate));
			writer.write(fileContents);
			writer.close();
		} catch (IOException e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public static void ensureDirectoriesExist(String fileName) {
		File file = new File(fileName);
		File parentDir = file.getParentFile();

		if (parentDir != null && !parentDir.exists()) {
			if (!parentDir.mkdirs()) {
				throw new RuntimeException("Failed to create directories: " + parentDir.getAbsolutePath());
			}
		}
	}
}