package de.tud.vcd.eVotingTallyAssistance.common.exceptions;

public class OCRException extends VotingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OCRException() {
		
	}

	public OCRException(String message) {
		super(message);
		
	}

	public OCRException(Throwable cause) {
		super(cause);
		
	}

	public OCRException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public OCRException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
