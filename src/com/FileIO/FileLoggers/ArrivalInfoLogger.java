package com.FileIO.FileLoggers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.Info.ArrivalInfo;
import com.TrainTracking.TrainLogger;

public class ArrivalInfoLogger {

	public static void writeToFile(ArrivalInfo arrivalInfo) {
		String tripNumber = arrivalInfo.getTrainNumber().replaceAll("[/\\\\:*?\"<>|]", "");
		Path baseDir = Paths.get(TrainLogger.getFoldertostore()).toAbsolutePath().normalize();
		Path filePath = baseDir.resolve("Info").resolve("arrivalInfo_" + tripNumber + ".txt").normalize();

		// Ensure the filePath is still inside baseDir
		if (!filePath.startsWith(baseDir)) {
			throw new SecurityException("Invalid file path detected!");
		}

		try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
			writer.write("Origin: " + arrivalInfo.getOrigin());
			writer.newLine();
			writer.write("Train Number: " + arrivalInfo.getTrainNumber());
			writer.newLine();
			writer.write("Planned DateTime: " + arrivalInfo.getPlannedDateTime());
			writer.newLine();
			writer.write("Actual DateTime: " + arrivalInfo.getActualDateTime());
			writer.newLine();
			String track = arrivalInfo.getActualTrack().equals(arrivalInfo.getPlannedTrack()) ? arrivalInfo.getActualTrack() : arrivalInfo.getPlannedTrack() + " -> " + arrivalInfo.getActualTrack();
			writer.write("Track: " + track);
			writer.newLine();

			writer.write("Product Information:");
			writer.newLine();
			writer.write("  Number: " + arrivalInfo.getNumber());
			writer.newLine();
			writer.write("  Category Code: " + arrivalInfo.getCategoryCode());
			writer.newLine();
			writer.write("  Short Category Name: " + arrivalInfo.getShortCategoryName());
			writer.newLine();
			writer.write("  Long Category Name: " + arrivalInfo.getLongCategoryName());
			writer.newLine();
			writer.write("  Operator Name: " + arrivalInfo.getOperatorName());
			writer.newLine();
			writer.write("  Operator Code: " + arrivalInfo.getOperatorCode());
			writer.newLine();
			writer.write("  Type: " + arrivalInfo.getType());
			writer.newLine();

			writer.write("Train Category: " + arrivalInfo.getTrainCategory());
			writer.newLine();
			writer.write("Cancelled: " + arrivalInfo.isCancelled());
			writer.newLine();

			writer.write("Messages: ");
			writer.newLine();
			if (arrivalInfo.getMessagesList().isEmpty()) {
				writer.write("  No messages.");
				writer.newLine();
			} else {
				for (Object message : arrivalInfo.getMessagesList()) {
					writer.write("  " + message.toString());
					writer.newLine();
				}
			}

			writer.write("Arrival Status: " + arrivalInfo.getArrivalStatus());
			writer.newLine();

			writer.flush();
		} catch (IOException e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}
}