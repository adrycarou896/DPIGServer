package dpigServer.model.rule.event;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import dpigServer.model.Match;
import dpigServer.model.rule.Rule;

public abstract class Event implements Rule{
	
	private String name;
	private String action;
	private int hall;
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

	public Date getAccomplishedDate() {
		return this.accomplishedDate;
	}
	
	public void setAccomplished(Date date){
		this.accomplishedDate = date;
	}
	
	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public int getHall() {
		return hall;
	}
	
	public void setHall(int hall) {
		this.hall = hall;
	}
	
	@Override
	public String getType() {
		return "event";
	}
	
	@Override
	public JSONObject getJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("action", action);
		jsonObject.put("hall", hall);
		jsonObject.put("accomplishedDate", accomplishedDate);
		return jsonObject;
	}

}
