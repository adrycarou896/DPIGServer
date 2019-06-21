package com.server.eventsserver;

public interface IEventsServer extends Runnable {
	public void sendMessage(String text);
}
