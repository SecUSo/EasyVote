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
