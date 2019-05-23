package com.server.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ReadRules {
	
	private static final String FILEPATH="src/main/resources/rules.properties";
	private BufferedReader bf;
	
	public List<String> readRulesFile() throws FileNotFoundException, IOException {
		int totalRules = numberOfLines();
		List<String> rules = new ArrayList<String>();
		for (int i = 1; i <= totalRules; i++) {
			try (InputStream input = new FileInputStream(FILEPATH)) {
				Properties prop = new Properties();
	            prop.load(input);
	            String rule = prop.getProperty("rule"+i);
	            rules.add(rule);
			}
		}
		return rules;
	}
	
	private int numberOfLines() throws IOException {
		
		FileReader fr = new FileReader(FILEPATH);
		bf = new BufferedReader(fr);
		int lNumeroLineas = 0;
		while ((bf.readLine())!=null) {
		  lNumeroLineas++;
		}
		
		return lNumeroLineas;
	}
}
