package dpigServer.model.rule.event;

import java.io.Serializable;
import java.util.List;

import dpigServer.model.Match;

public class EventComplex extends Event implements Serializable{

	private static final long serialVersionUID = 6161696925923575356L;

	//private int priority;
	private Event event1, event2;
	
	public EventComplex(Event event1, Event event2, String action, int hall) {
		this.event1 = event1;
		this.event2 = event2;
		setAction(action);
		setHall(hall);
		//this.priority = this.event1.getPriority()+this.event2.getPriority();
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
	public boolean isAccomplished(List<Match> personMatches) {
		if(this.event1.isAccomplished(personMatches.subList(1, personMatches.size())) &&
		   this.event2.isAccomplished(personMatches.subList(0, personMatches.size()))){
			setAccomplished(this.event2.getAccomplishedDate());
			return true;
		}
		return false;
	}

}
