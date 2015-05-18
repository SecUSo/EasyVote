package de.tud.vcd.votedevice.ampel;

public interface StatusSignaling {

	/**
	 * Setzt den Status der Signalisierung auf Rot
	 */
	public abstract void setRed();

	/**
	 * Setzt den Status der Signalisierung auf Grün
	 */
	public abstract void setGreen();

	/**
	 * Setzt den Status der Signalisierung auf Orange/Gelb
	 */
	public abstract void setOrange();
	
	/**
	 * 	Setzt den Status der Signalisierung auf den INitialzustand
	 */
	public abstract void setInit();
	
	//public abstract boolean unlockAllowed();
	

}