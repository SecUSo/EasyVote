package de.tud.vcd.eVotingTallyAssistance.common.exceptions;

public class UrneSpeichernException extends VotingException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UrneSpeichernException() {
		
	}

	public UrneSpeichernException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

	public UrneSpeichernException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public UrneSpeichernException(String message) {
		super(message);
		
	}

	public UrneSpeichernException(Throwable cause) {
		super(cause);
		
	}

}
