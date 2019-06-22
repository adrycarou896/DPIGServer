package com.event;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import com.server.model.Match;

public interface Event{
	public boolean isSuccesed(List<Match> personMatches);
	public int getPriority();
	public String getMensaje();
	public Date getDate();
	public String toString();
	public JSONObject getJson();
}
