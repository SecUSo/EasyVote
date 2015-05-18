/**
 * 
 */
package de.tud.vcd.eVotingTallyAssistance.gui.resultGui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

/**
 * Erzeugt ein Feld um das Ergebnis von genau einem Kandidaten anzuzeigen. Es
 * besteht jeweils aus der Kandidaten-Id und den grade vorhandenen Stimmen für
 * diesen Kandidaten. Die Größe dieses Bereichs passt sich dynamisch an,
 * jenachdem wieviel Platz von dem erzeuger Fenster zur Verfügung gestellt wird.
 * Das Feld baut auf einem JPanel auf.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 */
public class CandidateShow extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private int votes;
	private JLabel lblVotes;
	private Color resetcolor;

	/**
	 * Erzeugt ein Ergebnisfeld und übergibt, die Id, den Namen und die Anzahl
	 * vorhandenen Stimmen. Der Name wird jedoch momentan nicht verarbeitet
	 * Jedoch könnte dies bei anderen Anzeigeformen interessant sein, oder wenn
	 * beabsichtigt ist, mit wenig Kandiddaten zu arbeiten. Die Id und die
	 * Stimmen werden dabei farblich unterschiedlich dargestellt.
	 * 
	 * @param id
	 *            int Id des Kandidaten
	 * @param name
	 *            String Name des Kandidaten (wird nicht verwendet)
	 * @param votes
	 *            int Anzahl der Stimmen, die angezeigt werden sollen.
	 */
	public CandidateShow(int id, String name, int votes) {
		this.id = id;
		this.votes = votes;

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		// Resetfarbe auslesen, um die Farbe später auf die am Anfang erzeugte
		// Farbe setzen zu können
		resetcolor = getBackground();
		setBackground(resetcolor);
		// Die Größe definieren, dabei die dynamische Entwicklung beachten
		setMinimumSize(new Dimension(600, 50));
		setPreferredSize(new Dimension(600, 50));
		setMaximumSize(new Dimension(1900, 100));
		// den Rest konfigurieren
		setBorder(new LineBorder(Color.LIGHT_GRAY));
		JLabel lblName = new JLabel(String.valueOf(this.id));
		lblName.setMinimumSize(new Dimension(20, 40));
		lblName.setPreferredSize(new Dimension(20, 40));
		lblName.setMaximumSize(new Dimension(50, 40));

		lblName.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		add(lblName);
		// einen kleinen Abstand zum Rand schaffen
		add(Box.createHorizontalStrut(5));
		lblVotes = new JLabel(String.valueOf(this.votes));
		lblVotes.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
		lblVotes.setAlignmentX(RIGHT_ALIGNMENT);
		add(lblVotes);
		add(Box.createHorizontalStrut(5));

	}

	/**
	 * Ändert die Hintergrundfarbe des Ergebnisfeldes, um bei aktuellen
	 * Ergebnissen die Aufmerksamkeit auf sich zu ziehen. Die Farbe wird dabei
	 * zentral übergeben.
	 * 
	 * @param c
	 *            Color Farbe des zukünftigen Hintergrundes
	 */
	public void changeColor(Color c) {
		setBackground(c);
	}

	/**
	 * Setzt die Farbe wieder auf die Ausgangsfarbe zurück, um so die Markierung
	 * aufzuheben.
	 */
	public void resetColor() {
		setBackground(resetcolor);
	}

	/**
	 * Ändert die angezeigte Stimmenanzahl für dieses Ergebnisfeld
	 * 
	 * @param votes
	 *            int Anzahl an Stimmen
	 */
	public void changeVotes(int votes) {
		lblVotes.setText(String.valueOf(votes));
	}

	/**
	 * Liest die Anzahl an Stimmen aus. Dies wird vom übergeordneten Fenster
	 * benötigt, um zu prüfen, ob sich was geändert hat.
	 * 
	 * @return String Anzahl der Stimmen, jedoch noch als Text!
	 */
	public String getVote() {
		return lblVotes.getText();
	}

}
