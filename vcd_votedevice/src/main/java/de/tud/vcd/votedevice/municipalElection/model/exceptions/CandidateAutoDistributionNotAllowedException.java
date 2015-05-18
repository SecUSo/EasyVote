package de.tud.vcd.votedevice.municipalElection.model.exceptions;

public class CandidateAutoDistributionNotAllowedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CandidateAutoDistributionNotAllowedException() {
		
	}

	public CandidateAutoDistributionNotAllowedException(String message) {
		super(message);
		
	}

	public CandidateAutoDistributionNotAllowedException(Throwable cause) {
		super(cause);
		
	}

	public CandidateAutoDistributionNotAllowedException(String message,
			Throwable cause) {
		super(message, cause);
		
	}

	public CandidateAutoDistributionNotAllowedException(String message,
			Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
