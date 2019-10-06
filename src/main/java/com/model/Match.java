package com.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="matches")
public class Match implements Serializable {
	
	private static final long serialVersionUID = -3709323805785851011L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	private IPCamera ipCamera;
	
	@ManyToOne
	private Person person;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;
	
	public Match() {}
	
	public Match(IPCamera ipCamera, Person person, Date date) {
		this.ipCamera = ipCamera;
		this.person = person;
		this.date = date;
		//java.sql.Date date2 = new java.sql.Date(d.getTime());	
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public IPCamera getIpCamera() {
		return ipCamera;
	}

	public void setCamera(IPCamera ipCamera) {
		this.ipCamera = ipCamera;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	/*
	public JSONObject getJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("camera", this.camera.getJson());
		jsonObject.put("person", this.person.getJson());
		jsonObject.put("date", this.date.getTime());
		return jsonObject;
	}
	*/
}
