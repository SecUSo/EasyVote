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
/**
 * 
 */


import java.awt.event.ActionListener;

import javax.swing.JButton;

import de.tud.vcd.eVotingTallyAssistance.controller.ControllerCalls;


/**
 * Erzeugt ein Kontrollfeld, um das Programm zu sperren oder zu beenden.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class ControlPaneLock extends ControlPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Erzeugt das Feld mit dem übergebenen Namen als Überschrift und einem ActionListener, der bei den Kindern registriert wird.
	 * @param al
	 * @param name
	 */
	public ControlPaneLock(ActionListener al,String name) {
		super(name);
		JButton button=new JButton("Programm sperren");
		button.setActionCommand(ControllerCalls.Calls.LOCKUSER.name());
		button.setToolTipText("Das Programm wird gesperrt, so dass es erst durch die erneute Eingabe der Kennwörter der beiden Wahlhelfer wieder bedient werden kann.");
		button.addActionListener(al);
		addNewComponent("lock", button);
		
		button=new JButton("Programm beenden...");
		button.setActionCommand(ControllerCalls.Calls.CLOSEPROGRAM.name());
		button.setToolTipText("Das Beenden wird eingeleitet. Hierzu wird ein Protokoll gedruckt, welches mit der Anzeige verglichen werden muss. Anschließend wird auch die Urne gespeichert.");
		button.addActionListener(al);
		addNewComponent("close", button);
		
		
	}

}
