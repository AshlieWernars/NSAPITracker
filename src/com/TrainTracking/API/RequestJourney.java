package com.TrainTracking.API;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.FileIO.FileLoggers.Logger;
import com.Info.JourneyInfo;

public class RequestJourney {

	public void getJourneyInfo(JourneyInfo journeyInfo) throws Exception {
		if (journeyInfo == null) {
			System.out.println("Invalid journeyInfo class");
			return;
		}

		int tripNumber = -1;
		String tripNumberString = journeyInfo.getTripNumber();
		if (tripNumberString.startsWith("NS INT") || tripNumberString.startsWith("NS Int")) {
			tripNumber = Integer.valueOf(tripNumberString.substring(6).trim());
		} else if (tripNumberString.startsWith("Eu Sleeper")) {
			tripNumber = Integer.valueOf(tripNumberString.substring(10).trim());
		} else if (tripNumberString.startsWith("Blauwnet")) {
			tripNumber = Integer.valueOf(tripNumberString.substring(8).trim());
		} else if (tripNumberString.startsWith("Arriva")) {
			tripNumber = Integer.valueOf(tripNumberString.substring(6).trim());
		} else if (tripNumberString.startsWith("NS")) {
			tripNumber = Integer.valueOf(tripNumberString.substring(2).trim());
		} else {
			System.err.println("Tripnumber is not recognized: " + tripNumberString);
			Logger.logErrorToFile("RequestJourney.java, " + "Tripnumber is not recognized: " + tripNumberString);
			return;
		}

		if (tripNumber == -1) { // Trip number should be assigned but still :)
			System.err.println("Tripnumber is invalid: " + tripNumberString);
			Logger.logErrorToFile("RequestJourney.java, " + "Tripnumber is invalid: " + tripNumberString);
			return;
		}

		String requestURL = "https://gateway.apiportal.ns.nl/reisinformatie-api/api/v2/journey?train=" + tripNumber;

		String response = NSAPI.talkToAPI(requestURL);

		if (response == null) {
			System.err.println("Problem with journey API");
			Logger.logErrorToFile("RequestJourney.java, " + "Problem with journey API: " + tripNumberString);
			return;
		}

		if (response.equals("NOT FOUND")) { // Trip not found
			System.err.println("No trip found for journey");
			Logger.logErrorToFile("RequestJourney.java, " + "Problem with journey API: " + tripNumberString);
			return;
		}

		JSONObject jsonObject = new JSONObject(response.toString());
		JSONArray stops = jsonObject.getJSONObject("payload").getJSONArray("stops");

		ArrayList<String> stopsAt = new ArrayList<>();
		ArrayList<Integer> stock = new ArrayList<>();

		for (int i = 0; i < stops.length(); i++) {
			JSONObject stop = stops.getJSONObject(i);
			processStop(stop, stopsAt, stock);
		}

		if (stopsAt.isEmpty()) {
			System.err.println("No stops found");
			Logger.logErrorToFile("RequestJourney.java, " + "No stops found: " + tripNumberString);
			return;
		}

		journeyInfo.setStopsAt(stopsAt);

		if (stock.isEmpty()) { // Usually only happens bc of disruptions.
			Logger.logErrorToFile("RequestJourney.java, " + "No stock found: " + tripNumberString);
			return;
		}

		journeyInfo.setStock(stock);

		return;
	}

	private void processStop(JSONObject stop, ArrayList<String> stopsAt, ArrayList<Integer> stock) {
		String stopName = stop.getJSONObject("stop").getString("name");
		String status = stop.getString("status");

		if (status.equals("PASSING")) { // Station is not stopped at
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

			stopsAt.add(stopName + "\n    " + arrivalTime + "\n    " + departureTime);

		} else if (status.equals("DESTINATION")) {
			String arrivalTime;

			try {
				arrivalTime = fixDateTimeFormat(arrivals.getJSONObject(0).getString("actualTime"));
			} catch (@SuppressWarnings("unused") Exception e) {
				arrivalTime = "UNKOWN";
			}

			stopsAt.add(stopName + "\n    " + arrivalTime + "\n    " + "DESTINATION");

		} else if (status.equals("ORIGIN")) {
			String departureTime;

			try {
				departureTime = fixDateTimeFormat(departures.getJSONObject(0).getString("actualTime"));
			} catch (@SuppressWarnings("unused") Exception e) {
				departureTime = "UNKOWN";
			}

			stopsAt.add(stopName + "\n    " + "ORIGIN" + "\n    " + departureTime);
		}

		processStock(departures, stock);
	}

	private void processStock(JSONArray departures, ArrayList<Integer> stock) {
		JSONArray stockIdentifiers;
		try {
			stockIdentifiers = departures.getJSONObject(0).getJSONArray("stockIdentifiers");
		} catch (@SuppressWarnings("unused") Exception e) { // No stock found for this departure
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