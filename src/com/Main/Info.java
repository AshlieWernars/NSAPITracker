package com.Main;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.FileIO.FileLoggers.Logger;
import com.Info.Disruption;

public class Info {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm");
	private static ArrayList<Disruption> activeDisruptions;
	private static ArrayList<Integer> activeMaterial;
	private static int activeMaterialCounter = 0;
	private static String lastTimeUpdated;

	public static ArrayList<Disruption> getActiveDisruptionsList() {
		return activeDisruptions;
	}

	public static String getActiveDisruptions() {
		return formatList(activeDisruptions);
	}

	public static int getActiveMaterialCounter() {
		return activeMaterialCounter;
	}

	public static void setActiveMaterialCounter(int activeMaterialCounter) {
		Info.activeMaterialCounter = activeMaterialCounter;
	}

	public static ArrayList<Integer> getActiveMaterial() {
		return activeMaterial;
	}

	public static void setActiveMaterial(ArrayList<Integer> activeMaterial) {
		Info.activeMaterial = activeMaterial;
	}

	public static String getLastTimeUpdated() {
		return lastTimeUpdated;
	}

	public static void setLastTimeUpdated(ZonedDateTime lastTimeUpdated) {
		Info.lastTimeUpdated = lastTimeUpdated.format(formatter);
	}

	public static String formatList(ArrayList<Disruption> list) {
		if (list == null || list.isEmpty()) {
			return "No active Disruptions";
		}

		StringBuilder sb = new StringBuilder();

		sb.append(String.format("%-10s %-10s %-15s %-20s %-15s %-15s %-15s %-30s %-20s %-20s\n", "Start Date", "Start Time", "Station 1", "Station 2", "Station 3", "Station 4", "Station 5", "Reason", "Expected End Date", "Expected End Time"));

		for (Disruption disruption : list) {
			if (disruption == null) {
				sb.append("\n"); // Functions so there is some space between maintenance and disruptions
				continue;
			}

			String[] values = disruption.toStringArray();

			if (values.length < 12) {
				Logger.logErrorToFile(disruption + "Not good");
				continue;
			}

			sb.append(String.format("%-10s %-10s %-15s %-20s %-15s %-15s %-15s %-30s %-20s %-20s\n", values[0], values[1], values[2], values[3], values[4], values[5], values[6], values[7], values[8], values[9]));

		}

		return sb.toString().trim();
	}

	public static void setActiveDisruptions(ArrayList<Disruption> activeDisruptions) {
		Info.activeDisruptions = activeDisruptions;
	}
}