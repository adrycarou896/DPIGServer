package com.event;

import java.util.List;

import com.server.model.Match;

public interface Event {
	public boolean isSuccesed(List<Match> personMatches);
	public int getPriority();
	public String getMensaje();
}
