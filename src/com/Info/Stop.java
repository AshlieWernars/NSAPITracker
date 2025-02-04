package com.Info;

public class Stop {

	private String stopName;
	private String arrivalTime;
	private String departureTime;

	public Stop(String stopName, String arrivalTime, String departureTime) {
		this.stopName = stopName;
		this.arrivalTime = arrivalTime;
		this.departureTime = departureTime;
	}

	@Override
	public String toString() {
		return stopName + "\n    " + arrivalTime + "\n    " + departureTime;
	}

	public String getStopName() {
		return stopName;
	}

	public void setStopName(String stopName) {
		this.stopName = stopName;
	}

	public String getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public String getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}
}