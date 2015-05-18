package de.tud.vcd.eVotingTallyAssistance.common.exceptions;

public class WahlhelferException extends VotingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WahlhelferException() {
		
	}

	public WahlhelferException(String message) {
		super(message);
		
	}

	public WahlhelferException(Throwable cause) {
		super(cause);
		
	}

	public WahlhelferException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public WahlhelferException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
