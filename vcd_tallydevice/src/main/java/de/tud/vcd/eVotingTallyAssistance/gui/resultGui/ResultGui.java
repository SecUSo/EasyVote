/*******************************************************************************
 * #  Copyright 2015 SecUSo.org / Jurlind Budurushi / Roman Jöris
 * #
 * #  Licensed under the Apache License, Version 2.0 (the "License");
 * #  you may not use this file except in compliance with the License.
 * #  You may obtain a copy of the License at
 * #
 * #      http://www.apache.org/licenses/LICENSE-2.0
 * #
 * #  Unless required by applicable law or agreed to in writing, software
 * #  distributed under the License is distributed on an "AS IS" BASIS,
 * #  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * #  See the License for the specific language governing permissions and
 * #  limitations under the License.
 *******************************************************************************/
package de.tud.vcd.eVotingTallyAssistance.gui.resultGui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.tud.vcd.common.BallotCardDesign;
import de.tud.vcd.common.CandidateImportInterface;
import de.tud.vcd.eVotingTallyAssistance.common.ConfigHandler;
import de.tud.vcd.eVotingTallyAssistance.common.ConfigHandler.ConfigVars;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.ConfigFileException;
import de.tud.vcd.eVotingTallyAssistance.model.Wahlergebnis;


/**
 * In dieser Klasse wird das Ergebnisfenster angelegt. Das Fenster wird vom
 * Observer kontaktiert, wenn sich was am Model ändert. Dann wird die
 * update()-Routine aufgerufen, um das Ergebnis auf der Oberfläche darzustellen.
 * Die neuen Ergebnisse werden dabei abwechselnd in den Farben dargestellt, die
 * in der config.xml definiert sind. In der Statusleiste ist zudem eine
 * Übersicht, welcher Wahlzettel als letztes bearbeitet wurde, wieviele
 * Wahlzettel bereits bearbeitet wurden und wie viele davon ungültig sind.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 */
public class ResultGui extends JFrame implements java.util.Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HashMap<Integer, CandidateShow> candidates = new HashMap<Integer, CandidateShow>();
	JLabel lblNummer;
	JLabel lblZettel;
	JLabel lblUngueltig;
	JLabel lblWahlleiter;
	// Defaultcolors, falls keine weiteren in der Designdatei enthalten sind.
	Color color1 = Color.ORANGE;
	Color color2 = Color.YELLOW;
	Color activeColor;

	/**
	 * 
	 * Erzeugt das Ergebnisfenster und initialisiert es mit allen Kandidaten,
	 * die in der Kandidatenliste enthalten sind. Dabei passt sich die
	 * Darstellungsgröße an den Monitor an.
	 * 
	 * @param candidateList
	 *            ArrayList<Candidate> Liste der Kandidaten, die möglich sind.
	 * @param monitor
	 *            GraphicsConfiguration der gewünschte Monitor auf dem die GUI
	 *            angezeigt werden soll.
	 */
	public ResultGui(ArrayList<CandidateImportInterface> candidateList,
			GraphicsConfiguration monitor) {
		super(monitor);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		// Die Farben laden, wenn keine gefunden werden, Standardfarben
		// benutzen:
		try {
			color1 = Color.decode(""
					+ ConfigHandler.getInstance().getConfigValue(
							ConfigVars.RESULTCOLOR1));
		} catch (ConfigFileException | NumberFormatException e) {
			color1 = Color.ORANGE;
		}
		try {
			color2 = Color.decode(""
					+ ConfigHandler.getInstance().getConfigValue(
							ConfigVars.RESULTCOLOR2));
		} catch (ConfigFileException | NumberFormatException e) {
			color2 = Color.YELLOW;
		}

		// Standardfenster auf Fullscreen setzen und Elemente entfernen:
		setUndecorated(true);
		//Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//		setBounds(getBounds().x + 0, getBounds().y + 0, screenSize.width,
//				screenSize.height);
		setBounds(monitor.getBounds().x + 0, monitor.getBounds().y + 0, monitor.getBounds().width,
				monitor.getBounds().height );

		// dem Hauptfenster ein Layoutmanager setzen, um die Statuszeile nicht
		// im Grid zu haben
		getContentPane().setLayout(
				new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		// headline im BoxLayout:
		JPanel headlinePanel = new JPanel();
		// headlinePanel.setAlignmentX(LEFT_ALIGNMENT);
		// headlinePanel.setBackground(Color.GREEN);
		headlinePanel.setLayout(new BoxLayout(headlinePanel, BoxLayout.X_AXIS));
		add(headlinePanel);

		JLabel lblHeadline = new JLabel("Ergebnis");
		lblHeadline.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
		lblHeadline.setForeground(Color.DARK_GRAY);
		// lblHeadline.setAlignmentX(LEFT_ALIGNMENT);
		
		
		JLabel lblTNummer = new JLabel(
				"Letzte Wahlzettelnummer: ");
		lblTNummer.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		lblTNummer.setForeground(Color.GRAY);
		lblNummer = new JLabel(
				"0");
		lblNummer.setOpaque(true);
		lblNummer.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		lblNummer.setForeground(Color.GRAY);
		JLabel lblTZettel = new JLabel(
				", Anzahl Wahlzettel: ");
		lblTZettel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		lblTZettel.setForeground(Color.GRAY);
		lblZettel = new JLabel(
				"-");
		lblZettel.setOpaque(true);
		lblZettel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		lblZettel.setForeground(Color.GRAY);
		JLabel lblTUngueltig = new JLabel(
				", davon ungültige Wahlzettel: ");
		lblTUngueltig.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		lblTUngueltig.setForeground(Color.GRAY);
		lblUngueltig = new JLabel(
				"-");
		lblUngueltig.setOpaque(true);
		lblUngueltig.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		lblUngueltig.setForeground(Color.GRAY);
		JLabel lblTWahlleiter = new JLabel(
				"    Wahlleiter: ");
		lblTWahlleiter.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		lblTWahlleiter.setForeground(Color.GRAY);
		lblWahlleiter = new JLabel(
				"-");
		lblWahlleiter.setOpaque(true);
		lblWahlleiter.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		lblWahlleiter.setForeground(Color.GRAY);

				
		headlinePanel.add(lblHeadline);
		headlinePanel.add(Box.createRigidArea(new Dimension(20, 0)));
		headlinePanel.add(lblTNummer);
		headlinePanel.add(lblNummer);
		headlinePanel.add(lblTZettel);
		headlinePanel.add(lblZettel);
		headlinePanel.add(lblTUngueltig);
		headlinePanel.add(lblUngueltig);
		headlinePanel.add(lblTWahlleiter);
		headlinePanel.add(lblWahlleiter);
		headlinePanel.add(Box.createHorizontalGlue());

		// Hauptanzeige im Griddesign
		JPanel main = new JPanel();
		main.setLayout(new GridBagLayout());
		
		main.setBackground(Color.DARK_GRAY);
		main.setBounds(monitor.getBounds().x + 0, monitor.getBounds().y + 0, monitor.getBounds().width,
				monitor.getBounds().height-400);
		GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0);
		
		// Spalten und Zeilen berechnen
		CandidateShow temp = new CandidateShow(0, "Nobody", 1000);
		// int w=temp.getPreferredSize().width;
		int h = temp.getPreferredSize().height;
		// int spalten=screenSize.width /w;
	
		int zeilen = (monitor.getBounds().height - headlinePanel.getPreferredSize().height)
				/ h;
	
		String[] parties;
		try {
			parties = BallotCardDesign.getInstance().getParties();
		} catch (Exception e) {
			parties=new String[0];
		}
		int partiesCount=parties.length;
		int lastParty=-1;
		for (CandidateImportInterface k : candidateList) {
			int actParty=k.getId()/100;
			if (actParty!=lastParty){
				//insert partycaption
				String partyName="";
				if (partiesCount>=actParty){
					partyName=parties[actParty-1];
				}
				candidates.put(actParty*100, new PartyShow(actParty, partyName));
				lastParty=actParty;
			}
			candidates.put(k.getId(), new CandidateShow(k.getId(), k.getName(),
					0));
		}

		int count = 0;// Für die Positionsberechnung
		SortedSet<Integer> sortedset = new TreeSet<Integer>(candidates.keySet());

		for (int id : sortedset) {

			int xpos = count / zeilen;
			int ypos = count % zeilen;
			c.gridx = xpos + 1;
			c.gridy = ypos + 1;
			//main.add(new JLabel("foo"),c);
			main.add(candidates.get(id), c);
			count++;

		}
		add(main);

		activeColor = color1;

	}

	/**
	 * Setzt den Statustext neu. Wird nur intern aufgerufen zur
	 * Übersichtlichkeit.
	 * 
	 * @param text
	 *            String der angezeigt werden soll.
	 */
	private boolean setStatus(boolean noColorChanging, int letzteId, int anzZettel, int anzUngueltig, String wahlleiter) {
		boolean somethingChanged=false;
		//prüfen, ob sich eine Änderung ergeben wird, wenn ja umfärben
		
		Color resetC=  new Color(238,238,238);
		
		if (!noColorChanging){
		
		
		
		if (lblNummer.getText().equals(String.valueOf(letzteId))){
			lblNummer.setBackground(resetC);
		}else{
			lblNummer.setBackground(activeColor);
			somethingChanged=true;
		}
		
		if (lblZettel.getText().equals(String.valueOf(anzZettel))){
			lblZettel.setBackground(resetC);
		}else{
			lblZettel.setBackground(activeColor);
			somethingChanged=true;
		}
		
		if (lblUngueltig.getText().equals(String.valueOf(anzUngueltig))){
			lblUngueltig.setBackground(resetC);
		}else{
			lblUngueltig.setBackground(activeColor);
			somethingChanged=true;
		}
		
		if (lblWahlleiter.getText().equals(wahlleiter)){
			lblWahlleiter.setBackground(resetC);
		}else{
			lblWahlleiter.setBackground(activeColor);
			somethingChanged=true;
		}
		}else{
			lblNummer.setBackground(resetC);
			lblZettel.setBackground(resetC);
			lblUngueltig.setBackground(resetC);
			lblWahlleiter.setBackground(resetC);
		}
		//Nun die Werte setzen
		lblNummer.setText(""+letzteId);
		lblZettel.setText(""+anzZettel);
		lblUngueltig.setText(""+anzUngueltig);
		lblWahlleiter.setText(wahlleiter);
		
		return somethingChanged;
		
	}

	/**
	 * Nach jedem aktualisieren wird die Farbe gewechselt. So erscheinen
	 * hintereinander eingetragene Ergebnisse in zwei Farben auf dem Bildschirm
	 * und sind nachvollziehbarer.
	 */
	private void toggleColor() {
		if (activeColor == color1) {
			activeColor = color2;
		} else {
			activeColor = color1;
		}
	}

	/**
	 * Updatemethode. Wird vom Observer aufgerufen, der darüber die GUI
	 * informiert, dass sich am Model was geändert hat. Diese Änderungen werden
	 * hier nun verarbeitet und sichtbar gemacht. Dabei erfolgt nach jeder
	 * Aktualisierung ein Farbwechsel.
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg1 instanceof Wahlergebnis) {
			// Statusbar updaten
			int lastId = ((Wahlergebnis) arg1).getLastId();
			int stimmzettel = ((Wahlergebnis) arg1).getAnzahlWahlzettel();
			int ungueltige = ((Wahlergebnis) arg1)
					.getAnzahlUngueltigeWahlzettel();
			String wahlleiter = ((Wahlergebnis) arg1).getWahlleiter();
			boolean noColorChanging = ((Wahlergebnis) arg1).isOnlyView();

//			String statusText = "Letzte Wahlzettelnummer: "
//					+ String.valueOf(lastId) + ", Anzahl Wahlzettel: "
//					+ String.valueOf(stimmzettel)
//					+ ", davon ungültige Wahlzettel: "
//					+ String.valueOf(ungueltige) + "    (Wahlleiter: "
//					+ wahlleiter + ")";
			boolean statusChanged= setStatus(noColorChanging,lastId,stimmzettel,ungueltige, wahlleiter);
			// die ergebnisse updaten
			boolean changed=false;
			for (Entry<Integer, CandidateShow> cs : candidates.entrySet()) {
				cs.getValue().resetColor();

				int value = ((Wahlergebnis) arg1).getErgebnisOfCandidate(cs
						.getKey());

				if (!noColorChanging
						&& !cs.getValue().getVote().equals(String.valueOf(value))
								)
					cs.getValue().changeColor(activeColor);

				cs.getValue().changeVotes(value);
				changed =true;
			}
			if (changed || statusChanged)toggleColor();

		}

	}
}
