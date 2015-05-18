package de.tud.vcd.votedevice;

import de.tud.vcd.votedevice.controller.VotingDeviceController;


/**
 * Zentrale Starterklasse. Startet das Projekt indem ein Controller erstellt wird und dieser den Befehl zur Anzeige bekommt.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class Starter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		VotingDeviceController vdc= new VotingDeviceController();
		vdc.showView();
	}
	
	
	

}
