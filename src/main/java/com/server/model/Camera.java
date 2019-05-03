package com.server.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="cameras")
public class Camera implements Serializable {
	
	 private static final long serialVersionUID = 3560972546182458142L;
	 
	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long id;
	 
	 private String name;
	 
	 @ManyToMany
	 private List<Camera> observers;
	 
	 public Camera() {}
	 
	 public Camera(String name) {
		 this.name = name;
	 }
	 
	 public Camera(String name, List<Camera> observers) {
		 this.name = name;
		 this.observers = observers;
	 }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	 
	 
}
