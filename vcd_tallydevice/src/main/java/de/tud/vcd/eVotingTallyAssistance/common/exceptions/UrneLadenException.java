package de.tud.vcd.eVotingTallyAssistance.common.exceptions;

public class UrneLadenException extends VotingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UrneLadenException() {
		
	}

	public UrneLadenException(String message) {
		super(message);
		
	}

	public UrneLadenException(Throwable cause) {
		super(cause);
		
	}

	public UrneLadenException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public UrneLadenException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
