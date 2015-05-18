package de.tud.vcd.eVotingTallyAssistance.common.exceptions;

public class ConfigFileEntryNotKnownException extends ConfigFileException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConfigFileEntryNotKnownException() {
		
	}

	public ConfigFileEntryNotKnownException(String message) {
		super(message);
		
	}

	public ConfigFileEntryNotKnownException(Throwable cause) {
		super(cause);
		
	}

	public ConfigFileEntryNotKnownException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public ConfigFileEntryNotKnownException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
