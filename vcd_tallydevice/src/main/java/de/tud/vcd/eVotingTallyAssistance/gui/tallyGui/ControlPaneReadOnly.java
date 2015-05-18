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
