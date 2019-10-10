package com.util.eventsserver;

import com.model.Person;
import com.model.event.Event;

public interface IEventsServer extends Runnable {
	public void sendData(Person person, Event event);
}
