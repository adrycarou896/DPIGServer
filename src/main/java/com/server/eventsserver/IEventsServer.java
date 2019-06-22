package com.server.eventsserver;

import com.event.Event;
import com.server.model.Person;

public interface IEventsServer extends Runnable {
	public void saveData(Person person, Event event);
}
