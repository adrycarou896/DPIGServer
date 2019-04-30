package com.server.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Match implements Serializable {
	
	private static final long serialVersionUID = -3709323805785851011L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(cascade=CascadeType.ALL)
	private Camera camera;
	@ManyToOne(cascade=CascadeType.ALL)
	private Person person;
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;
	
	public Match() {}
	
	public Match(Camera camera, Person person, Date date) {
		this.camera = camera;
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

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
}
