package dpigServer.model.rule.event;

import java.io.Serializable;
import java.util.List;

import dpigServer.model.IPCamera;
import dpigServer.model.Match;

public class EventSimple extends Event implements Serializable{

	private static final long serialVersionUID = 7630199873467233523L;
	
	private int priority;
	private IPCamera ipCamera;
	
	public EventSimple(IPCamera ipCamera, String mensaje) {
		this.ipCamera = ipCamera;
		setMessage(mensaje);
		this.priority = 1;
	}
	
	public EventSimple(IPCamera ipCamera) {
		this.ipCamera = ipCamera;
		this.priority = 1;
	}
	
	@Override
	public boolean isAccomplished(List<Match> personMatches) {
		if(personMatches.size()>0) {
			setDate(personMatches.get(0).getDate());
			Match ultimateMatch = personMatches.get(0);
			if(ultimateMatch.getIpCamera().getName().equals(ipCamera.getName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getPriority() {
		return this.priority;
	}
}
