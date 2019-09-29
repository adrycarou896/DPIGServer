package com.model.alert;

import java.util.Date;

public class Alert {
	
	private String name;
	private String eventName;
	private String operator;
	private Date date;
	
	public Alert(String name, String eventName, String operator, Date date) {
		this.name=name;
		this.eventName=eventName;
		this.operator=operator;
		this.date=date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	
}
