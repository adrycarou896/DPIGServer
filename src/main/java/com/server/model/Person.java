package com.server.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.json.JSONObject;

@Entity
@Table(name="persons")
public class Person implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7915536599168677189L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique=true)
	private String name;
	
	public Person() {}
	
	public Person(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Long getId() {
		return this.id;
	}
	
	public JSONObject getJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("name", this.id);
		jsonObject.put("name", this.name);
		return jsonObject;
	}

}
