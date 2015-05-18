package de.tud.vcd.eVotingTallyAssistance.common.exceptions;

public class UrneDoppelteIdGefunden extends VotingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UrneDoppelteIdGefunden() {

	}

	public UrneDoppelteIdGefunden(String message) {
		super(message);
	}

	public UrneDoppelteIdGefunden(Throwable cause) {
		super(cause);
	}

	public UrneDoppelteIdGefunden(String message, Throwable cause) {
		super(message, cause);
	}

	public UrneDoppelteIdGefunden(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
