package de.tud.vcd.eVotingTallyAssistance.gui.tallyGui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import de.tud.vcd.common.BallotCardDesign;
import de.tud.vcd.eVotingTallyAssistance.gui.tallyGui.TallyCheckbox.CheckboxState;
import de.tud.vcd.eVotingTallyAssistance.model.Stimme;


/**
 * Zeigt genau eine Stimme auf der TallyGUI an. Die Stimme besteht aus Name, Id
 * und den Checkboxen, um die die Kreuze symbolisieren. Es ist dabei möglich die
 * Veränderbarkeit zu sperren, damit nur Änderungen zugelassen werden, wenn das
 * Model dies auch erwartet. Zudem wird ein PopUp-Menü eingebunden, um Befehle
 * zu senden, wenn etwas geändert werden soll.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 */
public class BallotForm extends JPanel {
	private int numberOfVotes;
	private String nameOfCandidate;
	private boolean gueltigkeitsZustand;
	private JLabel candName;
	private JLabel candId;
	private JPopupMenu popmen;
	private JMenuItem changeCandidate;
	private JMenuItem deleteCandidate;
	private JMenuItem insertCandidate;
	private boolean isParty;
	private TallyCheckbox checkboxes[];
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Ausrichtungsparameter für die linke und rechte Liste
	 * 
	 * @author Roman Jöris <roman.joeris@googlemail.com>
	 * 
	 */
	public enum Align {
		LEFT, RIGHT
	}

	/**
	 * Erzeugt ein Panel für die Anzeige einer Stimme. Die Id udn noVotes
	 * spielen im Prinzip keine Rolle und dienen nur der ersten Darstellung. Die
	 * Ausrichtung bestimmt auf welcher Seite die Checkboxen angezeigt werden.
	 * Die Listener sind zuständig für die Befehle der Checkboxen und der
	 * PopUpMenüs.
	 * 
	 * @param id
	 * @param noVotes
	 * @param caption_align
	 *            Align: Ausrichtung der Checkboxen. Auf welche Seite sie
	 *            kommen.
	 * @param il
	 *            ItemListener für die Checkbox Befehle
	 * @param al
	 *            ActionListener für das PopUpMenü
	 */
	public BallotForm(int id, int noVotes, Align caption_align,
			ItemListener il, ActionListener al) {
		super();
		numberOfVotes = noVotes;
		gueltigkeitsZustand=false;
		nameOfCandidate="";
		isParty=false;
		//setBounds(0, 0, 50, 15);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		// PopUpMenü anlegen und konfigurieren
		popmen = new JPopupMenu();
		changeCandidate = new JMenuItem("Ändern...");
		deleteCandidate = new JMenuItem("Entfernen...");
		insertCandidate = new JMenuItem("Einfügen...");

		// ActionCommand mitsenden lassen zur Unterscheidung:
		changeCandidate.setActionCommand("CANDIDATE_change_-1");
		deleteCandidate.setActionCommand("CANDIDATE_delete_-1");
		insertCandidate.setActionCommand("CANDIDATE_insert_-1");

		// ActionListener registrieren
		changeCandidate.addActionListener(al);
		deleteCandidate.addActionListener(al);
		insertCandidate.addActionListener(al);

		// Menü zusammenbauen
		popmen.add(changeCandidate);
		popmen.add(deleteCandidate);
		popmen.addSeparator();
		popmen.add(insertCandidate);

		// Default anzeigen:
		candName = new JLabel("Ballot" + id);
		candName.setFont(new Font("Sans Serif", Font.PLAIN, 12));
		candId = new JLabel("-1");
		candId.setFont(new Font("Sans Serif", Font.BOLD, 12));
		// Die Listener für die Anzeige der PopUpMenüs anlegen. Dies geschieht
		// hier, da es eine rein grafische Sache ist und erste die Befehle des
		// Menüs an den Controller weitergereicht werden.
		candName.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent me) {
				if (me.isPopupTrigger() && popmen.isEnabled())
					popmen.show(me.getComponent(), me.getX(), me.getY());
			}
		});
		candId.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent me) {
				if (me.isPopupTrigger() && popmen.isEnabled())
					popmen.show(me.getComponent(), me.getX(), me.getY());
			}
		});

		// Id und Name auf der linken Seite anzeigen?
		if (caption_align == Align.LEFT) {
			add(Box.createHorizontalGlue());
			add(candName);
			add(Box.createRigidArea(new Dimension(10, 0)));
			add(candId);
			add(Box.createRigidArea(new Dimension(10, 0)));
		}
		//add(Box.createHorizontalGlue());
		//Für die Checkboxen auf der rechten Seite die Ids erhöhen
		int idZusatzFuerRechts=0;
		if (caption_align==Align.RIGHT){
			int kandProSpalte=40;
			try {
				kandProSpalte =BallotCardDesign.getInstance().getMaxFelderEachSide();
			} catch (Exception e) {
				//Naja wenn nicht gefunden, dann einfach Standardwert annehmen.
			}
			idZusatzFuerRechts=kandProSpalte;
		}
		
		// Checkboxes anlegen (so viele wie erlaubt sind) und Listener
		// registrieren:
		checkboxes = new TallyCheckbox[noVotes];
		for (int i = 0; i < noVotes; i++) {
			checkboxes[i] = new TallyCheckbox(false, id+idZusatzFuerRechts, i);
			checkboxes[i].setEnabled(false);
			checkboxes[i].setSelected(false);
			checkboxes[i].addItemListener(il);
			//invalidate();
//			int size=20;//getHeight();
//			Dimension d=new Dimension(size, size);
//			checkboxes[i].setPreferredSize(d);
//			checkboxes[i].setMaximumSize(d);
//			checkboxes[i].setMinimumSize(d);
//			checkboxes[i].setSize(d);
			
			//checkboxes[i].setSize(getHeight(), getHeight());
			add(checkboxes[i]);
			//validate();
			//add(Box.createRigidArea(new Dimension(-5, 0)));
		}
		// Id und Name auf der rechten Seite anzeigen?
		if (caption_align == Align.RIGHT) {
			add(Box.createRigidArea(new Dimension(10, 0)));
			add(candId);
			add(Box.createRigidArea(new Dimension(10, 0)));
			add(candName);
			add(Box.createHorizontalGlue());
		}
	}

	public int getCandidateId(){
		return Integer.parseInt(candId.getText());
	}
	
	/**
	 * Ändert die angezeigte Stimme in diesem Feld. Dafür wird eine ganze Stimme
	 * übergeben. Aus dieser wird dann die Id und die Checkboxes gesezt.
	 * 
	 * @param st
	 *            Stimme, die angezeigt werden soll
	 */
	public void changeVote(Stimme st, int pos) {
		isParty=false;
		int id = st.getId();
		if (id==-2){
			//System.out.println("Id ist -2!!!");
			setLabel(id);
			for (int i = 0; i < numberOfVotes; i++) {
				checkboxes[i].setVisible(false);
				checkboxes[i].setCandId(pos);

			}
		}else{
			setLabel(id);
			//System.out.println("IDs: " + id);
			int mvoted = st.getmanualVotes();
			int reduzierte= st.getReduzierteStimmen();
			for (int i = 0; i < numberOfVotes; i++) {
				checkboxes[i].setVisible(true);
				checkboxes[i].setCandId(pos);
				int sv=st.getValueAtPos(i);
				int votes=st.getAngekreuzteStimmen();
				
				if(mvoted > 0){
					checkboxes[i].setcolor(Color.ORANGE);
					mvoted--;
				}else{
					checkboxes[i].setcolor(Color.WHITE);
				}
				
				if (sv>0 && reduzierte>0) {
					checkboxes[i].setSelected(CheckboxState.INVALID);
					reduzierte--;
				}else if (sv>0 ) {
					checkboxes[i].setSelected(CheckboxState.VALID);
				}else {
					checkboxes[i].setSelected(CheckboxState.UNCHECKED);
				}

			}
		}
	}
	
	/**
	 * Ändert die angezeigte Stimme in diesem Feld. Dafür wird eine ganze Stimme
	 * übergeben. Hier wird dann nur der Parteiname angezeigt.
	 * 
	 * @param st
	 *            Stimme, die angezeigt werden soll
	 */
	public void changeVote(String parteiname, int pos) {
		isParty=true;
		//System.out.println("Id ist -2!!!");
		setLabel(parteiname);
		for (int i = 0; i < numberOfVotes; i++) {
			checkboxes[i].setVisible(false);
			checkboxes[i].setCandId(pos);
		}
		
	}
	
	
	public void setInvalid(boolean b){
		gueltigkeitsZustand=b;
		if (b){
			candName.setText("UNGÜLTIG");
			candName.setForeground(Color.RED);
		}else{
			candName.setText(nameOfCandidate);
			candName.setForeground(Color.BLACK);
		}
	}
	

	/**
	 * setzt den passenden Namen zur übergebenen Id. Wenn die Id==-1 ist, wird
	 * nur der String "Kandidatenname" angezeigt. Dies wird nur bei der
	 * Initialisierung zur EInstellung der Größe benutzt.
	 * 
	 * @param id
	 *            int Id des Kandidaten
	 */
	public void setLabel(int id) {
		// ActionCommand mitsenden lassen zur Unterscheidung:
		changeCandidate.setActionCommand("CANDIDATE_change_" + id);
		deleteCandidate.setActionCommand("CANDIDATE_delete_" + id);
		insertCandidate.setActionCommand("CANDIDATE_insert_" + id);

		// Kandidatenname aus Verzeichnis auslesen:
		String name;
		String strId;
		if (id == -1) {
			candName.setText("Kandidatenname");
			candId.setText("000");
		} else if (id == -2) {
			System.out.println("Auch hioer noch -2!!!");
			candName.setText("Kandidatenname");
			candId.setText("Parteigrenze");
		} else{

			try {
				name = BallotCardDesign.getInstance().getCandidate(id)
						.getName()+", " + BallotCardDesign.getInstance().getCandidate(id)
						.getPrename();
				strId = String.valueOf(BallotCardDesign.getInstance()
						.getCandidate(id).getId());
				candName.setForeground(Color.BLACK);
			} catch (Exception e) {
				candName.setForeground(Color.RED);
				strId = "000";
				name = "KANDIDAT UNBEKANNT!";
			}
			
			nameOfCandidate=name;
			setInvalid(gueltigkeitsZustand);
			candId.setText(strId);

		}
	}
	
	public void setLabel(String parteiname) {
		// ActionCommand mitsenden lassen zur Unterscheidung:
		changeCandidate.setActionCommand("CANDIDATE_change_-2" );
		deleteCandidate.setActionCommand("CANDIDATE_delete_-2" );
		insertCandidate.setActionCommand("CANDIDATE_insert_-2" );

		//System.out.println("Auch hioer noch -2!!!");
		//candName.setText("Kandidatenname");
		nameOfCandidate="";
		candId.setText(parteiname);
	}

	/**
	 * Setzt den Status, ob momentan das Bearbeiten erlaubt ist. Wenn nicht,
	 * auch das PopUpMenü und alle Checkboxes sperren.
	 * 
	 * @param editable
	 *            Boolean Status, ob bearbeitet werden darf.
	 */
	public void setEditable(boolean editable) {
		for (JCheckBox cb : checkboxes) {
			cb.setEnabled(editable);
		}
		// Editiermenü für Kandidatennummer auch an/abschalten:
		if (isParty==false){
			popmen.setEnabled(editable);
		}else{
			popmen.setEnabled(false);
		}
		
	}

	
	
}
