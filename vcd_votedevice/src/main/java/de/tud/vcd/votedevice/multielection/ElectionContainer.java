/*******************************************************************************
 * #  Copyright 2015 SecUSo.org / Jurlind Budurushi / Roman Jöris
 * #
 * #  Licensed under the Apache License, Version 2.0 (the "License");
 * #  you may not use this file except in compliance with the License.
 * #  You may obtain a copy of the License at
 * #
 * #      http://www.apache.org/licenses/LICENSE-2.0
 * #
 * #  Unless required by applicable law or agreed to in writing, software
 * #  distributed under the License is distributed on an "AS IS" BASIS,
 * #  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * #  See the License for the specific language governing permissions and
 * #  limitations under the License.
 *******************************************************************************/
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
