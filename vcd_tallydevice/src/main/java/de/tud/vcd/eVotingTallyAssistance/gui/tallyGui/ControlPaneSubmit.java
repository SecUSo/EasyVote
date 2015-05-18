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
