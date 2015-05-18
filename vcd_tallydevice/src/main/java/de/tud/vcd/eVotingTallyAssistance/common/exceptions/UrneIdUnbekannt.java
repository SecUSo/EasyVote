package de.tud.vcd.eVotingTallyAssistance.common.exceptions;

public class UrneIdUnbekannt extends VotingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UrneIdUnbekannt() {
		super();

	}

	public UrneIdUnbekannt(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UrneIdUnbekannt(String message, Throwable cause) {
		super(message, cause);
	}

	public UrneIdUnbekannt(String message) {
		super(message);
	}

	public UrneIdUnbekannt(Throwable cause) {
		super(cause);
	}

}
