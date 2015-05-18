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
package de.tud.vcd.eVotingTallyAssistance.gui.tallyGui;

import java.awt.event.ActionListener;

import javax.swing.JButton;

import de.tud.vcd.eVotingTallyAssistance.controller.ControllerCalls;


/**
 * Erzeugt ein Kontrollfeld, um einen Wahlzettel zu bestätigen oder zu verwerfen
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 */
public class ControlPaneSubmit extends ControlPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Erzeugt das Feld mit dem übergebenen Namen als Überschrift und einem
	 * ActionListener, der bei den Kindern registriert wird.
	 * 
	 * @param al
	 * @param name
	 */
	public ControlPaneSubmit(ActionListener al, String name) {
		super(name);
		JButton button = new JButton("Wahlzettel speichern");
		button.setActionCommand(ControllerCalls.Calls.SUBMITBALLOT.name());
		button.addActionListener(al);
		button.setToolTipText("Legt den Wahlzettel in die Urne und aktualisiert die Ansicht auf dem Ergebnismonitor.");

		addNewComponent("submit", button);

		button = new JButton("Wahlzettel verwerfen");
		button.setActionCommand(ControllerCalls.Calls.DISCARDBALLOT.name());
		button.addActionListener(al);
		button.setToolTipText("Der Wahlzettel wird verworfen und nicht gespeichert.");

		addNewComponent("cancel", button);

	}

}
