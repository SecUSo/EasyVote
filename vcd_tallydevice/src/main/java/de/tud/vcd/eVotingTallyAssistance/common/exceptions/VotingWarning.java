package de.tud.vcd.eVotingTallyAssistance.common.exceptions;

public class VotingWarning extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public VotingWarning() {
		
	}

	public VotingWarning(String message) {
		super(message);
		
	}

	public VotingWarning(Throwable cause) {
		super(cause);
		
	}

	public VotingWarning(String message, Throwable cause) {
		super(message, cause);
		
	}

	public VotingWarning(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
