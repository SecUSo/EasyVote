package de.tud.vcd.eVotingTallyAssistance.common.exceptions;

public class CandidateNotFoundException extends VotingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CandidateNotFoundException() {
		
	}

	public CandidateNotFoundException(String message) {
		super(message);
		
	}

	public CandidateNotFoundException(Throwable cause) {
		super(cause);
		
	}

	public CandidateNotFoundException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public CandidateNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
