package com.server.controller;

import java.util.Date;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.server.model.Camera;
import com.server.model.Match;
import com.server.model.Person;

@RestController
@RequestMapping(path = "/servicesREST/JR")//Rura en la que encontramos el servicio
public class RestService{

	@RequestMapping(method = RequestMethod.POST, path = "/validateUser", //dirección del servicio
			consumes = "application/json", produces = "application/json")
	public @ResponseBody Match //Convierte Camara a JSON
	validateUser(@RequestBody Match match) throws Exception {
		Camera camera = match.getCamera();
		Person person = match.getPerson();
		Date day = match.getDay();
		Date hour = match.getHour();
		
		//System.out.println("camera -> "+camera.getIdentificador()+", "+camera.getObservers().toString());
		System.out.println("person -> "+person.getIdentificador());
		System.out.println("day -> "+day);
		System.out.println("hour -> "+hour);
		return new Match(camera, person, day, hour);
	}
}
