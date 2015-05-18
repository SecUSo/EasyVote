package de.tud.vcd.eVotingTallyAssistance.common.exceptions;

public class WahlzettelDesignKeyNotFoundException extends VotingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WahlzettelDesignKeyNotFoundException() {
		
	}

	public WahlzettelDesignKeyNotFoundException(String message) {
		super(message);
		
	}

	public WahlzettelDesignKeyNotFoundException(Throwable cause) {
		super(cause);
		
	}

	public WahlzettelDesignKeyNotFoundException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public WahlzettelDesignKeyNotFoundException(String message,
			Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
