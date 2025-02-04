package com.Info;

public class Disruption {

	private final int id;
	private final String[] stations;
	private final String type;
	private String startDate;
	private String startTime;
	private String expectedEndDate;
	private String expectedEndTime;
	private String actualEndDate;
	private String actualEndTime;
	private final String cause;

	public Disruption(int id, String[] stations, String type, String startDate, String startTime, String expectedEndDate, String expectedEndTime, String actualEndDate, String actualEndTime, String cause) {
		this.id = id;
		this.stations = stations;
		this.type = type;
		this.startDate = startDate;
		this.startTime = startTime;
		this.expectedEndDate = expectedEndDate;
		this.expectedEndTime = expectedEndTime;
		this.actualEndDate = actualEndDate;
		this.actualEndTime = actualEndTime;
		this.cause = cause;
	}

	public String[] toStringArray() {
		String station0 = "";
		String station1 = "";
		String station2 = "";
		String station3 = "";
		String station4 = "";

		try {
			station0 = stations[0];
			station1 = stations[1];
			station2 = stations[2];
			station3 = stations[3];
			station4 = stations[4];
		} catch (@SuppressWarnings("unused") Exception e) {
			// It might not have more than 2 stations affected but I have seen up to 4.
		}

		return new String[] { startDate, startTime, station0, station1, station2, station3, station4, cause, expectedEndDate, expectedEndTime, actualEndDate, actualEndTime };
	}

	public int getID() {
		return id;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getExpectedEndDate() {
		return expectedEndDate;
	}

	public void setExpectedEndDate(String expectedEndDate) {
		this.expectedEndDate = expectedEndDate;
	}

	public String getExpectedEndTime() {
		return expectedEndTime;
	}

	public void setExpectedEndTime(String expectedEndTime) {
		this.expectedEndTime = expectedEndTime;
	}

	public String getActualEndDate() {
		return actualEndDate;
	}

	public void setActualEndDate(String actualEndDate) {
		this.actualEndDate = actualEndDate;
	}

	public String getActualEndTime() {
		return actualEndTime;
	}

	public void setActualEndTime(String actualEndTime) {
		this.actualEndTime = actualEndTime;
	}

	public String[] getStations() {
		return stations;
	}

	public String getType() {
		return type;
	}

	public String getCause() {
		return cause;
	}
}