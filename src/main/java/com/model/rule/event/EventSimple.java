package com.model.rule.event;

import java.io.Serializable;
import java.util.List;

import com.model.IPCamera;
import com.model.Match;

public class EventSimple extends Event implements Serializable{

	private static final long serialVersionUID = 7630199873467233523L;
	
	private int priority;
	private IPCamera ipCamera;
	
	public EventSimple(IPCamera ipCamera, String mensaje) {
		this.ipCamera = ipCamera;
		setMensaje(mensaje);
		this.priority = 1;
	}
	
	public EventSimple(IPCamera ipCamera) {
		this.ipCamera = ipCamera;
		this.priority = 1;
	}
	
	@Override
	public boolean isSuccesed(List<Match> personMatches) {
		if(personMatches.size()>0) {
			setDate(personMatches.get(0).getDate());
			Match ultimateMatch = personMatches.get(0);
			if(ultimateMatch.getIpCamera().getName().equals(ipCamera.getName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getPriority() {
		return this.priority;
	}
}
