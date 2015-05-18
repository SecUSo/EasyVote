package de.tud.vcd.eVotingTallyAssistance.common.exceptions;

public class UrneWahlzettelOffen extends VotingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UrneWahlzettelOffen() {
		
	}

	public UrneWahlzettelOffen(String message) {
		super(message);
		
	}

	public UrneWahlzettelOffen(Throwable cause) {
		super(cause);
		
	}

	public UrneWahlzettelOffen(String message, Throwable cause) {
		super(message, cause);
		
	}

	public UrneWahlzettelOffen(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
