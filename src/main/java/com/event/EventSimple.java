package com.event;

import java.util.Date;
import java.util.List;

import com.server.model.Camera;
import com.server.model.Match;

public class EventSimple implements Event{
	
	private int priority;
	private Camera camera;
	private String mensaje;
	private Date date;
	
	public EventSimple(Camera camera, String mensaje) {
		this.camera = camera;
		this.mensaje = mensaje;
		this.priority = 1;
	}
	
	public EventSimple(Camera camera) {
		this.camera = camera;
		this.priority = 1;
	}
	
	@Override
	public boolean isSuccesed(List<Match> personMatches) {
		if(personMatches.size()>0) {
			this.date = personMatches.get(0).getDate();
			Match ultimateMatch = personMatches.get(0);
			if(ultimateMatch.getCamera().getName().equals(camera.getName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getPriority() {
		return this.priority;
	}

	@Override
	public String getMensaje() {
		return this.mensaje;
	}

	@Override
	public Date getDate() {
		return this.date;
	}
	
	@Override
	public String toString() {
		return mensaje;
	}

}
