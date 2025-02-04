package com.TrainTracking;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.FileIO.CSVWriter;
import com.FileIO.FileCreator;
import com.FileIO.FileLoggers.Logger;
import com.Info.Disruption;
import com.Main.Info;
import com.TrainTracking.API.NSAPI;

public class DisruptionLogger extends Thread {

	// Pre-Defined Vars
	private static final int SECONDS_IN_MINUTES = 60;
	private static final HashMap<String, Disruption> trackedDisruptions = new HashMap<>();
	private static final ArrayList<String> updatedDisruptions = new ArrayList<>();

	// Vars
	private static int checkIntervalInMinutes = 1;
	private static int checkInterval = checkIntervalInMinutes * SECONDS_IN_MINUTES;
	private boolean shouldRun = true;

	public DisruptionLogger() {
		FileCreator.ensureDirectoriesExist(Info.getSavefilepath() + "Disruptions/" + "hello.txt");

		System.out.println("Checking for disruptions every: " + checkIntervalInMinutes + " Minute(s).");
	}

	@Override
	public void start() {
		super.start();
		super.setName("DisruptionLogger-Thread");

		setUncaughtExceptionHandler((thread, exception) -> {
			System.err.println("Thread " + thread.getName() + " crashed: " + exception.getMessage());
			Logger.logErrorToFile("DisruptionLogger.java, " + "Thread " + thread.getName() + " crashed: " + exception.getMessage());
			System.out.println(exception.getClass().getName() + ": " + exception.getMessage());

			// Restart the thread
			DisruptionLogger newLogger = new DisruptionLogger();
			newLogger.start();
		});
	}

	@Override
	public void run() {
		try {
			while (shouldRun) {
				checkForDisruptions();
				Info.setActiveDisruptions(sortActiveDisruptions());
				Thread.sleep(checkInterval * 1000L);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error in DisruptionLogger: " + e.getMessage(), e);
		}
	}

	private ArrayList<Disruption> sortActiveDisruptions() {
		ArrayList<Disruption> toSort = new ArrayList<>(trackedDisruptions.values());

		List<Disruption> disruption = new ArrayList<>();
		List<Disruption> maintenance = new ArrayList<>();

		for (Disruption d : toSort) {
			String id = String.valueOf(d.getID());
			if (id.startsWith("6")) {
				disruption.add(d);
			} else if (id.startsWith("7")) {
				maintenance.add(d);
			}
		}

		disruption.sort(Comparator.comparingInt(Disruption::getID));
		maintenance.sort(Comparator.comparingInt(Disruption::getID));

		toSort.clear();
		toSort.addAll(maintenance);
		toSort.add(null);
		toSort.addAll(disruption);

		return toSort;
	}

	public void checkForDisruptions() {
		String requestURL = "https://gateway.apiportal.ns.nl/disruptions/v3?isActive=true";
		String response = NSAPI.talkToAPI(requestURL);

		if (response == null) {
			System.err.println("Problem with disruption API");
			return;
		}

		JSONArray jsonArray = new JSONArray(response.toString());

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			String id = getIDFromJSON(jsonObject);

			if (trackedDisruptions.containsKey(id)) { // Already tracked disruption
				Disruption disruption = getDisruptionFromJson(jsonObject, id);
				trackedDisruptions.replace(id, disruption);
				updatedDisruptions.add(id);
			} else { // New disruption
				Disruption disruption = getDisruptionFromJson(jsonObject, id);
				trackedDisruptions.put(id, disruption);
				String disruptionString = String.join(" ", disruption.toStringArray());
				System.out.println("Disruption started. ID: " + id);
				FileCreator.createFile(Info.getSavefilepath() + "Disruptions/" + id + ".txt", disruptionString);
				updatedDisruptions.add(id);
			}
		}

		Iterator<String> iterator = trackedDisruptions.keySet().iterator();
		while (iterator.hasNext()) {
			String id = iterator.next();

			if (updatedDisruptions.contains(id)) {
				continue; // Disruption still active
			}

			LocalDateTime now = LocalDateTime.now();

			// Format for date
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			String date = now.format(dateFormatter);

			// Format for time
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
			String time = now.format(timeFormatter);

			trackedDisruptions.get(id).setActualEndDate(date);
			trackedDisruptions.get(id).setActualEndTime(time);

			// Write to CSV
			CSVWriter.writeToCSV(trackedDisruptions.get(id).toStringArray());

			System.out.println("Disruption ended. ID: " + id);

			// Remove from map
			iterator.remove();
		}

		updatedDisruptions.clear();
	}

	private static String getIDFromJSON(JSONObject jsonObject) {
		try {
			return jsonObject.get("id").toString();
		} catch (JSONException e) {
			Logger.logErrorToFile("DisruptionLogger.java, " + "Problem getting ID: " + jsonObject);
			throw new RuntimeException(e);
		}
	}

	public static Disruption getDisruptionFromJson(JSONObject jsonObject, String ID) {
		Integer id = null;
		String[] stations = null;
		String type = null;
		String startDate = null;
		String startTime = null;
		String expectedEndDate = null;
		String expectedEndTime = null;
		String cause = null;

		try {
			String numericId = ID.replaceAll("\\D", "");
			id = numericId.isEmpty() ? -1 : Integer.parseInt(numericId);
		} catch (@SuppressWarnings("unused") Exception e) {
			Logger.logErrorToFile("DisruptionLogger.java, Can't cast ID for disruption: " + ID);
			id = -1;
		}

		try {
			// Extracting affected stations
			String title = jsonObject.get("title").toString(); // Heerlen - Maastricht Randwyck.

			title = title.replace(".", ""); // Heerlen - Maastricht Randwyck

			String[] semiSplitStations = splitStations(new String[] { title }, ";"); // Heerlen - Maastricht Randwyck

			String[] dashSplitStations = splitStations(semiSplitStations, "-"); // 0: Heerlen, 1: Maastricht Randwyck

			stations = splitStations(dashSplitStations, " "); // 0: Heerlen, 1: Maastricht, 2: Randwyck

		} catch (@SuppressWarnings("unused") JSONException e) {
			Logger.logErrorToFile("DisruptionLogger.java, " + "Can't find title/stations for disruption: " + ID);
			stations = new String[] { "UNKOWN" };
		}

		try {
			type = jsonObject.getString("type");
		} catch (@SuppressWarnings("unused") JSONException e) {
			Logger.logErrorToFile("DisruptionLogger.java, " + "Can't find type for disruption: " + ID);
			type = "UNKOWN";
		}

		try {
			String startDateTime = jsonObject.getString("start");
			// Custom formatter to handle "+HHMM" format
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXX");
			OffsetDateTime offsetDateTime = OffsetDateTime.parse(startDateTime, formatter);
			LocalDateTime dateTime = offsetDateTime.toLocalDateTime();
			startDate = dateTime.toLocalDate().toString();
			startTime = dateTime.toLocalTime().toString();
		} catch (@SuppressWarnings("unused") JSONException e) {
			Logger.logErrorToFile("DisruptionLogger.java, " + "Can't find start for disruption: " + ID);
			LocalDateTime now = LocalDateTime.now();

			// Format for date
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			startDate = now.format(dateFormatter);

			// Format for time
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
			startTime = now.format(timeFormatter);
		}

		// Extracting expected end time
		try {
			String expectedEndDateTime = jsonObject.getJSONObject("expectedDuration").getString("endTime");
			// Custom formatter to handle "+HHMM" format
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXX");
			OffsetDateTime offsetDateTime = OffsetDateTime.parse(expectedEndDateTime, formatter);
			LocalDateTime dateTime = offsetDateTime.toLocalDateTime();
			expectedEndDate = dateTime.toLocalDate().toString();
			expectedEndTime = dateTime.toLocalTime().toString();

		} catch (@SuppressWarnings("unused") JSONException e) {
			try {
				JSONArray array = jsonObject.getJSONArray("timespans");
				JSONObject object = array.getJSONObject(0);

				String expectedEndDateTime = object.get("end").toString();
				// Custom formatter to handle "+HHMM" format
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXX");
				OffsetDateTime offsetDateTime = OffsetDateTime.parse(expectedEndDateTime, formatter);
				LocalDateTime dateTime = offsetDateTime.toLocalDateTime();
				expectedEndDate = dateTime.toLocalDate().toString();
				expectedEndTime = dateTime.toLocalTime().toString();

			} catch (@SuppressWarnings("unused") JSONException e1) {
				Logger.logErrorToFile("DisruptionLogger.java, " + "Can't find end time for disruption: " + ID);
				expectedEndDate = "UNKOWN";
				expectedEndTime = "UNKOWN";
			}
		}

		try {
			// Extracting cause
			cause = jsonObject.getJSONArray("timespans").getJSONObject(0).getJSONObject("cause").getString("label");
		} catch (@SuppressWarnings("unused") JSONException e) {
			Logger.logErrorToFile("DisruptionLogger.java, " + "Can't find cause for disruption: " + ID);
			cause = type;
		}

		return new Disruption(id, stations, type, startDate, startTime, expectedEndDate, expectedEndTime, null, null, cause);
	}

	private static String[] splitStations(String[] stations, String splitter) {
		ArrayList<String> returnStations = new ArrayList<>();
		for (int i = 0; i < stations.length; i++) {
			String[] stationsGroup = stations[i].split(splitter);
			for (int j = 0; j < stationsGroup.length; j++) {
				returnStations.add(stationsGroup[j].trim());
			}
		}
		return returnStations.toArray(new String[0]);
	}
}