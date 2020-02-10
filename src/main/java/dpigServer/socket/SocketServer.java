package dpigServer.socket;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import dpigServer.model.Person;
import dpigServer.model.rule.Rule;
import dpigServer.services.InsertDataService;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON, proxyMode = ScopedProxyMode.INTERFACES)
public class SocketServer implements Runnable, ISocketServer {
	
	private ServerSocket ss;
	private List<Socket> connections = new ArrayList<Socket>();
	
	@Autowired
	private InsertDataService insertDataService;

	@Override
	public void run() {
		Socket s;
		try {
	    	this.ss = new ServerSocket(insertDataService.getUtil().getSocketPort());
	    	while (true) {
				s = ss.accept();
	    		connections.add(s);
	    	}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void sendData(Person person, Rule rule) {
		try {
			for (Socket s: connections) {
				if (!s.isClosed()) {
					ObjectOutputStream output =  new ObjectOutputStream(s.getOutputStream());
					JSONObject dataJSON = new JSONObject();
			    	dataJSON.put("person", person.getJson());
			    	dataJSON.put("type", rule.getType());
			    	dataJSON.put("rule", rule.getJson());
			    	try{
			    		output.writeObject(dataJSON.toString());
			    	}catch(Exception e){
			    		try {
							Thread.sleep(4000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
			    		output.writeObject(dataJSON.toString());
			    	}
				}
				else {
					connections.remove(s);
				}
			}
		} catch (IOException e) {
			System.out.println("Error de conexión al puerto");
		}
		
		//Crear outputstream para enviar mensaje a través del socket
		//Con cada conexión que me almacenar el outputstream
		//En el sendMessage enviar el mensaje a todas los outputstream registrados
		//Detectar sockets cerrados y borrar el outputstream
	}
	
	
}
