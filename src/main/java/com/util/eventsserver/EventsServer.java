package com.util.eventsserver;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.model.Person;
import com.model.event.Event;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON, proxyMode = ScopedProxyMode.INTERFACES)
public class EventsServer implements Runnable, IEventsServer {
	
	private ServerSocket ss;
	
	@Override
	public void run() {
		try {
	    	this.ss = new ServerSocket(4999);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveData(Person person, Event event) {
		Socket s;
		try {
			s = ss.accept();
			ObjectOutputStream output =  new ObjectOutputStream(s.getOutputStream());
			JSONObject dataJSON = new JSONObject();
	    	dataJSON.put("person", person.getJson());
	    	dataJSON.put("event", event.getJson());
	    	output.writeObject(dataJSON.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		//Crear outputstream para enviar mensaje a través del socket
		//Con cada conexión que me almacenar el outputstream
		//En el sendMessage enviar el mensaje a todas los outputstream registrados
		//Detectar sockets cerrados y borrar el outputstream
	}
}
