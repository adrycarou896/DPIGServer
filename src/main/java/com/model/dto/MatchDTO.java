package com.model.dto;

import java.io.Serializable;
import java.util.Date;

public class MatchDTO implements Serializable {
	
	private static final long serialVersionUID = -3709323805785851011L;
	private CameraDTO camera;
	private PersonDTO person;
	private Date date;
	
	public MatchDTO() {}
	
	public MatchDTO(CameraDTO camera, PersonDTO person, Date date) {
		this.camera = camera;
		this.person = person;
		this.date = date;
		
		//java.sql.Date date2 = new java.sql.Date(d.getTime());
		
	}

	public PersonDTO getPerson() {
		return person;
	}

	public void setPerson(PersonDTO person) {
		this.person = person;
	}

	public CameraDTO getCamera() {
		return camera;
	}

	public void setCamera(CameraDTO camera) {
		this.camera = camera;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
}
