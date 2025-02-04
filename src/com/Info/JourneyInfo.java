package com.Info;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class JourneyInfo {

	private final String origin;
	private final String routeStations;
	private final String destination;
	private final String tripNumber;
	private final ZonedDateTime arrivalPlannedTime;
	private final ZonedDateTime arrivalActualTime;
	private final Duration arrivalDelay;
	private final ZonedDateTime departurePlannedTime;
	private final ZonedDateTime departureActualTime;
	private final Duration departureDelay;
	private final String track;

	private final String operatorCode;
	private final String categoryCode;

	private final List<Object> messages;

	private final String status;
	private List<String> stopsAt;
	private List<Integer> stock;

	public JourneyInfo(String origin, String routeStations, String destination, String tripNumber, ZonedDateTime arrivalPlannedTime, ZonedDateTime arrivalActualTime, Duration arrivalDelay, ZonedDateTime departurePlannedTime, ZonedDateTime departureActualTime, Duration departureDelay, String track, String operatorCode, String categoryCode, List<Object> messages, String status) {
		this.origin = origin;
		this.routeStations = routeStations;
		this.destination = destination;
		this.tripNumber = tripNumber;
		this.arrivalPlannedTime = arrivalPlannedTime;
		this.arrivalActualTime = arrivalActualTime;
		this.arrivalDelay = arrivalDelay;
		this.departurePlannedTime = departurePlannedTime;
		this.departureActualTime = departureActualTime;
		this.departureDelay = departureDelay;
		this.track = track;
		this.operatorCode = operatorCode;
		this.categoryCode = categoryCode;
		this.messages = messages;
		this.status = status;
	}

	public String getOrigin() {
		return origin;
	}

	public String getRouteStations() {
		return routeStations;
	}

	public String getDestination() {
		return destination;
	}

	public String getTripNumber() {
		return tripNumber;
	}

	public ZonedDateTime getArrivalPlannedTime() {
		return arrivalPlannedTime;
	}

	public ZonedDateTime getArrivalActualTime() {
		return arrivalActualTime;
	}

	public Duration getArrivalDelay() {
		return arrivalDelay;
	}

	public ZonedDateTime getDeparturePlannedTime() {
		return departurePlannedTime;
	}

	public ZonedDateTime getDepartureActualTime() {
		return departureActualTime;
	}

	public Duration getDepartureDelay() {
		return departureDelay;
	}

	public String getTrack() {
		return track;
	}

	public String getOperatorCode() {
		return operatorCode;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public List<Object> getMessages() {
		return messages;
	}

	public String getStatus() {
		return status;
	}

	public void setStopsAt(ArrayList<String> stopsAt) {
		this.stopsAt = stopsAt;
	}

	public List<String> getStopsAt() {
		return stopsAt;
	}

	public void setStock(ArrayList<Integer> stock) {
		this.stock = stock;
	}

	public List<Integer> getStock() {
		return stock;
	}
}