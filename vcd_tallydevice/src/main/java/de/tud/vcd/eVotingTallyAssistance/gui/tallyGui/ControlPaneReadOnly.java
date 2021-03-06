/*******************************************************************************
 * #  Copyright 2015 SecUSo.org / Jurlind Budurushi / Roman J�ris
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
/**
 * 
 */
package de.tud.vcd.eVotingTallyAssistance.gui.tallyGui;

import java.awt.event.ActionListener;

import javax.swing.JButton;

import de.tud.vcd.eVotingTallyAssistance.controller.ControllerCalls;


/**
 * Erzeugt ein Kontrollfeld, um eine Wahlurne oder einen Wahlzettel zu laden,
 * oder das Programm zu beenden. Diese Befehlsgruppe ist f�r den ReadOnly
 * Bereich gedacht, wenn nur angezeigt werden kann.
 * 
 * @author Roman J�ris <roman.joeris@googlemail.com>
 * 
 */
public class ControlPaneReadOnly extends ControlPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Erzeugt das Feld mit dem �bergebenen Namen als �berschrift und einem
	 * ActionListener, der bei den Kindern registriert wird.
	 * 
	 * @param al
	 * @param name
	 */
	public ControlPaneReadOnly(ActionListener al, String name) {
		super(name);

		JButton loadUrne = new JButton("Wahlurne laden...");
		loadUrne.setActionCommand(ControllerCalls.Calls.LOADWAHLURNE.name());
		loadUrne.setToolTipText("�ber diese Schaltfl�che l��t sich eine gespeicherte Wahlurne wieder laden. Ein Editieren ist nicht m�glich.");

		loadUrne.addActionListener(al);
		addNewComponent("loadUrne", loadUrne);

		JButton loadButton = new JButton("Wahlzettel laden...");
		loadButton.setActionCommand(ControllerCalls.Calls.LOADBALLOT.name());
		loadButton
				.setToolTipText("Ein Wahlzettel aus der Urne wird geladen. Ein Editieren ist nicht m�glich.");

		loadButton.addActionListener(al);
		addNewComponent("load", loadButton);

		JButton button = new JButton("Programm beenden...");
		button.setActionCommand(ControllerCalls.Calls.CLOSEPROGRAMDIRECT.name());
		button.addActionListener(al);
		button.setToolTipText("Das Programm wird direkt beendet.");

		addNewComponent("close", button);
	}

}
