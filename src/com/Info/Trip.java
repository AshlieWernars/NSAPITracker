package com.Info;

public class Trip {

	private final String tripStartTime;
	private final String tripEndTime;
	private final String tripFileContents;

	public Trip(String tripStartTime, String tripEndTime, String tripFileContents) {
		this.tripStartTime = tripStartTime;
		this.tripEndTime = tripEndTime;
		this.tripFileContents = tripFileContents;
	}

	public String getTripStartTime() {
		return tripStartTime;
	}

	public String getTripEndTime() {
		return tripEndTime;
	}

	public String getTripFileContents() {
		return tripFileContents;
	}
}