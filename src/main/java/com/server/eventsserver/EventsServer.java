package com.server.eventsserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON, proxyMode = ScopedProxyMode.INTERFACES)
public class EventsServer implements Runnable, IEventsServer {

	@Override
	public void run() {
		try {
	    	ServerSocket ss = new ServerSocket(4999);
			do {
		    	Socket s = ss.accept();
		    	ObjectOutputStream data =  new ObjectOutputStream(s.getOutputStream());
				data.writeObject("Hola");
				
				
				System.out.println("Client conected");
			}
			while (true);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendMessage(String text) {
		
		//Crear outputstream para enviar mensaje a través del socket
		//Con cada conexión que me almacenar el outputstream
		//En el sendMessage enviar el mensaje a todas los outputstream registrados
		//Detectar sockets cerrados y borrar el outputstream
	}
}
