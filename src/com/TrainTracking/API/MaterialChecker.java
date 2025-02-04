package com.TrainTracking.API;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.FileIO.FileCreator;
import com.Info.Stop;
import com.Info.Trip;

public class MaterialChecker {

	public boolean doesMaterialHaveATripAttached(int materialNumber, String folderToStoreTo) {
		String urlString = "https://gateway.apiportal.ns.nl/virtual-train-api/v1/ritnummer/";
		String requestURL = urlString + materialNumber;

		String response = NSAPI.talkToAPI(requestURL);

		if (response == null) {
			System.err.println("Problem with trip number API");
			return false;
		}

		if (response.equals("NOT FOUND")) { // HTTP CODE 404, means no trip was found for this train.
			return false;
		}

		Trip trip = getTripInfo(Integer.valueOf(response.toString()));

		if (trip == null) {
			return false;
		}

		if (trip.getTripStartTime().equals("UNKOWN") || trip.getTripEndTime().equals("UNKOWN")) {
			return false;
		}

		LocalTime leaveTime = LocalTime.parse(trip.getTripStartTime());
		LocalTime arrivalTime = LocalTime.parse(trip.getTripEndTime());

		ZonedDateTime plannedLeaveTime = ZonedDateTime.of(LocalDate.now(), leaveTime, ZoneId.systemDefault());
		ZonedDateTime plannedArrivalTime = ZonedDateTime.of(LocalDate.now(), arrivalTime, ZoneId.systemDefault());

		ZonedDateTime currentTime = ZonedDateTime.now();

		if (currentTime.isAfter(plannedArrivalTime)) {
			return false;
		} else if (currentTime.isBefore(plannedLeaveTime)) {
			return false;
		}
		// The journey is happening

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(folderToStoreTo + "/" + response + ".txt"))) {
			writer.append(trip.getTripFileContents());
		} catch (Exception e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}

		return true; // The material has a trip connected to it.
	}

	private Trip getTripInfo(int tripNumber) {
		if (tripNumber < 1) {
			System.out.println("Please give a valid tripNumber");
			return null;
		}

		ArrayList<Stop> stopsAt = new ArrayList<>();
		ArrayList<Integer> stock = new ArrayList<>();

		String requestURL = "https://gateway.apiportal.ns.nl/reisinformatie-api/api/v2/journey?train=" + tripNumber;
		String response = NSAPI.talkToAPI(requestURL);

		if (response == null) {
			return null;
		}

		if (response.equals("NOT FOUND")) { // HTTP CODE 404, means no trip was found for this tripNumber?.
											// Shouldn't happen.
			return null;
		}

		String jsonResponse = response.toString();

		JSONObject jsonObject = null;

		try {
			jsonObject = new JSONObject(jsonResponse);
		} catch (@SuppressWarnings("unused") JSONException e) {
			FileCreator.createFile("/home/ashlie/Desktop/" + tripNumber + ".txt", jsonResponse);
		}

		JSONArray stops = jsonObject.getJSONObject("payload").getJSONArray("stops");

		for (int i = 0; i < stops.length(); i++) {
			JSONObject stop = stops.getJSONObject(i);
			processStop(stop, stopsAt, stock);
		}

		String tripStartTime = null;
		String tripEndTime = null;

		StringBuilder tripFileContents = new StringBuilder();
		tripFileContents.append("Trip number: " + tripNumber).append("\n");
		tripFileContents.append("Stops: ").append("\n");
		for (Stop stop : stopsAt) {
			tripFileContents.append("  " + stop).append("\n");

			if (stop.getArrivalTime().equalsIgnoreCase("ORIGIN")) {
				tripStartTime = stop.getDepartureTime();
			}

			if (stop.getDepartureTime().equalsIgnoreCase("DESTINATION")) {
				tripEndTime = stop.getArrivalTime();
			}
		}

		tripFileContents.append("Stock: ").append("\n");
		for (Integer stockID : stock) {
			tripFileContents.append("  " + stockID).append("\n");
		}

		return new Trip(tripStartTime, tripEndTime, tripFileContents.toString());
	}

	private void processStop(JSONObject stop, ArrayList<Stop> stopsAt, ArrayList<Integer> stock) {
		String stopName = stop.getJSONObject("stop").getString("name");
		String status = stop.getString("status");

		if (status.equals("PASSING")) {
			return;
		}

		JSONArray arrivals = stop.getJSONArray("arrivals");
		JSONArray departures = stop.getJSONArray("departures");

		if (status.equals("STOP")) {
			String arrivalTime;
			String departureTime;

			try {
				arrivalTime = fixDateTimeFormat(arrivals.getJSONObject(0).getString("actualTime"));
			} catch (@SuppressWarnings("unused") Exception e) {
				arrivalTime = "UNKOWN";
			}

			try {
				departureTime = fixDateTimeFormat(departures.getJSONObject(0).getString("actualTime"));
			} catch (@SuppressWarnings("unused") Exception e) {
				departureTime = "UNKOWN";
			}

			stopsAt.add(new Stop(stopName, arrivalTime, departureTime));

		} else if (status.equals("DESTINATION")) {
			String arrivalTime;

			try {
				arrivalTime = fixDateTimeFormat(arrivals.getJSONObject(0).getString("actualTime"));
			} catch (@SuppressWarnings("unused") Exception e) {
				arrivalTime = "UNKOWN";
			}

			stopsAt.add(new Stop(stopName, arrivalTime, "DESTINATION"));

		} else if (status.equals("ORIGIN")) {
			String departureTime;

			try {
				departureTime = fixDateTimeFormat(departures.getJSONObject(0).getString("actualTime"));
			} catch (@SuppressWarnings("unused") Exception e) {
				departureTime = "UNKOWN";
			}

			stopsAt.add(new Stop(stopName, "ORIGIN", departureTime));
		}

		processStock(departures, stock);
	}

	private void processStock(JSONArray departures, ArrayList<Integer> stock) {
		JSONArray stockIdentifiers;
		try {
			stockIdentifiers = departures.getJSONObject(0).getJSONArray("stockIdentifiers");
		} catch (@SuppressWarnings("unused") Exception e) {
			return;
		}

		for (int i = 0; i < stockIdentifiers.length(); i++) {
			String stockIDString = stockIdentifiers.getString(i).trim();
			int stockID = Integer.valueOf(stockIDString);

			if (stockID <= 0) {
				continue;
			}

			if (stock.isEmpty()) {
				stock.add(stockID);
				continue;
			}

			// Check if stockID already exists in the stock list
			boolean exists = false;
			for (int j = 0; j < stock.size(); j++) {
				if (stock.get(j).equals(stockID)) {
					exists = true;
					break; // Exit loop if stockID is found
				}
			}

			// If stockID does not exist, add it to the list
			if (!exists) {
				stock.add(stockID);
			}
		}
	}

	// Utility method to fix the timezone offset format from +0200 to +02:00
	private static String fixDateTimeFormat(String dateTime) {
		return dateTime.substring(11, 19);
	}
}