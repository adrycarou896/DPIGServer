package com.socket;

import com.model.Person;
import com.model.rule.Rule;

public interface ISocketServer extends Runnable {
	public void sendData(Person person, Rule event);
}
