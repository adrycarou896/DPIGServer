package com.event;

import java.util.List;

import com.server.model.Camera;
import com.server.model.Match;

public class EventSimple implements Event{
	
	private int priority = 1;
	private Camera camera;
	private String mensaje;
	
	public EventSimple(Camera camera, String mensaje) {
		this.camera = camera;
		this.mensaje = mensaje;
	}
	
	public EventSimple(Camera camera) {
		this.camera = camera;
	}
	
	@Override
	public  boolean isSuccesed(List<Match> personMatches, int index) {
		Match ultimateMatch = personMatches.get(priority-1);
		if(ultimateMatch.getCamera().getName().equals(camera.getName())) {
			if(mensaje!=null) {
				System.out.println(mensaje);
			}
			return true;
		}
		return false;
	}

	@Override
	public int getPriority() {
		return this.priority;
	}
}
