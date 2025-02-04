package com.FileIO;

import java.io.BufferedWriter;
import java.io.FileWriter;

import com.Main.Info;

public class CSVWriter {

	private static final String filePath = Info.getSavefilepath() + "disruptions.csv";

	public static void writeToCSV(String[] rowData) {
		try {
			// Open the file in append mode
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));

			// Write the new row data
			writer.write(String.join(",", rowData));
			writer.newLine();

			writer.close();
		} catch (Exception e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}
}