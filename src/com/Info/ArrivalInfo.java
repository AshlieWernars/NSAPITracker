package com.Info;

import java.util.List;

public class ArrivalInfo {

	private final String origin;
	private final String trainNumber;
	private final String plannedDateTime;
	private final String actualDateTime;
	private final String plannedTrack;
	private final String actualTrack;

	private final String number;
	private final String categoryCode;
	private final String shortCategoryName;
	private final String longCategoryName;
	private final String operatorName;
	private final String operatorCode;
	private final String type;

	private final String trainCategory;
	private final boolean cancelled;

	private final List<Object> messagesList;

	private final String arrivalStatus;

	public ArrivalInfo(String origin, String trainNumber, String plannedDateTime, String actualDateTime, String plannedTrack, String actualTrack, String number, String categoryCode, String shortCategoryName, String longCategoryName, String operatorName, String operatorCode, String type, String trainCategory, boolean cancelled, List<Object> messagesList, String arrivalStatus) {
		this.origin = origin;
		this.trainNumber = trainNumber;
		this.plannedDateTime = plannedDateTime;
		this.actualDateTime = actualDateTime;
		this.plannedTrack = plannedTrack;
		this.actualTrack = actualTrack;
		this.number = number;
		this.categoryCode = categoryCode;
		this.shortCategoryName = shortCategoryName;
		this.longCategoryName = longCategoryName;
		this.operatorName = operatorName;
		this.operatorCode = operatorCode;
		this.type = type;
		this.trainCategory = trainCategory;
		this.cancelled = cancelled;
		this.messagesList = messagesList;
		this.arrivalStatus = arrivalStatus;
	}

	public String getOrigin() {
		return origin;
	}

	public String getTrainNumber() {
		return trainNumber;
	}

	public String getPlannedDateTime() {
		return plannedDateTime;
	}

	public String getActualDateTime() {
		return actualDateTime;
	}

	public String getPlannedTrack() {
		return plannedTrack;
	}

	public String getActualTrack() {
		return actualTrack;
	}

	public String getNumber() {
		return number;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public String getShortCategoryName() {
		return shortCategoryName;
	}

	public String getLongCategoryName() {
		return longCategoryName;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public String getOperatorCode() {
		return operatorCode;
	}

	public String getType() {
		return type;
	}

	public String getTrainCategory() {
		return trainCategory;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public List<Object> getMessagesList() {
		return messagesList;
	}

	public String getArrivalStatus() {
		return arrivalStatus;
	}
}