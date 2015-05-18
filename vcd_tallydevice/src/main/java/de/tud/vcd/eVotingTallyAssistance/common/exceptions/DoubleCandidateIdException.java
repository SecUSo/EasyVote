package de.tud.vcd.eVotingTallyAssistance.common.exceptions;

public class DoubleCandidateIdException extends VotingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DoubleCandidateIdException() {
		
	}

	public DoubleCandidateIdException(String message) {
		super(message);
		
	}

	public DoubleCandidateIdException(Throwable cause) {
		super(cause);
		
	}

	public DoubleCandidateIdException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public DoubleCandidateIdException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
