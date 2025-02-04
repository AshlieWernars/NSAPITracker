package com.TrainTracking.API;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.FileIO.FileLoggers.Logger;
import com.Main.Config;

public class NSAPI {

	private static final String API_KEY = Config.getSafeApiKey();

	public static String talkToAPI(String requestURL) {
		if (requestURL == null || requestURL.isEmpty()) {
			System.err.println("Request URL is empty or invalid!");
			return null;
		}

		try {
			URL url = new URL(requestURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// Request headers
			connection.setRequestProperty("Cache-Control", "no-cache");

			connection.setRequestProperty("Ocp-Apim-Subscription-Key", API_KEY);

			connection.setRequestMethod("GET");

			int status = connection.getResponseCode();

			if (status == HttpURLConnection.HTTP_NOT_FOUND) {
				return "NOT FOUND";
			}

			if (status != HttpURLConnection.HTTP_OK) {
				throw new Exception("Problem with connection, Error Code: " + status);
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			in.close();

			connection.disconnect();

			return content.toString();
		} catch (Exception e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
			Logger.logErrorToFile("NSAPI.java, " + "NS API crashed: " + e.getMessage());
		}

		return null;
	}
}