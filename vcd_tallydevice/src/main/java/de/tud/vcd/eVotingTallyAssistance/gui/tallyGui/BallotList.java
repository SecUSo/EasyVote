package de.tud.vcd.eVotingTallyAssistance.gui.tallyGui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.LineBorder;

import de.tud.vcd.common.BallotCardDesign;
import de.tud.vcd.common.BallotCardDesign.DesignKeys;
import de.tud.vcd.common.exceptions.DesignKeyNotInXMLException;
import de.tud.vcd.eVotingTallyAssistance.common.ConfigHandler;
import de.tud.vcd.eVotingTallyAssistance.common.ConfigHandler.ConfigVars;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.ConfigFileException;
import de.tud.vcd.eVotingTallyAssistance.gui.tallyGui.BallotForm.Align;
import de.tud.vcd.eVotingTallyAssistance.model.RegelChecker.Validity;
import de.tud.vcd.eVotingTallyAssistance.model.Stimme;



/**
 * Diese Klasse erstellt eine Liste von Kandidaten und zeigt diese auf der GUI
 * an. Sie ist so geschrieben, dass direkt alle möglichen Kandidatenpositionen
 * angelegt werden. Nicht benötigte Positionen lassen sich jedoch ausblenden.
 * Die Liste enthält daher ein Array von BallotForms, die die Stimme aufnehmen
 * und die Aktionen bereit stellen.
 * 
 * In der linken Liste wird oben noch die Id angezeigt, mit der der Wahlzettel
 * gespeichert werden wird. In der rechten Liste wird am unteren Rand noch eine
 * Box angezeigt, die anzeigt, ob der Wahlzettel gültig ist oder nicht. Über
 * einen rechten Mausklick läßt sich eine Nachricht an den Controller schicken,
 * dass der Status geändert werden soll.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 */
public class BallotList extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BallotForm bf[];
	private JLabel lblId;
	private JPanel invalidPanel;
	private JLabel invalid;
	private JPanel partyPanel;
	private JLabel party;
	private JLabel party2;

	private JPopupMenu popmenInvalid;
	private JPopupMenu popmenParty;
	private JMenuItem wahlzettelGueltig;
	private JMenuItem wahlzettelUngueltig;

	private Color partyEqualColor;
	
	/**
	 * Konstruktor der die Liste erzeugt und konfiguriert. Dabei wird die
	 * darzustellende Seitenhöhe übergeben, um die skalierte Darstellung
	 * berechnen zu können. Ebenso wird über Direction festgelegt, ob es sich um
	 * eine linke oder rechte Liste handelt. Die beiden Listener il und al
	 * werden entgegen genommen und weitergereicht an die Checkboxen der
	 * BallotForms und an die PopUpMenüs.
	 * 
	 * @param pageheight
	 *            int Höhe des angezeigten Bereichs
	 * @param direction
	 *            Align LEFT oder RIGHT, um die linke oder rechte Liste zu
	 *            erzeugen.
	 * @param il
	 *            ItemListener, der sich um die Befehle der Checkboxen kümmert.
	 * @param al
	 *            ActionListener, der sich um die Befehle der PopUp-Menüs
	 *            kümmert.
	 * 
	 * @throws Exception
	 */
	public BallotList(int pageheight, Align direction, ItemListener il,
			ActionListener al) throws Exception {
		super();
		
		try{
		partyEqualColor=Color.decode(""
				+ ConfigHandler.getInstance().getConfigValue(
						ConfigVars.BALLOTCARD_EQUAL_COLOR));
			} catch (ConfigFileException | NumberFormatException e) {
				partyEqualColor = new Color(0xF4FA58);
			}
		// Setzt die Höhe und Breite fest, damit sich der Anzeigebereich nicht
		// mehr verschiebt.
		setPreferredSize(new Dimension(300, pageheight));
		setMaximumSize(new Dimension(300, pageheight));
		setMinimumSize(new Dimension(300, pageheight));
//		setOpaque(true);
//		setBackground(Color.CYAN);
		setBounds(0, 0, 300, pageheight);
		setLayout(null);
		// initialisiert die Werte aus der XML Config Datei.
		int count = BallotCardDesign.getInstance().getMaxFelderEachSide();
		int votes = BallotCardDesign.getInstance().getDesignValue(
				DesignKeys.VOTESPROKANDIDAT);

		
		int seitenhoehe=BallotCardDesign.getInstance().getDesignValue(DesignKeys.PAGEHEIGHT);;
		int abstandOben=BallotCardDesign.getInstance().getDesignValue(DesignKeys.MARGIN_TOP);
		int abstandUnten=BallotCardDesign.getInstance().getDesignValue(DesignKeys.MARGIN_BOTTOM);
		//int abstandLinks=10;
		//int abstandRechts=10;
		
		//int beginInfoBoxHoehe=20;
		//int parteiFeldBreite=57;
		int beginnStimmen=BallotCardDesign.getInstance().getDesignValue(DesignKeys.BEGIN_VOTES_COLUMN);
		int infoBoxHoehe=BallotCardDesign.getInstance().getDesignValue(DesignKeys.HEIGHT_INFOBOX);
		int subUeberschriftGroesse=BallotCardDesign.getInstance().getDesignValue(DesignKeys.FONTSIZE_HEADLINE_SUB);//4;
		int ueberschriftGroesse=BallotCardDesign.getInstance().getDesignValue(DesignKeys.FONTSIZE_HEADLINE);//8;
		//int parteiFeldHoehe=infoBoxHoehe;
//		int qrCodeBreite=infoBoxHoehe;
//		int qrCodeHoehe=infoBoxHoehe;
		//int infoBoxBreite=seitenbreite-abstandLinks-abstandRechts-parteiFeldBreite-qrCodeBreite-2;
		
//		int spalteKandidatBreite=70;
//		int spalteGestrichene=45;
		
		int vertPlatzMM=seitenhoehe-abstandUnten-beginnStimmen;
    	int vertPlatzPixel=hoeheUmrechnen(vertPlatzMM, pageheight);
		double lineHeight=((double)vertPlatzPixel/(double)count);

		//setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		//setOpaque(true);
		//setBackground(Color.ORANGE);
		bf = new BallotForm[count];
		
		int beginnStimmenPixel=hoeheUmrechnen(beginnStimmen, pageheight);
		int beginInfoboxPixel = hoeheUmrechnen(abstandOben+subUeberschriftGroesse+ueberschriftGroesse, pageheight);
		int hoeheInfoboxPixel= hoeheUmrechnen(infoBoxHoehe, pageheight);
		//setAlignmentX(RIGHT_ALIGNMENT);
		if (direction == Align.LEFT) {
			
			lblId = new JLabel("Id: ");
			lblId.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
			
			// add(lblId);
			
			
			JPanel idFieldPanel = new JPanel();
			idFieldPanel.setAlignmentX(RIGHT_ALIGNMENT);
			idFieldPanel.setLayout(new BoxLayout(idFieldPanel, BoxLayout.X_AXIS));
			idFieldPanel.setBounds(0, 0, 150, beginInfoboxPixel);
			idFieldPanel.add(lblId);
			add(idFieldPanel);
			//obenlinks.add(Box.createHorizontalGlue());
			
			
			
			
			//Parteifeld erzeugen:
			partyPanel= new JPanel();
			partyPanel.setLayout(new BoxLayout(partyPanel, BoxLayout.Y_AXIS));
			partyPanel.setBounds(100, beginInfoboxPixel, 200, hoeheInfoboxPixel);
			partyPanel.setOpaque(true);
			partyPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			party=new JLabel("");
			party.setFont(new Font("Sans Serif", Font.BOLD, 20));
			partyPanel.add(party);
			party2=new JLabel("");
			party2.setFont(new Font("Sans Serif", Font.PLAIN, 12));
			partyPanel.add(party2);
			add(partyPanel);
			
			// PopUp-Menü für das Parteifeld initialisieren
			// erzeugen:
			popmenParty = new JPopupMenu("");
			JMenuItem setInvalidMenuItem;
			JMenuItem setValidMenuItem;
			JMenuItem removePartyMenuItem;
			JMenuItem setPartyMenuItem;
			
			setPartyMenuItem = new JMenuItem("Partei ändern...");
			removePartyMenuItem = new JMenuItem("Parteikreuz entfernen");
			setInvalidMenuItem = new JMenuItem("Manuell ungültig setzen!");
			setValidMenuItem = new JMenuItem("Manuell ungültig entfernen!");

			// ActionCommand mitsenden lassen zur Unterscheidung:
			setPartyMenuItem.setActionCommand("PARTY_CHANGE");
			removePartyMenuItem.setActionCommand("PARTY_REMOVE");
			setInvalidMenuItem.setActionCommand("WAHLZETTEL_UNGUELTIG");
			setValidMenuItem.setActionCommand("WAHLZETTEL_GUELTIG");

			// ActionListener registrieren
			setPartyMenuItem.addActionListener(al);
			removePartyMenuItem.addActionListener(al);
			setInvalidMenuItem.addActionListener(al);
			setValidMenuItem.addActionListener(al);
			// einfügen
			popmenParty.add(setPartyMenuItem);
			popmenParty.add(removePartyMenuItem);
			popmenParty.addSeparator();
			popmenParty.add(setInvalidMenuItem);
			popmenParty.add(setValidMenuItem);
			popmenParty.setEnabled(false);
			partyPanel.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent me) {
					if (me.isPopupTrigger()&& popmenParty.isEnabled())
						popmenParty.show(me.getComponent(), me.getX(), me.getY());
				}
			});
			
		} else {

			// InvalidBox einzeichnen:
			// Valid/Invalid-Feld initialisieren:
			invalid = new JLabel("Stimme ungültig");
			invalid.setForeground(Color.WHITE);
			invalid.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
			invalid.setAlignmentX(CENTER_ALIGNMENT);

			// PopUp-Menü für das Validfeld initialisieren
			// erzeugen:
			popmenInvalid = new JPopupMenu();
			wahlzettelGueltig = new JMenuItem("Wahlzettel gültig");
			wahlzettelUngueltig = new JMenuItem("Wahlzettel ungültig!");

			// ActionCommand mitsenden lassen zur Unterscheidung:
			wahlzettelGueltig.setActionCommand("WAHLZETTEL_GUELTIG");
			wahlzettelUngueltig.setActionCommand("WAHLZETTEL_UNGUELTIG");

			// ActionListener registrieren
			wahlzettelGueltig.addActionListener(al);
			wahlzettelUngueltig.addActionListener(al);
			// einfügen
			popmenInvalid.add(wahlzettelGueltig);
			popmenInvalid.add(wahlzettelUngueltig);
			popmenInvalid.setEnabled(false);
			
			// Das umgebene Valid/InvalidFeld anlegen und initialisieren
			invalidPanel = new JPanel();
			invalidPanel.setAlignmentX(CENTER_ALIGNMENT);
			invalidPanel.setBackground(Color.RED);
			invalidPanel.setBorder(new LineBorder(Color.DARK_GRAY));
			invalidPanel.setLayout(new BoxLayout(invalidPanel,
					BoxLayout.PAGE_AXIS));
			invalidPanel.setBounds(50, beginInfoboxPixel, 200, hoeheInfoboxPixel);
			invalidPanel.add(invalid);
			invalidPanel.add(Box.createVerticalGlue());
			invalidPanel.setVisible(false);
			// auch hier den MouseListener registrieren, damit auch neben der
			// Schrift geklickt werden kann.
			// MouseListener registrieren, diese werden hier verarbeitet und
						// nicht im Controller, da vom Rechtsklick noch kein Befehl ausgeht,
						// sondern erst die Menü für die Befehle sichtbar macht und dies ist
						// eine reine GUI Angelegenheit.
			invalidPanel.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent me) {
					if (me.isPopupTrigger()&& popmenInvalid.isEnabled())// && popmen.isEnabled()
						popmenInvalid.show(me.getComponent(), me.getX(), me.getY());
				}
			});
			invalid.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent me) {
					if (me.isPopupTrigger()&& popmenInvalid.isEnabled())// && popmen.isEnabled()
						popmenInvalid.show(me.getComponent(), me.getX(), me.getY());
				}
			});
			setValidStatus(Validity.VALID, 0);
			setInvalidShow(true);
			// zur GUI hinzufügen.
			add(invalidPanel);
			
		}
		//Nun die Stimmen anzeigen auf der Liste
		for (int i = 0; i < count; i++) {
			// Kandidatenzeile erstellen und einfügen:
			bf[i] = new BallotForm(i, votes, direction, il, al);
			//Position und Größe setzen. Größe sind 70% der Zeile, daher begin um 15 Prozent nach unten verschieben. Sieht besser aus
			bf[i].setBounds(0, (int)(lineHeight*i)+beginnStimmenPixel+(int)(lineHeight*0.15), 300, (int)(lineHeight*0.7));
			add(bf[i]);
		}
		
	}
	
	
	public void setParty(int party, boolean equivalent){
		String partyname="";
		String parties[];
		//Wahlzettel ungültig:
		if (party==-1){
			partyname = "Ungültig";
//			partyPanel.setMinimumSize(partyPanel.getSize());
//			partyPanel.setMaximumSize(partyPanel.getSize());
//			partyPanel.setPreferredSize(partyPanel.getSize());
			this.partyPanel.setOpaque(true);
			this.partyPanel.setBackground(Color.WHITE);
			this.party.setForeground(Color.BLACK);
			party2.setText("");
			partyPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			partyPanel.setToolTipText("");
		} else if (party==-2){
			partyname = "";
//			partyPanel.setMinimumSize(partyPanel.getSize());
//			partyPanel.setMaximumSize(partyPanel.getSize());
//			partyPanel.setPreferredSize(partyPanel.getSize());
			this.partyPanel.setOpaque(false);
			this.partyPanel.setBorder(BorderFactory.createEmptyBorder());
			this.partyPanel.setBackground(Color.RED);
			this.party.setForeground(Color.WHITE);
			party2.setText("");
			partyPanel.setToolTipText("");
			
		} else {
			Color c = (equivalent ? partyEqualColor : Color.WHITE);
			String eqText = (equivalent ? "Entspricht Listenkreuz" : "");
			String toolTip=(equivalent ? "Die untenstehende Darstellung entspricht der Darstellung des Listenkreuzes. Es genügt daher der Vergleich der Liste." : "");
			partyPanel.setToolTipText(toolTip);
			party2.setText(eqText);
			partyPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			try {
				parties = BallotCardDesign.getInstance().getParties();
				partyname = parties[party - 1];
				this.partyPanel.setBackground(Color.ORANGE); //this.partyPanel.setBackground(c);
				this.partyPanel.setOpaque(true);
			} catch (Exception e) {
				this.partyPanel.setBackground(c);
				partyname = "";
				this.partyPanel.setOpaque(false);
			}
		}
		this.party.setForeground(new Color(0x000000));
		this.party.setText(partyname);
	}

	/**
	 * Gibt das angeforderte Ballotform zurück.
	 * 
	 * @param i
	 *            int Position der BalloForm.
	 * @return BallotForm welches eine Stimme darstellt
	 */
	public BallotForm getBallotForm(int i) {
		return bf[i];
	}

	/**
	 * Setzt die nicht benötigten Ballotforms unsichtbar. Damit können die
	 * unbenutzten Felder ausgeblendet werden, da sie nicht den
	 * Original-Wahlzettel darstellen würden.
	 * 
	 * @param noCandidates
	 *            : int Anzahl an Kandidaten, die auf dieser Liste benötigt
	 *            werden.
	 */
	public void setVisibleCandidates(int noCandidates) {
	
		for (int i = 0; i < bf.length; i++) {
			if (i < noCandidates) {
				bf[i].setVisible(true);
			} else {
				bf[i].setVisible(false);
			}
		}
	}

	/**
	 * Ändert eine Stimme an der Position voteId der Liste.
	 * 
	 * @param voteId
	 *            int Position in der Liste.
	 * @param st
	 *            Stimme die nun auf diese Position gesetzt werden soll
	 */
	public void changeVote(int voteId, Stimme st, int pos) {
		bf[voteId].changeVote(st,pos);
	}
	
	/**
	 * Ändert eine Stimme an der Position voteId der Liste und zeigt dort soll nur der Parteiname angezeigt werden.
	 * 
	 * @param voteId
	 *            int Position in der Liste.
	 * @param parteiname
	 *           Parteiname der ausgegeben werden soll
	 */
	public void changeVote(int voteId, String parteiname, int pos) {
		bf[voteId].changeVote(parteiname,pos);
	}

	/**
	 * Setzt die in der rechten Liste angezeigten Id des angezeigten
	 * Wahlzettels.
	 * 
	 * @param id
	 *            int Id des Wahlzettels
	 */
	public void setId(int id) {
		if (lblId != null) {
			if (id == -1) {
				lblId.setText("");
			} else {
				lblId.setText("Id: " + id);
			}
		}
	}

	/**
	 * Zeigt in der rechten Liste den Valid/Invalidbereich an oder blendet ihn
	 * aus. Wenn kein Wahlzettel angezeigt wird, wird der Bereich ausgeblendet.
	 * 
	 * @param b
	 *            Boolean
	 */
	public void setInvalidShow(boolean b) {
		if (invalidPanel != null) {
			invalidPanel.setVisible(b);
		}
	}

	/**
	 * Zeigt an, ob der Wahlzettel gültig ist oder nicht. Der Bereich wird dabei
	 * jeweils farblich markiert.
	 * 
	 * @param b
	 *            Boolean
	 */
	public void setValidStatus(Validity v, int anzStimmen) {
		int maxSt;
		try {
			maxSt = BallotCardDesign.getInstance().getDesignValue(DesignKeys.MAXSTIMMEN);
		} catch (Exception e) {
			maxSt=0;
		}
		if (v.equals(Validity.VALID)) {
			invalid.setForeground(Color.BLACK);
			invalid.setText("<html>Stimme gültig<br><font size=-1>Es sind "+anzStimmen+" von "+maxSt+"<br> vergeben worden.</font></html></font></html>");
			invalid.setFont(calcFont("Stimme gültig", invalidPanel));
			invalidPanel.setBackground(new Color(0x99FF66));
		} else if (v.equals(Validity.REDUCE_CANDIDATES)){
			invalid.setForeground(Color.BLACK);
			invalid.setText("<html>Stimmen reduziert!<br><font size=-2>Orange markierte Stimmen<br> werden nicht gezählt.</font></html>");
			invalidPanel.setBackground(Color.ORANGE);
			invalid.setFont(calcFont("Stimmen reduziert!", invalidPanel));
		}else {
			invalid.setForeground(Color.WHITE);
			invalid.setText("<html>Stimme ungültig<br><font size=-1>Es sind "+anzStimmen+" von "+maxSt+"<br> vergeben worden.</font></html>");
			invalidPanel.setBackground(Color.RED);
			invalid.setFont(calcFont("Stimme ungültig", invalidPanel));
		}
	}
	
	private Font calcFont(String text, Component c){
		int breite= c.getWidth();
		int fontSizeMax=24;
		int fontSize=10;
		//System.out.println("Größe ist:"+ breite+"x"+c.getHeight());
		
		for (int i=1;i<fontSizeMax;i++){
			fontSize=i;
			FontMetrics fm=c.getFontMetrics(new Font("Sans Sarif", Font.PLAIN, fontSize));
			int fontWidth=fm.stringWidth(text);
			if (fontWidth>breite){
				break;
			}
		}	
		return new Font(Font.SANS_SERIF, Font.PLAIN, fontSize-1);
	}
	
	public void setAllInvalid(boolean b){
		for(BallotForm ballotform : bf){
			ballotform.setInvalid(b);
		}
	}
	
	
	/**
	 * Setzt den Status, ob die einzelnen Bereiche editierbar oder gesperrt
	 * sind. Dabei wird auch das PopUp-Menü für die Gültig/ungültig-Funktion
	 * gesperrt. So wird verhindert, dass Veränderungen gemacht werden, die dann
	 * vom Modell blockiert würden.
	 * 
	 * @param editable
	 *            Boolean: Wert ob das Ändern erlaubt ist.
	 */
	public void setEditable(boolean editable) {
		for (BallotForm b : bf) {
			b.setEditable(editable);
		}
		if (popmenInvalid != null) {
			popmenInvalid.setEnabled(editable);
		}
		if (popmenParty != null) {
			popmenParty.setEnabled(editable);
		}
	}

	/**
	 * rechnet Millimeter in Pixel um.
	 * 
	 * @param int Gewünschtes Maß aus der Designdatei, das nun dargestellt
	 *        werden soll.
	 * @param int Höhe des darstellbaren Bereichs. Auf dieses wird die Angabe
	 *        nun skaliert.
	 * @return int pixel Höhe in Pixeln in dem die mm-Angabe nun dargestellt
	 *         wird.
	 * @throws Exception
	 *             Wirft ein Fehler wenn aus der Designdatei nicht gelesen
	 *             werden kann, oder eine NullDivision auftritt
	 */
	private int hoeheUmrechnen(int millimeter, int bezugsgroesse)
			throws Exception {
		int realerHoehe = millimeter;
		int realeSeitenhoehe = BallotCardDesign.getInstance().getDesignValue(
				DesignKeys.PAGEHEIGHT);
		int angezeigteSeitenhoehe = bezugsgroesse;
		int angezeigteHoehe;
	
		angezeigteHoehe = (int) (((double) realerHoehe / realeSeitenhoehe) * angezeigteSeitenhoehe);
	
		return angezeigteHoehe;
	}
}
