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
package de.tud.vcd.eVotingTallyAssistance.gui.tallyGui;

import java.awt.event.ActionListener;

import javax.swing.JButton;

import de.tud.vcd.eVotingTallyAssistance.controller.ControllerCalls;


/**
 * Erzeugt ein Kontrollfeld, um einen Wahlzettel zu editieren, die �nderungen zu
 * best�tigen oder zu verwerfen.
 * 
 * @author Roman J�ris <roman.joeris@googlemail.com>
 * 
 */
public class ControlPaneEdit extends ControlPane {

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
	public ControlPaneEdit(ActionListener al, String name) {
		super(name);
		JButton button = new JButton("Wahlzettel editieren...");
		button.setActionCommand(ControllerCalls.Calls.EDITBALLOT.name());
		button.addActionListener(al);
		button.setToolTipText("�ber diese Schaltfl�che l��t sich der momentan angezeigte Wahlzettel korrigieren.");
		addNewComponent("edit", button);

		button = new JButton("�nderungen speichern");
		button.setActionCommand(ControllerCalls.Calls.SUBMITCHANGES.name());
		button.addActionListener(al);
		button.setToolTipText("�bernimmt die gemachten �nderungen und zeigt diese an. Der Wahlzettel ist damit noch nicht gespeichert.");

		addNewComponent("submit", button);

		button = new JButton("�nderungen verwerfen");
		button.setActionCommand(ControllerCalls.Calls.DISCARDCHANGES.name());
		button.setToolTipText("Verwirft die �nderungen und zeigt wieder den Originalwahlzettel an.");
		button.addActionListener(al);
		addNewComponent("discard", button);

	}

}
