package com.server.model;

import java.io.Serializable;
import java.util.Date;

public class Match implements Serializable {
	
	private static final long serialVersionUID = -3709323805785851011L;
	private Camera camera;
	private Person person;
	private Date day;
	private Date hour;
	
	public Match() {}
	
	public Match(Camera camera, Person person, Date day, Date hour) {
		this.camera = camera;
		this.person = person;
		this.day = day;
		this.hour = hour;
		
		//java.sql.Date date2 = new java.sql.Date(d.getTime());
		
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public Date getDay() {
		return day;
	}

	public void setDay(Date day) {
		this.day = day;
	}

	public Date getHour() {
		return hour;
	}

	public void setHour(Date hour) {
		this.hour = hour;
	}
	
	
}
