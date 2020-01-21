package com.model.rule.event;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import com.model.Match;
import com.model.rule.Rule;

public abstract class Event implements Rule{
	
	private String name;
	private String mensaje;
	private Date date;
	
	public abstract boolean isSuccesed(List<Match> personMatches);
	public abstract int getPriority();
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name=name;
	}
	
	public String getMensaje(){
		return this.mensaje;
	}
	
	public void setMensaje(String mensaje){
		this.mensaje = mensaje;
	}
	
	@Override
	public Date getDate() {
		return this.date;
	}
	
	@Override
	public void setDate(Date date){
		this.date = date;
	}
	
	@Override
	public String getType() {
		return "event";
	}
	
	@Override
	public JSONObject getJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("mensaje", getMensaje());
		jsonObject.put("date", date);
		return jsonObject;
	}
	
	@Override
	public String toString() {
		return getMensaje();
	}

}
