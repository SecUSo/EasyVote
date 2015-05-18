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
