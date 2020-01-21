package com.model.rule.event;

import java.io.Serializable;
import java.util.List;

import com.model.Match;

public class EventComplex extends Event implements Serializable{

	private static final long serialVersionUID = 6161696925923575356L;

	private int priority;
	private Event event1, event2;
	
	public EventComplex(Event event1, Event event2, String mensaje) {
		this.event1 = event1;
		this.event2 = event2;
		setMensaje(mensaje);
		this.priority = this.event1.getPriority()+this.event2.getPriority();
	}

	public Event getEvent1() {
		return event1;
	}

	public void setEvent1(Event event1) {
		this.event1 = event1;
	}

	public Event getEvent2() {
		return event2;
	}

	public void setEvent2(Event event2) {
		this.event2 = event2;
	}

	@Override
	public boolean isSuccesed(List<Match> personMatches) {
		if(this.event1.isSuccesed(personMatches.subList(1, personMatches.size())) &&
		   this.event2.isSuccesed(personMatches.subList(0, personMatches.size()))){
			setDate(this.event2.getDate());
			return true;
		}
		return false;
	}
	
	@Override
	public int getPriority() {
		return this.priority;
	}

}
