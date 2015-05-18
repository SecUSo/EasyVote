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

/**
 * Objekt für einen Logeintrag. Es kann nur gelesen aber nicht mehr geändert
 * werden und wird am Ende mit ausgegeben.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 */
public class VotingLogEntry {

	public enum LogLevel {
		INFO, WARNING, ERROR
	};

	private LogLevel level;
	private String msg;

	/**
	 * Erzeugt einen Logeintrag
	 * 
	 * @param level
	 *            : Stufe des Loglevels aus Enum
	 * @param msg
	 *            : die individuelle Botschaft
	 */
	public VotingLogEntry(LogLevel level, String msg) {
		super();
		this.level = level;
		this.msg = msg;
	}

	/**
	 * @return the Logging-level
	 */
	public LogLevel getLevel() {
		return level;
	}

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + level.name() + "] " + msg;
	}

}
