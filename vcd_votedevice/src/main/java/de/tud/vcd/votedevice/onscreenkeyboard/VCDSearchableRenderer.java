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
package de.tud.vcd.votedevice.onscreenkeyboard;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import de.tud.vcd.votedevice.municipalElection.model.Candidate;
import de.tud.vcd.votedevice.municipalElection.model.Party;

/**
 * Stellt das Suchergebnis korrekt da. Die zurückgelieferten Objekte sind
 * Instanzen im Modell und können nicht einfach dargestellt werden. Daher
 * erfolgt hier die Definition wie die Darstellung zu erfolgen hat.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 * @param <T>
 */
public class VCDSearchableRenderer<T> extends JLabel implements
		ListCellRenderer<T> {

	private static final long serialVersionUID = 1L;
	private static final Color HIGHLIGHT_COLOR = new Color(0, 0, 128);

	/**
	 * Erzeugt den Renderer und setzt den Hintergrund sichtbar.
	 */
	public VCDSearchableRenderer() {
		setOpaque(true);
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	public Component getListCellRendererComponent(JList<? extends T> list,
			T value, int index, boolean isSelected, boolean cellHasFocus) {
		if (value instanceof Candidate) {
			Candidate c = (Candidate) value;
			setText(c.getId() + ": " + c.getName() + ", " + c.getPrename()
					+ "(" + c.getParty() + ")");
		} else if (value instanceof Party) {
			Party p = (Party) value;
			setText(p.getName());
		} else {
		}

		if (isSelected) {
			setBackground(HIGHLIGHT_COLOR);
			setForeground(Color.white);
		} else {
			setBackground(Color.white);
			setForeground(Color.black);
		}
		return this;
	}
}
