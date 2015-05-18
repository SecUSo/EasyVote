package de.tud.vcd.eVotingTallyAssistance.common.exceptions;

public class PartyNotExistsException extends VotingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PartyNotExistsException() {
		
	}

	public PartyNotExistsException(String message) {
		super(message);
		
	}

	public PartyNotExistsException(Throwable cause) {
		super(cause);
		
	}

	public PartyNotExistsException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public PartyNotExistsException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
