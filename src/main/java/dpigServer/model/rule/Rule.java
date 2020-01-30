package dpigServer.model.rule;

import org.json.JSONObject;

public interface Rule {
	
	public String getName();
	public void setName(String name);
	public String getType();
	public String toString();
	public JSONObject getJson();
	
}
