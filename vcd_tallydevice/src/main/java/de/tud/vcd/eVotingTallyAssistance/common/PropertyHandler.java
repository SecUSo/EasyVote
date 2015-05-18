package de.tud.vcd.eVotingTallyAssistance.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Dient nur dem Laden der Wahlleiter aus der ini-Datei. Wenn die Wahlleiter per
 * SC oder ähnlichem eingeladen werden, kann diese Klasse ach verschwinden.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 */
public class PropertyHandler {

	public PropertyHandler() {
		super();

	}

	public static String setProperty(String filename, String id, String value)
			throws FileNotFoundException, IOException {
		Properties props = new Properties();
		props.load(new FileInputStream(filename));
		props.setProperty(id, value);
		props.store(new FileOutputStream(filename, true), "");

		return "";
	}

	public static String getProperty(String filename, String eigenschaft)
			throws FileNotFoundException, IOException {
		Properties props = new Properties();
		props.load(new FileInputStream(filename));
		return props.getProperty(eigenschaft, "");
	}

	public static ArrayList<String> getProperties(String filename)
			throws FileNotFoundException, IOException {
		Properties props = new Properties();
		ArrayList<String> wahlleiter = new ArrayList<String>();

		props.load(new FileInputStream(filename));
		Enumeration<?> propertyNames = props.propertyNames();
		while (propertyNames.hasMoreElements()) {
			wahlleiter.add((String) propertyNames.nextElement());
		}

		return wahlleiter;
	}

}
