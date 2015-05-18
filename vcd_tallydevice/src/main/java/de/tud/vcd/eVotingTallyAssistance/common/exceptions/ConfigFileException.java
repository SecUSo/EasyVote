package de.tud.vcd.eVotingTallyAssistance.common.exceptions;

public class ConfigFileException extends VotingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConfigFileException() {
		
	}

	public ConfigFileException(String message) {
		super(message);
		
	}

	public ConfigFileException(Throwable cause) {
		super(cause);
		
	}

	public ConfigFileException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public ConfigFileException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
