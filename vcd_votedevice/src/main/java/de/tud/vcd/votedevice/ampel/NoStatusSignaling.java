package de.tud.vcd.votedevice.ampel;

/**
 * Diese Klasse dient dazu die Signalisierung ganz auszuschalten. Damit die Funktionsaufrufe dennoch 
 * erfolgen können werden sie an diese Klasse weitergeleitet. Hier geschieht einfach gar nichts.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class NoStatusSignaling implements StatusSignaling {

	public NoStatusSignaling() {
		
	}

	public void setRed() {
		

	}

	public void setGreen() {
		

	}

	public void setOrange() {
		

	}

	public void setInit() {
		

	}

	public boolean unlockAllowed() {
		
		return true;
	}

	

}
