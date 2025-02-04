package com.FileIO.FileLoggers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

import com.Info.JourneyInfo;
import com.TrainTracking.TrainLogger;

public class JourneyInfoLogger {

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

	public static void writeToFile(JourneyInfo journeyInfo) {
		String tripName = getTripName(journeyInfo.getTripNumber()).replaceAll("[/\\\\:*?\"<>|]", "");
		Path baseDir = Paths.get(TrainLogger.getFoldertostore()).toAbsolutePath().normalize();
		Path filePath = baseDir.resolve("Journeys").resolve("journeyLog" + tripName + ".txt").normalize();

		// Ensure the filePath is still inside baseDir
		if (!filePath.startsWith(baseDir)) {
			throw new SecurityException("Invalid file path detected!");
		}

		try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
			writer.write("Origin: \n  " + journeyInfo.getOrigin() + "\n");

			if (journeyInfo.getStopsAt() != null) {
				writer.write("Stops At: " + "\n");
				for (String stop : journeyInfo.getStopsAt()) {
					writer.write("  " + stop + "\n");
				}
			} else {
				writer.write("Stops At: " + journeyInfo.getRouteStations() + "\n");
			}

			writer.write("Destination:\n  " + journeyInfo.getDestination() + "\n");

			writer.write("Trip Number:\n  " + getTripName(journeyInfo.getTripNumber()) + "\n");

			String delay = calculateDelay(journeyInfo.getArrivalDelay());

			writer.write("Arrival Time:\n  " + journeyInfo.getArrivalActualTime().format(formatter) + delay + "\n");

			delay = calculateDelay(journeyInfo.getDepartureDelay());

			writer.write("Departure Time:\n  " + journeyInfo.getDepartureActualTime().format(formatter) + delay + "\n");

			writer.write("Track:\n  " + journeyInfo.getTrack() + "\n");
			writer.write("Operator Code:\n  " + journeyInfo.getOperatorCode() + "\n");
			writer.write("Category Code:\n  " + journeyInfo.getCategoryCode() + "\n");

			writer.write("Stock: " + "\n");
			if (journeyInfo.getStock() != null) {
				for (Integer stockID : journeyInfo.getStock()) {
					writer.write("  " + stockID + "\n");
				}
			} else {
				writer.write("  Unkown" + "\n");
			}

			writer.write("Messages: \n");
			if (journeyInfo.getMessages().isEmpty()) {
				writer.write("  No messages.\n");
			} else {
				for (Object message : journeyInfo.getMessages()) {
					writer.write("  " + message.toString() + "\n");
				}
			}

			writer.write("Departure Status:\n  " + journeyInfo.getStatus() + "\n");
			writer.write("\n");

		} catch (IOException e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	private static String calculateDelay(Duration delayDuration) {
		if (delayDuration == null) {
			return "UNKOWN Delay";
		}

		String delay = formatDuration(delayDuration);

		delay = delay.equals("00:00:00") ? "" : " (" + delay + " Delay)";

		return delay;
	}

	private static String getTripName(String tripNumber) {
		if (tripNumber.startsWith("NS INT")) {
			return tripNumber;
		}

		if (tripNumber.startsWith("Blauwnet")) {
			return tripNumber;
		}

		if (tripNumber.startsWith("NS")) {
			return tripNumber.replaceAll("  ", " ");
		}
		return tripNumber;
	}

	// Utility function to format Duration into hh:mm:ss
	private static String formatDuration(Duration duration) {
		long hours = duration.toHours();
		long minutes = duration.toMinutesPart();
		long seconds = duration.toSecondsPart();
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
}