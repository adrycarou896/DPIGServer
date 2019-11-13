package com.model;

import java.io.File;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="imagesFalsesPositives")
public class ImageFalsePositive implements Serializable{

	private static final long serialVersionUID = 5449820548075571268L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String deviceId;
	
	@Column
	private File image;
	
	public ImageFalsePositive(){}
	
	public ImageFalsePositive(String deviceId,File image){
		this.deviceId = deviceId;
		this.image = image;
	}
	
	public File getImage(){
		return this.image;
	}
}
