package dpigServer.model.rule;

import java.util.Date;

import org.json.JSONObject;

public interface Rule {
	
	public String getName();
	public void setName(String name);
	public Date getDate();
	public void setDate(Date date);
	public String getType();
	public String toString();
	public JSONObject getJson();
	
}
