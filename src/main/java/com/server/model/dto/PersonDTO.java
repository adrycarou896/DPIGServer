package com.server.model.dto;
import java.io.Serializable;

public class PersonDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7915536599168677189L;
	
	private String name;
	
	public PersonDTO() {}
	
	public PersonDTO(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

