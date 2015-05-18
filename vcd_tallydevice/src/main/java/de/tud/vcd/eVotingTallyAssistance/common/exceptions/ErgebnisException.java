package de.tud.vcd.eVotingTallyAssistance.common.exceptions;

public class ErgebnisException extends VotingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ErgebnisException() {
		
	}

	public ErgebnisException(String message) {
		super(message);
		
	}

	public ErgebnisException(Throwable cause) {
		super(cause);
		
	}

	public ErgebnisException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public ErgebnisException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
