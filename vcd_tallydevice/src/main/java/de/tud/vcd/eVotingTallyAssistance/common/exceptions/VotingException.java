/**
 * 
 */
package de.tud.vcd.eVotingTallyAssistance.common.exceptions;

/**
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public  class VotingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public VotingException() {
		
	}

	/**
	 * @param message
	 */
	public VotingException(String message) {
		super(message);
		
		
	}

	/**
	 * @param cause
	 */
	public VotingException(Throwable cause) {
		super(cause);
		
	}

	/**
	 * @param message
	 * @param cause
	 */
	public VotingException(String message, Throwable cause) {
		super(message, cause);
		
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public VotingException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
