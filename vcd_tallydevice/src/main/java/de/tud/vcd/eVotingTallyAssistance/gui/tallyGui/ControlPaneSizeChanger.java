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

import javax.swing.JButton;

/**
 * Zeigt nur den Rahmen an, in dem die Komponenten für die Größenänderung
 * registriert werden. Die Handler werden dabei nicht an den Kontroller
 * geleitet, sondern werden intern in der GUI behandelt, da sie nichts mit dem
 * Model zu tun haben.
 * 
 * @author Roman
 * 
 */
public class ControlPaneSizeChanger extends ControlPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Erzeugt das Feld mit dem übergebenen Namen als Überschrift.
	 * 
	 * @param name
	 */
	public ControlPaneSizeChanger(String name) {
		super(name);
		addNewComponent("plus", new JButton("+"));
		addNewComponent("minus", new JButton("-"));
		addNewComponent("save", new JButton("Größe bestätigen"));
	}

}
