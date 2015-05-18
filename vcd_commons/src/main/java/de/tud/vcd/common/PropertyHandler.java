package de.tud.vcd.common;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyHandler {

	Properties properties;
	
	public PropertyHandler(String propertyfile){
		properties = new Properties();
		BufferedInputStream stream;
		try {
			InputStream filename=getClass().getClassLoader().getResource(propertyfile).openStream();
			stream = new BufferedInputStream(filename);
			properties.loadFromXML(stream);
			stream.close();
			//
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	public String getProperty(String key, String defaultValue){
		return properties.getProperty(key, defaultValue);
		
	}
}
