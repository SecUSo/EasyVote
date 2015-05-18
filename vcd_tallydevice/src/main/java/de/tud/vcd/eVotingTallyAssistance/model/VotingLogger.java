/*******************************************************************************
 * #  Copyright 2015 SecUSo.org / Jurlind Budurushi / Roman Jöris
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
package de.tud.vcd.eVotingTallyAssistance.model;

import java.util.ArrayList;

import de.tud.vcd.eVotingTallyAssistance.model.VotingLogEntry.LogLevel;


public class VotingLogger {

	private ArrayList<VotingLogEntry> logs;

	private static VotingLogger instance = null;

	/**
	 * liefert die Instanz des Singleton Pattern. So wird das Objekt nur einmal
	 * angelegt und verfügt über das Wissen.
	 * 
	 * @return
	 */
	public static synchronized VotingLogger getInstance() {
		if (instance == null) {
			instance = new VotingLogger();
		}
		return instance;
	}

	/**
	 * Konstruktor legt die ArrayList an, wenn das Objekt noch nicht vorhanden
	 * war.
	 * 
	 */
	public VotingLogger() {
		logs = new ArrayList<VotingLogEntry>();
	}

	/**
	 * fügt einen Logeintrag der Liste hinzu. Mit Level und Text
	 * 
	 * @param level
	 *            : Logginglevel der Botschaft
	 * @param msg
	 *            : die botschaft selber
	 */
	public void log(LogLevel level, String msg) {
		logs.add(new VotingLogEntry(level, msg));
	}

	/**
	 * leert die Liste komplett. Ist nur beim Start sinnvoll, wenn die Wahlurne
	 * initialisiert wird, um auch einen leeren Log zu haben.
	 */
	public void flush() {
		logs.clear();
	}

	/**
	 * fügt einen Logeintrag der Liste hinzu nur mit Text.
	 * 
	 * @param msg
	 */
	public void log(String msg) {
		logs.add(new VotingLogEntry(LogLevel.INFO, msg));
	}

	/**
	 * Gibt die LoggingInformationen aus. Aber nur bis zu einem bestimmten
	 * Logginglevel. Also kleineren werden nicht ausgegeben.
	 * 
	 * @param level
	 *            : Logginglevel welches als Ausgabeschwelle dient.
	 * @return ArrayList of VotingLogEntries
	 */
	public ArrayList<VotingLogEntry> getVotingLogEntries(LogLevel level) {
		ArrayList<VotingLogEntry> res = new ArrayList<VotingLogEntry>();
		for (VotingLogEntry vle : logs) {
			if (vle.getLevel().ordinal() >= level.ordinal()) {
				res.add(vle);
			}
		}
		return res;
	}

	/**
	 * Gibt die LoggingInformationen als STRING_ARRAY aus. Aber nur bis zu einem
	 * bestimmten Logginglevel. Also kleineren werden nicht ausgegeben.
	 * 
	 * @param level
	 *            : Logginglevel welches als Ausgabeschwelle dient.
	 * @return ArrayList of Strings
	 */
	public ArrayList<String> toStringArray(LogLevel level) {
		ArrayList<String> res = new ArrayList<String>();
		for (VotingLogEntry vle : logs) {
			if (vle.getLevel().ordinal() >= level.ordinal()) {
				res.add(vle.toString());
			}
		}
		return res;
	}

}
