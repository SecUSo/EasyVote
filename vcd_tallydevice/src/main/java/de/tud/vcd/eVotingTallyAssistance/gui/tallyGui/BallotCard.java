package de.tud.vcd.eVotingTallyAssistance.gui.tallyGui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.simpleframework.xml.core.Validate;

import de.tud.vcd.common.BallotCardDesign;
import de.tud.vcd.eVotingTallyAssistance.gui.tallyGui.BallotForm.Align;
import de.tud.vcd.eVotingTallyAssistance.model.RegelChecker.Validity;
import de.tud.vcd.eVotingTallyAssistance.model.Stimme;


/**
 * Stellt den inneren Bereich der Wahlzetteldarstellung dar. Die Klasse besteht
 * aus zwei Listen an Stimmen. Jeweils am rechten und linken Rand und aus einem
 * weißen Bereich in der Mitte, der an die Größe des Wahlzettels angepasst
 * werden kann.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 */
public class BallotCard extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel paper;
	private BallotList ballotList1;
	private BallotList ballotList2;
	private ItemListener il;
	private ActionListener ccl;

	/**
	 * Erzeugt den Bereich basieredn auf einem JPanel. Es wird die Darstellung
	 * initialisert und alle benötigten Komponenten angelegt und die Listener
	 * registriert.
	 * 
	 * @param paperWidth
	 *            : Breite der weißen Din A4 Seite in der Mitte des Bildschirms
	 *            in Pixeln
	 * @param il
	 *            : ItemListener für die Kommandos der CheckboxEvents
	 * @param ccl
	 *            : ActionListener für die Kommandos in den PopUpMenüs. Werden
	 *            an die zuständigen Klassen durchgereicht.
	 * 
	 * @throws Exception
	 */
	public BallotCard(int paperWidth, ItemListener il, ActionListener ccl)
			throws Exception {
		this.il = il;
		this.ccl = ccl;
//		Dimension fixedwidth = new Dimension(150, 0);
//		Dimension infinitewidth = new Dimension(Short.MAX_VALUE, 0);
//		Box.Filler filler = new Box.Filler(fixedwidth, fixedwidth,
//				infinitewidth);

		
		//Die beiden Listen rechts und linke erzeugen:
		ballotList1 = new BallotList((int) (paperWidth * 1.414), Align.LEFT,
				il, ccl);
		ballotList2 = new BallotList((int) (paperWidth * 1.414), Align.RIGHT,
				il, ccl);

		

		/////////////////////////////////////////////////////////////////////
		///////Papier in der Mitte //////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////
		paper = new JPanel();
		setPaperWidth(paperWidth);
		paper.setBorder(new LineBorder(Color.DARK_GRAY));
		paper.setBackground(Color.WHITE);
		// Text in die Mitte der weißen Fläche schreiben
		JLabel paperlabel = new JLabel("Bitte hier den Stimmzettel hinhalten.");
		paperlabel.setSize(300, 50);
		paperlabel.setFont(new Font("sansserif", Font.BOLD, 20));
		paper.add(paperlabel);
		/////////////////////////////////////////////////////////////////////
		
		//Anzeige in der Box aufbauen:
		Box ballotcard = Box.createHorizontalBox();
		ballotcard.add(Box.createHorizontalGlue());
		ballotcard.add(ballotList1);
		ballotcard.add(paper);
		ballotcard.add(ballotList2);
		ballotcard.add(Box.createHorizontalGlue());

		
		//einfügen
		add(ballotcard, BorderLayout.CENTER);
		//setBackground(Color.GRAY);

	}

	/**
	 * Setzt die Blattbreite neu. Wird notwendig, wenn die anzuzeigende Größe
	 * sich ändert, weil der Benutzer die Blattgröße über +/- anpasst.
	 * 
	 * @param width
	 *            int Neue Breite des Blattes in Pixeln.
	 */
	public void setPaperWidth(int width) {
		int height = (int) Math.round(width * 1.41428);
		paper.setPreferredSize(new Dimension(width, height));
		paper.revalidate();
	}

	/**
	 * Liefert die aktuelle Blattgröße des weißen Blattes in der Mitte zurück.
	 * 
	 * @return int Blattbreite
	 */
	public int getPaperWidth() {
		return paper.getWidth();
	}

	/**
	 * Ändert die Papierbreite um den übergebenen Wert. Dieser kann sowohl
	 * positiv als auch negativ sein, um das Blatt zu verkleinern.
	 * 
	 * @param size
	 *            int Ändert der Blattgröße
	 */
	public void changeSize(int size) {
		int w = getPaperWidth();
		setPaperWidth(w + size);

	}

	/**
	 * Setzt die Sichbarkeitseinstellung der beiden Listen. Dabei wird anhand
	 * der übergebenen Kandidatenanzahl bestimmt wieviele Kandidaten auf welcher
	 * Liste angezeigt werden.
	 * 
	 * @param noCandidates
	 *            int Gibt an wieviele Kandidaten sichtbar sein sollen.
	 * @throws Exception
	 */
	public void setVisibleCandidates(int noCandidates) throws Exception {
		ballotList1.setVisibleCandidates(Math.min(BallotCardDesign
				.getInstance().getMaxFelderEachSide(),
				noCandidates));
		ballotList2
				.setVisibleCandidates(Math.min(
						BallotCardDesign.getInstance().getMaxFelderEachSide(),
						(Math.max(
								noCandidates
										- BallotCardDesign
												.getInstance()
												.getMaxFelderEachSide(),
								0))));
	}

	/**
	 * Reicht den Status weiter, ob die Objekte verändert werden dürfen. Dies
	 * ist nur dann möglich, wenn sich das Model im Edit-Modus befindet, daher
	 * kann das Bearbeiten deaktiviert werden, um keine Fehler anzeigen zu
	 * müssen.
	 * 
	 * @param editable
	 *            Boolean
	 */
	public void setEditable(boolean editable) {
		ballotList1.setEditable(editable);
		ballotList2.setEditable(editable);
	}

	/**
	 * Setzt die auf der linken Liste vorhandene Wahlzettel-Id neu. Wird an nur
	 * an die linke Liste weitergereicht.
	 * 
	 * @param id
	 *            int Wahlzettel-Id
	 */
	public void setId(int id) {
		ballotList1.setId(id);
	}

	/**
	 * Reicht den Status, ob die ValidBox angezeigt werden soll an die rechte
	 * Liste weiter.
	 * 
	 * @param b
	 *            Boolean ob angezeigt wird.
	 */
	public void setInvalidShow(boolean b) {
		ballotList2.setInvalidShow(b);
	}

	/**
	 * Reicht den Status weiter, ob der grade angezeigte Wahlzettel gültig ist
	 * oder nicht.
	 * 
	 * @param b
	 *            Boolean Gültig oder nicht.
	 */
	public void setValidStatus(Validity v, int anzStimmen) {
		ballotList2.setValidStatus(v, anzStimmen);
		//Die Checkboxnamen als ungültig anzeigen
		boolean b=true;
		if (v.equals(Validity.INVALID))b=false;
		ballotList1.setAllInvalid(!b);
		ballotList2.setAllInvalid(!b);
	}

	/**
	 * Zeigt eine Stimme auf der GUI an. Anhand der Id wird bestimmt auf welcher
	 * Seite die Stimme angezeigt werden soll.
	 * 
	 * @param id
	 *            int Position der Stimme
	 * @param st
	 *            Stimme: Stimme die angezeigt werden soll.
	 * 
	 * @throws Exception
	 *             wirft einen Fehler wenn die Designdatei fehlerhaft ist.
	 */
	public void showVote(int id, Stimme st, int pos) throws Exception {
		int anzProSpalte = BallotCardDesign.getInstance().getMaxFelderEachSide();
		if (id < anzProSpalte) {
			ballotList1.changeVote(id, st, pos);
		} else {
			ballotList2.changeVote(id - anzProSpalte, st, pos);
		}
	}
	
	/**
	 * Zeigt eine Stimme auf der GUI an. Anhand der Id wird bestimmt auf welcher
	 * Seite die Stimme angezeigt werden soll. Mit dieser Funktion wird nur der Parteiname angezeigt
	 * 
	 * @param id
	 *            int Position der Stimme
	 * @param st
	 *            Stimme: Stimme die angezeigt werden soll.
	 * 
	 * @throws Exception
	 *             wirft einen Fehler wenn die Designdatei fehlerhaft ist.
	 */
	public void showVote(int id, String parteiname, int pos) throws Exception {
		int anzProSpalte = BallotCardDesign.getInstance().getMaxFelderEachSide();
		if (id < anzProSpalte) {
			ballotList1.changeVote(id, parteiname, pos);
		} else {
			ballotList2.changeVote(id - anzProSpalte, parteiname, pos);
		}
	}
	
	public void showParty(int party, boolean c){
		ballotList1.setParty(party, c);
	}

	public ActionListener getCandidateClickListener() {
		return ccl;
	}

	public ItemListener getItemListener() {
		return il;
	}

}
