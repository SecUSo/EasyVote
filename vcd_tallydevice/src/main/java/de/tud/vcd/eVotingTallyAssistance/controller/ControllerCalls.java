package de.tud.vcd.eVotingTallyAssistance.controller;

/**
 * Definiert die erlaubten Befehle, die die GUI an den Controller schicken darf.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class ControllerCalls {

	public enum Calls {
	   LOADIMAGE, INVALID,LOADWAHLURNE, SCAN,BARCODE,LOADBALLOT, EDITBALLOT, SUBMITCHANGES, DISCARDCHANGES, SUBMITBALLOT, DISCARDBALLOT, LOCKUSER, CLOSEPROGRAM,CLOSEPROGRAMDIRECT, LOGIN 
	}
	
	/**
	 * Gibt den ENUM Wert des übergebenen Strings zurück. Wenn er nicht gefunden wird, wird er auf INVALID gesetzt.
	 * @param str String Name des Calls
	 * @return ENUM Calls der wirkliche ENUM Typ.
	 */
	public static Calls getValue(String str) {
		for (Calls c:  Calls.values()){
			if (str.equals(c.name())){
				return c;
			}
		}
		
		return Calls.INVALID;
	}

	
}
