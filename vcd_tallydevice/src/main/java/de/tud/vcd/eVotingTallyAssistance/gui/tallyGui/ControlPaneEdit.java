package de.tud.vcd.eVotingTallyAssistance.gui.tallyGui;

import java.awt.event.ActionListener;

import javax.swing.JButton;

import de.tud.vcd.eVotingTallyAssistance.controller.ControllerCalls;


/**
 * Erzeugt ein Kontrollfeld, um einen Wahlzettel zu editieren, die Änderungen zu
 * bestätigen oder zu verwerfen.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 */
public class ControlPaneEdit extends ControlPane {

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
	public ControlPaneEdit(ActionListener al, String name) {
		super(name);
		JButton button = new JButton("Wahlzettel editieren...");
		button.setActionCommand(ControllerCalls.Calls.EDITBALLOT.name());
		button.addActionListener(al);
		button.setToolTipText("Über diese Schaltfläche läßt sich der momentan angezeigte Wahlzettel korrigieren.");
		addNewComponent("edit", button);

		button = new JButton("Änderungen speichern");
		button.setActionCommand(ControllerCalls.Calls.SUBMITCHANGES.name());
		button.addActionListener(al);
		button.setToolTipText("Übernimmt die gemachten Änderungen und zeigt diese an. Der Wahlzettel ist damit noch nicht gespeichert.");

		addNewComponent("submit", button);

		button = new JButton("Änderungen verwerfen");
		button.setActionCommand(ControllerCalls.Calls.DISCARDCHANGES.name());
		button.setToolTipText("Verwirft die Änderungen und zeigt wieder den Originalwahlzettel an.");
		button.addActionListener(al);
		addNewComponent("discard", button);

	}

}
