package de.tud.vcd.eVotingTallyAssistance.common.exceptions;

public class ConfigFileNotFoundException extends ConfigFileException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConfigFileNotFoundException() {
		
	}

	public ConfigFileNotFoundException(String message) {
		super(message);
		
	}

	public ConfigFileNotFoundException(Throwable cause) {
		super(cause);
		
	}

	public ConfigFileNotFoundException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public ConfigFileNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
