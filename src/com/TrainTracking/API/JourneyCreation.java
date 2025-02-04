package com.TrainTracking.API;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

import com.Info.ArrivalInfo;
import com.Info.DepartureInfo;
import com.Info.JourneyInfo;

public class JourneyCreation {

	public static JourneyInfo createJourney(ArrivalInfo arrivalInfo, DepartureInfo departureInfo) {
		String origin = arrivalInfo.getOrigin();
		String routeStations = departureInfo.getRouteStations();
		String destination = departureInfo.getDirection();
		String tripNumber = arrivalInfo.getTrainNumber().replaceAll("  ", " ");

		ZonedDateTime arrivalPlannedTime = null;

		if (!arrivalInfo.getPlannedDateTime().equals("UNKOWN")) {
			arrivalPlannedTime = ZonedDateTime.parse(arrivalInfo.getPlannedDateTime());
		}

		ZonedDateTime arrivalActualTime = null;

		if (!arrivalInfo.getActualDateTime().equals("UNKOWN")) {
			arrivalActualTime = ZonedDateTime.parse(arrivalInfo.getActualDateTime());
		}

		Duration arrivalDelay = null;

		if (arrivalPlannedTime != null && arrivalActualTime != null) {
			arrivalDelay = Duration.between(arrivalPlannedTime, arrivalActualTime);
		}

		ZonedDateTime departurePlannedTime = null;

		if (!departureInfo.getPlannedDateTime().equals("UNKOWN")) {
			departurePlannedTime = ZonedDateTime.parse(departureInfo.getPlannedDateTime());
		}

		ZonedDateTime departureActualTime = null;

		if (!departureInfo.getActualDateTime().equals("UNKOWN")) {
			departureActualTime = ZonedDateTime.parse(departureInfo.getActualDateTime());
		}

		Duration departureDelay = null;

		if (departurePlannedTime != null && departureActualTime != null) {
			departureDelay = Duration.between(departurePlannedTime, departureActualTime);
		}

		String track = arrivalInfo.getActualTrack().equals(arrivalInfo.getPlannedTrack()) ? arrivalInfo.getActualTrack() : arrivalInfo.getPlannedTrack() + " -> " + arrivalInfo.getActualTrack();
		String operatorCode = arrivalInfo.getOperatorCode();
		String categoryCode = arrivalInfo.getCategoryCode().stripLeading();

		List<Object> messages = arrivalInfo.getMessagesList();

		String status = departureInfo.getDepartureStatus();

		return new JourneyInfo(origin, routeStations, destination, tripNumber, arrivalPlannedTime, arrivalActualTime, arrivalDelay, departurePlannedTime, departureActualTime, departureDelay, track, operatorCode, categoryCode, messages, status);
	}
}