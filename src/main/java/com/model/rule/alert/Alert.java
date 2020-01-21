package com.model.rule.alert;

import java.util.Date;

import org.json.JSONObject;

import com.model.rule.Rule;
import com.model.rule.event.Event;

public class Alert implements Rule{
	
	private String name;
	private Event event;
	private String operator;
	private Date date;
	
	public Alert(String name, Event event, String operator, Date date) {
		this.name = name;
		this.event=event;
		this.operator=operator;
		this.date = date;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	public JSONObject getJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("event", event);
		jsonObject.put("operator", operator);
		jsonObject.put("date", getDate());
		return jsonObject;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
		
	}

	@Override
	public Date getDate() {
		return this.date;
	}

	@Override
	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String getType() {
		return "alert";
	}
	
	
}
