package com.TrainTracking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import com.FileIO.FileCreator;
import com.FileIO.FileLoggers.Logger;
import com.Main.Info;
import com.TrainTracking.API.MaterialChecker;

public class MaterialLogger extends Thread {

	// Pre-Defined Vars
	private final int SECONDS_IN_MINUTES = 60;
	private final int checkTime = 8;
	private final ArrayList<Integer> materialNumbers = new ArrayList<>();
	private final ArrayList<Integer> activeMaterialNumbers = new ArrayList<>();

	// Vars
	private int checkIntervalInMinutes = 10 - checkTime;
	private int checkInterval = checkIntervalInMinutes * SECONDS_IN_MINUTES;
	private boolean shouldRun = true;
	private String folderToStoreTo;
	private String currentDate;

	// Classes
	private final MaterialChecker tripNumberAPI = new MaterialChecker();

	public MaterialLogger() {
		System.out.println("Checking which trains are running every: " + checkIntervalInMinutes + " Minute(s).");

		loadAllTrainNumbers();

		setFoldersToStoreToBasedOnCurrentDate();

		setUncaughtExceptionHandler((thread, exception) -> {
			System.err.println("Thread " + thread.getName() + " crashed: " + exception.getMessage());
			Logger.logErrorToFile("MaterialLogger.java, " + "Thread " + thread.getName() + " crashed: " + exception.getMessage());
			System.out.println(exception.getClass().getName() + ": " + exception.getMessage());

			// Restart the thread
			MaterialLogger newLogger = new MaterialLogger();
			newLogger.start();
		});
	}

	private void loadAllTrainNumbers() {
		materialNumbers.clear();

		try (BufferedReader reader = new BufferedReader(new FileReader(Info.getSavefilepath() + "alleTreinen.txt"))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				materialNumbers.add(Integer.valueOf(line.trim()));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void setFoldersToStoreToBasedOnCurrentDate() {
		// Set current date in yyyy-MM-dd format
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		currentDate = LocalDate.now().format(formatter);

		folderToStoreTo = Info.getSavefilepath() + "Tracking/" + currentDate + "/MaterialTracking/";

		FileCreator.ensureDirectoriesExist(folderToStoreTo + "/hello.txt");

		for (Integer materialNumber : materialNumbers) {
			FileCreator.ensureDirectoriesExist(folderToStoreTo + "/" + materialNumber + "/hello.txt");
			FileCreator.ensureDirectoriesExist(folderToStoreTo + "/" + materialNumber + "/hello.txt");
		}
	}

	@Override
	public void start() {
		super.setName("MaterialLogger-Thread");
		super.start();
	}

	@Override
	public void run() {
		try {
			while (shouldRun) {
				getNewTrains();
				Thread.sleep(checkInterval * 1000L);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error in MaterialLogger: " + e.getMessage(), e);
		}
	}

	public void getNewTrains() {
		setFoldersToStoreToBasedOnCurrentDate();

		for (Integer materialNumber : materialNumbers) {
			String temp = new String(folderToStoreTo);
			folderToStoreTo += "/" + materialNumber;
			if (tripNumberAPI.doesMaterialHaveATripAttached(materialNumber, folderToStoreTo)) {
				activeMaterialNumbers.add(materialNumber);
			}
			folderToStoreTo = temp;
		}

		// Use HashSet to remove duplicates
		HashSet<Integer> numberSet = new HashSet<>(activeMaterialNumbers);

		// Convert back to ArrayList
		ArrayList<Integer> uniqueMaterialNumbers = new ArrayList<>(numberSet);

		// Sort the unique numbers
		Collections.sort(uniqueMaterialNumbers);

		int amountOfActiveMaterial = 0;

		StringBuilder activeMaterialNumberFile = new StringBuilder();
		activeMaterialNumberFile.append("Active material:").append("\n");
		for (Integer activeMaterialNumber : uniqueMaterialNumbers) {
			activeMaterialNumberFile.append("  " + activeMaterialNumber).append("\n");
			amountOfActiveMaterial++;
		}

		Info.setActiveMaterial(uniqueMaterialNumbers);
		Info.setLastTimeUpdated(ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()));
		Info.setActiveMaterialCounter(amountOfActiveMaterial);

		activeMaterialNumbers.clear();

		// Get the current time
		LocalTime currentTime = LocalTime.now();

		// Define the desired format
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH-mm-ss");

		// Format the current time
		String formattedTime = currentTime.format(formatter);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(folderToStoreTo + "activeMaterial" + formattedTime + ".txt"))) {
			writer.append(activeMaterialNumberFile.toString());
		} catch (IOException e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}
}