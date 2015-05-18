package de.tud.vcd.eVotingTallyAssistance.common.exceptions;

public class LoginVotingException extends VotingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LoginVotingException() {
		
	}

	public LoginVotingException(String message) {
		super(message);
		
	}

	public LoginVotingException(Throwable cause) {
		super(cause);
		
	}

	public LoginVotingException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public LoginVotingException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
