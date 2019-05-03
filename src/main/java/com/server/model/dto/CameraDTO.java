package com.server.model.dto;

import java.io.Serializable;

public class CameraDTO implements Serializable {
	
	 private static final long serialVersionUID = 3560972546182458142L;
	 private String name;
	 
	 public CameraDTO() {}
	 
	 public CameraDTO(String name) {
		 this.name = name;
	 }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

