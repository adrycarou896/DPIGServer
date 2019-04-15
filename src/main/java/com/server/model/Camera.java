package com.server.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Camera implements Serializable {
	
	 private static final long serialVersionUID = 3560972546182458142L;
	 private String identificador;
	 private List<Camera> observers;
	 
	 public Camera() {}
	 
	 public Camera(String identificador) {
		 this.identificador = identificador;
		 this.observers = new ArrayList<Camera>();
	 }
	 
	 public Camera(String identificador, List<Camera> observers) {
		 this.identificador = identificador;
		 this.observers = observers;
	 }

	public String getIdentificador() {
		return identificador;
	}

	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}

	public List<Camera> getObservers() {
		return observers;
	}

	public void setObservers(List<Camera> observers) {
		this.observers = observers;
	}
	 
	 
}
