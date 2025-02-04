package com.FileIO.FileLoggers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.Info.DepartureInfo;
import com.TrainTracking.TrainLogger;

public class DepartureInfoLogger {

	public static void writeToFile(DepartureInfo departureInfo) {
		// File output
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(TrainLogger.getFoldertostore() + "/Info/" + "departureInfo_" + departureInfo.getTrainNumber() + ".txt"))) {
			writer.write("Direction: " + departureInfo.getDirection());
			writer.newLine();
			writer.write("Train Number: " + departureInfo.getTrainNumber());
			writer.newLine();
			writer.write("Planned DateTime: " + departureInfo.getPlannedDateTime());
			writer.newLine();
			writer.write("Actual DateTime: " + departureInfo.getActualDateTime());
			writer.newLine();
			String track = departureInfo.getActualTrack().equals(departureInfo.getPlannedTrack()) ? departureInfo.getActualTrack() : departureInfo.getPlannedTrack() + " -> " + departureInfo.getActualTrack();
			writer.write("Track: " + track);
			writer.newLine();

			writer.write("Product Information:");
			writer.newLine();
			writer.write("  Number: " + departureInfo.getNumber());
			writer.newLine();
			writer.write("  Category Code: " + departureInfo.getCategoryCode());
			writer.newLine();
			writer.write("  Short Category Name: " + departureInfo.getShortCategoryName());
			writer.newLine();
			writer.write("  Long Category Name: " + departureInfo.getLongCategoryName());
			writer.newLine();
			writer.write("  Operator Name: " + departureInfo.getOperatorName());
			writer.newLine();
			writer.write("  Operator Code: " + departureInfo.getOperatorCode());
			writer.newLine();
			writer.write("  Type: " + departureInfo.getType());
			writer.newLine();

			writer.write("Train Category: " + departureInfo.getTrainCategory());
			writer.newLine();
			writer.write("Cancelled: " + departureInfo.isCancelled());
			writer.newLine();

			writer.write("Stops At: " + departureInfo.getRouteStations());
			writer.newLine();

			writer.write("Messages: ");
			writer.newLine();
			if (departureInfo.getMessagesList().isEmpty()) {
				writer.write("  No messages.");
				writer.newLine();
			} else {
				for (Object message : departureInfo.getMessagesList()) {
					writer.write("  " + message.toString());
					writer.newLine();
				}
			}

			writer.write("Departure Status: " + departureInfo.getDepartureStatus());
			writer.newLine();

			writer.flush();
		} catch (IOException e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}
}