package com.TrainTracking;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.FileIO.FileCreator;
import com.FileIO.FileLoggers.JourneyInfoLogger;
import com.FileIO.FileLoggers.Logger;
import com.Info.ArrivalInfo;
import com.Info.DepartureInfo;
import com.Info.JourneyInfo;
import com.Main.Info;
import com.TrainTracking.API.JourneyCreation;
import com.TrainTracking.API.RequestArrivals;
import com.TrainTracking.API.RequestDepartures;
import com.TrainTracking.API.RequestJourney;

public class TrainLogger extends Thread {

	// Pre-Defined Vars
	private static final int SECONDS_IN_MINUTES = 60;
	private static final ArrayList<String> stationCodes = new ArrayList<>();

	// Vars
	private static int checkIntervalInMinutes = 1;
	private static int checkInterval = checkIntervalInMinutes * SECONDS_IN_MINUTES;
	private boolean shouldRun = true;
	private static String folderToStoreTo;
	private static String currentDate;

	// Classes
	RequestArrivals arr = new RequestArrivals();
	RequestDepartures dep = new RequestDepartures();
	RequestJourney jrny = new RequestJourney();

	public TrainLogger() {
		System.out.println("Getting new Trains every: " + checkIntervalInMinutes + " Minute(s).");
		stationCodes.add("alm"); // Almere Centrum
		stationCodes.add("asd"); // Amsterdam Centraal
		stationCodes.add("gvc"); // Den Haag Centraal
		stationCodes.add("zl"); // Zwolle
		stationCodes.add("shl"); // Schiphol Airport
		stationCodes.add("Ledn"); // Leiden Centraal
		stationCodes.add("lw"); // Leeuwarden

		// Set current date in yyyy-MM-dd format
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		currentDate = LocalDate.now().format(formatter);

		folderToStoreTo = Info.getSavefilepath() + "Tracking/" + currentDate;

		FileCreator.ensureDirectoriesExist(folderToStoreTo + "/hello.txt");

		for (String stationCode : stationCodes) {
			FileCreator.ensureDirectoriesExist(folderToStoreTo + "/" + stationCode + "/Journeys/hello.txt");
			FileCreator.ensureDirectoriesExist(folderToStoreTo + "/" + stationCode + "/Info/hello.txt");
		}

		System.out.println("Storing data to: " + folderToStoreTo);
	}

	@Override
	public void start() {
		super.start();
		super.setName("TrainLogger-Thread");

		setUncaughtExceptionHandler((thread, exception) -> {
			System.err.println("Thread " + thread.getName() + " crashed: " + exception.getMessage());
			Logger.logErrorToFile("TrainLogger.java, " + "Thread " + thread.getName() + " crashed: " + exception.getMessage());
			System.out.println(exception.getClass().getName() + ": " + exception.getMessage());

			// Restart the thread
			TrainLogger newLogger = new TrainLogger();
			newLogger.start();
		});
	}

	@Override
	public void run() {
		try {
			while (shouldRun) {
				getNewTrains();
				Thread.sleep(checkInterval * 1000L);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error in TrainLogger: " + e.getMessage(), e);
		}
	}

	public void getNewTrains() {
		for (String stationCode : stationCodes) {
			String temp = new String(folderToStoreTo);
			folderToStoreTo += "/" + stationCode;
			getNewTrainsFromStation(stationCode);
			folderToStoreTo = temp;
		}
	}

	public void getNewTrainsFromStation(String stationCode) {
		ArrayList<ArrivalInfo> arrivals = arr.getArrivalsFromStation(stationCode);
		ArrayList<DepartureInfo> departures = dep.getDeparturesFromStation(stationCode);

		if (arrivals == null || arrivals.isEmpty() || departures == null || departures.isEmpty()) {
			System.err.println("No arrivals or departures found for: " + stationCode);
			return;
		}

		ArrayList<JourneyInfo> journeys = new ArrayList<>();

		for (int i = 0; i < arrivals.size(); i++) {
			ArrivalInfo arrival = arrivals.get(i);
			String arrivalTrainNumber = arrival.getTrainNumber();

			for (int j = 0; j < departures.size(); j++) {
				DepartureInfo departure = departures.get(j);
				String departureTrainNumber = departure.getTrainNumber();

				if (!arrivalTrainNumber.equals(departureTrainNumber)) {
					continue;
				}

				JourneyInfo journey = JourneyCreation.createJourney(arrival, departure);
				journeys.add(journey);
				arrivals.remove(i);
				departures.remove(j);
				// Adjust the index since you've removed an element
				i--;
				break; // Exit once the match is found
			}
		}

		for (JourneyInfo journey : journeys) {
			try {
				jrny.getJourneyInfo(journey);
			} catch (Exception e) {
				System.out.println(e.getClass().getName() + ": " + e.getMessage());
			}

			JourneyInfoLogger.writeToFile(journey);
		}
	}

	public static String getFoldertostore() {
		return folderToStoreTo;
	}
}