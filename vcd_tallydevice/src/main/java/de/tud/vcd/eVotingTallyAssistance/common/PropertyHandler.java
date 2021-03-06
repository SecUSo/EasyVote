/*******************************************************************************
 * #  Copyright 2015 SecUSo.org / Jurlind Budurushi / Roman J�ris
 * #
 * #  Licensed under the Apache License, Version 2.0 (the "License");
 * #  you may not use this file except in compliance with the License.
 * #  You may obtain a copy of the License at
 * #
 * #      http://www.apache.org/licenses/LICENSE-2.0
 * #
 * #  Unless required by applicable law or agreed to in writing, software
 * #  distributed under the License is distributed on an "AS IS" BASIS,
 * #  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * #  See the License for the specific language governing permissions and
 * #  limitations under the License.
 *******************************************************************************/
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
 * SC oder �hnlichem eingeladen werden, kann diese Klasse ach verschwinden.
 * 
 * @author Roman J�ris <roman.joeris@googlemail.com>
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
