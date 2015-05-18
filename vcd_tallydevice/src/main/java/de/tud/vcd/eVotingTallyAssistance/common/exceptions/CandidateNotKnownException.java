package de.tud.vcd.eVotingTallyAssistance.common.exceptions;

public class CandidateNotKnownException extends VotingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CandidateNotKnownException() {
		
	}

	public CandidateNotKnownException(String message) {
		super(message);
		
	}

	public CandidateNotKnownException(Throwable cause) {
		super(cause);
		
	}

	public CandidateNotKnownException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public CandidateNotKnownException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
