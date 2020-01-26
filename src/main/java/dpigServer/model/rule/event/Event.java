package dpigServer.model.rule.event;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import dpigServer.model.Match;
import dpigServer.model.rule.Rule;

public abstract class Event implements Rule{
	
	private String name;
	private String message;
	private Date accomplishedDate;
	
	public abstract boolean isAccomplished(List<Match> personMatches);
	public abstract int getPriority();
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name=name;
	}
	
	public String getMessage(){
		return this.message;
	}
	
	public void setMessage(String mensaje){
		this.message = mensaje;
	}
	
	@Override
	public Date getDate() {
		return this.accomplishedDate;
	}
	
	@Override
	public void setDate(Date date){
		this.accomplishedDate = date;
	}
	
	@Override
	public String getType() {
		return "event";
	}
	
	@Override
	public JSONObject getJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("message", getMessage());
		jsonObject.put("accomplishedDate", accomplishedDate);
		return jsonObject;
	}
	
	@Override
	public String toString() {
		return getMessage();
	}

}
