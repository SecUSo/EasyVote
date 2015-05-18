package de.tud.vcd.votedevice.multielection;

import de.tud.vcd.votedevice.ampel.StatusSignaling;
import de.tud.vcd.votedevice.controller.VotingDeviceController;

/**
 * Klasse nimmt sowohl Model View und Controller auf. Die erstmal abstrakt definiert sind, aber hierüber
 * zu einer Einheit verbunden werden. Ermöglcht es somit verschiedene Wahlen einzubinden
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public abstract class ElectionContainer {
	public VCDListener l;
	public VCDModel m;
	public VCDView v;
	
	VotingDeviceController vdc;
	
	/**
	 * Erzeugt einen Container, der einen Verweis auf den zentralen Controller hat.
	 * @param vdc
	 * @throws Exception
	 */
	public ElectionContainer(VotingDeviceController vdc) throws Exception {
		this.vdc=vdc;
	}
	
	/**
	 * Wahl ist beendet und Gerät wird gesperrt
	 */
	public void finished(){
		vdc.setGesperrt(true);
	}
	
	/**
	 * liefert die Statussignalisierung zurück. Dies kann ein Dummy oder die Kommunikation mit der Freischaltkomponente sein.
	 * @return
	 */
	public StatusSignaling getStatusSignaling(){
		return this.vdc.get_statusSignaling();
	}

	/**
	 * @return the l
	 */
	public VCDListener getListener() {
		return l;
	}

	/**
	 * @return the m
	 */
	public VCDModel getModel() {
		return m;
	}

	/**
	 * @return the v
	 */
	public VCDView getView() {
		return v;
	}

	
}
