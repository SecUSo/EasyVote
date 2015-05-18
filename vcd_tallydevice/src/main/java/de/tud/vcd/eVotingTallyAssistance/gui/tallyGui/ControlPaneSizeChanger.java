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
