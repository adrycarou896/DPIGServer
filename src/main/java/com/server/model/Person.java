package com.server.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Person implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7915536599168677189L;
	
	@Id
	private String identificador;
	
	public Person() {}
	
	public Person(String identificador) {
		this.identificador = identificador;
	}

	public String getIdentificador() {
		return identificador;
	}

	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}

}
