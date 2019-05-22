package com.event;

import java.util.List;

import com.server.model.Match;

public interface Event {
	public int getPriority();
	public boolean isSuccesed(List<Match> personMatches);
}
