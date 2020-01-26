package dpigServer.socket;

import dpigServer.model.Person;
import dpigServer.model.rule.Rule;

public interface ISocketServer extends Runnable {
	public void sendData(Person person, Rule event);
}
