package de.tud.vcd.eVotingTallyAssistance.common.exceptions;

public class WahlleiterNichtKorrektException extends VotingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WahlleiterNichtKorrektException() {
		
	}

	public WahlleiterNichtKorrektException(String message) {
		super(message);
		
	}

	public WahlleiterNichtKorrektException(Throwable cause) {
		super(cause);
		
	}

	public WahlleiterNichtKorrektException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public WahlleiterNichtKorrektException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
