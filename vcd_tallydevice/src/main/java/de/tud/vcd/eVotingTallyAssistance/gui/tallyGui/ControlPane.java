package de.tud.vcd.eVotingTallyAssistance.gui.tallyGui;

/**
 * 
 */

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

/**
 * Ist das Muster für die Anzeige von Kontrollbereichen im linken Rand. Stellt
 * schonmal grundlege Funktionalität zur Verfügung. Dies basiert auf einem
 * JPanel, welches mit einem Rahmen umzogen ist mit Überschriftstext im oberen
 * Rand.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 */
public class ControlPane extends JPanel {

	/**
	 * 
	 */
	private GridBagConstraints g;
	private static final long serialVersionUID = 1L;
	private HashMap<String, JComponent> components = new HashMap<String, JComponent>();

	/**
	 * Erzeugt einen Kontrollbereich mit einer Überschrift im oberen Rand
	 * 
	 * @param name
	 *            String Überschrift der Kategorie.
	 */
	public ControlPane(String name) {
		super();
		int breiteControl = 235;
		setPreferredSize(new Dimension(breiteControl, 150));
		setMaximumSize(new Dimension(breiteControl, 1000));
		setMinimumSize(new Dimension(breiteControl, 30));

		setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
				name, TitledBorder.LEADING, TitledBorder.TOP, null, null));

		setLayout(new GridBagLayout());
		g = new GridBagConstraints();
		g.fill = GridBagConstraints.BOTH;
		g.gridwidth = GridBagConstraints.REMAINDER;
		g.weightx = 50.0;
		g.insets = new Insets(0, 20, 5, 20);
	}

	/**
	 * Fügt eine neue Komponente auf das Panel hinzu und registriert es in der
	 * Map, um es später wieder ansprechen zu können, um es zum Beispiel zu
	 * deaktivieren.
	 * 
	 * @param name
	 *            String Name der Komponente, kann frei gewählt werden, sollte
	 *            jedoch aussagekräftig sein.
	 * @param c
	 *            JComponent eine beliebige Komponente, die angezeigt werden
	 *            soll
	 */
	protected void addNewComponent(String name, JComponent c) {
		components.put(name, c);
		add(components.get(name), g);
	}

	/**
	 * Liefert eine registrierte Komponente zurück.
	 * 
	 * @param name
	 *            String name der gewünschten Komponente
	 * @return JComponent
	 */
	public JComponent getComponent(String name) {
		return components.get(name);
	}

	/**
	 * Fordert speziell einen Button an, der zurückgegeben werden soll.
	 * 
	 * @param name
	 *            String Komponentenname
	 * @return JButton der angefordert wurde
	 * @throws Exception
	 *             wirft einen Fehler, wenn der Button nicht gefunden wird.
	 */
	public JButton getButton(String name) throws Exception {

		if (components.get(name) instanceof JButton) {
			return (JButton) components.get(name);
		} else {
			throw new Exception("Object not found in HashMap");
		}
	}

	/**
	 * Deaktiviert das Panel oder aktiviert es wieder. Dabei wird die
	 * eigentliche Methode überschrieben und der Status auch an die
	 * Kinderobjekte weitergeleitet.
	 */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		for (String key : components.keySet()) {
			components.get(key).setEnabled(enabled);
		}
	}

}
