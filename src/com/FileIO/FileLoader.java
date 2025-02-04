package com.FileIO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileLoader {

	public static String[] loadFile(String fileName) {
		List<String> lines = new ArrayList<>();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return lines.toArray(new String[0]);
	}

	public static String loadFileToString(String fileName) {
		StringBuilder lines = new StringBuilder();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				lines.append(line);
			}
		} catch (IOException e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return lines.toString();
	}
}