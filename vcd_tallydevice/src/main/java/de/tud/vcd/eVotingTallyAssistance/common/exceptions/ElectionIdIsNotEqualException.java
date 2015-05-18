package de.tud.vcd.eVotingTallyAssistance.common.exceptions;

public class ElectionIdIsNotEqualException extends VotingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ElectionIdIsNotEqualException() {
		
	}

	public ElectionIdIsNotEqualException(String message) {
		super(message);
		
	}

	public ElectionIdIsNotEqualException(Throwable cause) {
		super(cause);
		
	}

	public ElectionIdIsNotEqualException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public ElectionIdIsNotEqualException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
