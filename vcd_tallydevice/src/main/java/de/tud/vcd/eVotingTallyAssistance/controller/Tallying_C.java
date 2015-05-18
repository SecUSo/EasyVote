/*******************************************************************************
 * #  Copyright 2015 SecUSo.org / Jurlind Budurushi / Roman J�ris
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
/**
 * 
 */
package de.tud.vcd.eVotingTallyAssistance.controller;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import de.tud.vcd.common.BallotCardDesign;
import de.tud.vcd.common.CandidateImportInterface;
import de.tud.vcd.eVotingTallyAssistance.VotingOCR.VotingQRCode;
import de.tud.vcd.eVotingTallyAssistance.barcodeReadForm.BarcodeReaderForm;
import de.tud.vcd.eVotingTallyAssistance.common.ConfigHandler;
import de.tud.vcd.eVotingTallyAssistance.common.ConfigHandler.ConfigVars;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.CandidateNotFoundException;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.CandidateNotKnownException;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.ConfigFileException;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.DoubleCandidateIdException;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.ElectionIdIsNotEqualException;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.OCRException;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.PartyNotExistsException;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.UrneLadenException;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.UrneSpeichernException;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.VotingWarning;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.WahlhelferException;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.WahlleiterNichtKorrektException;
import de.tud.vcd.eVotingTallyAssistance.gui.loginPollWorker.LoginPollWorker;
import de.tud.vcd.eVotingTallyAssistance.gui.registerPollWorker.RegisterPollWorker;
import de.tud.vcd.eVotingTallyAssistance.gui.resultGui.ResultGui;
import de.tud.vcd.eVotingTallyAssistance.gui.tallyGui.TallyCheckbox;
import de.tud.vcd.eVotingTallyAssistance.gui.tallyGui.TallyGui;
import de.tud.vcd.eVotingTallyAssistance.gui.wahlleiter.Wahlleiter;
import de.tud.vcd.eVotingTallyAssistance.model.ReadonlyInvocationHandler;
import de.tud.vcd.eVotingTallyAssistance.model.RegelChecker;
import de.tud.vcd.eVotingTallyAssistance.model.RegelChecker.Validity;
import de.tud.vcd.eVotingTallyAssistance.model.Stimme;
import de.tud.vcd.eVotingTallyAssistance.model.Wahlhelfer;
import de.tud.vcd.eVotingTallyAssistance.model.WahlhelferInvocationHandler;
import de.tud.vcd.eVotingTallyAssistance.model.Wahlurne;
import de.tud.vcd.eVotingTallyAssistance.model.WahlurneInterface;
import de.tud.vcd.eVotingTallyAssistance.model.Wahlzettel;



/**
 * Der Controller des MVC-Patterns. Kommuniziert mit dem Model und den GUIs.
 * Beinhaltet auch die Listener, die auf Grund von Reaktionen auf den GUIs
 * ausgel�st werden. Und steuert somit den Zugriff. F�ngt zudem Fehlermeldungen
 * ab. Sollten Sie nicht behoben werden k�nnen, so werden sie an die TallyGUI
 * weitergeleitet.
 * 
 * @author Roman J�ris <roman.joeris@googlemail.com>
 * 
 */
public class Tallying_C {
	ResultGui resultGui;
	TallyGui tallyGui;
	private LoginPollWorker lpw;
	// public Wahlurne geschuetzteWahlurne;
	public WahlurneInterface geschuetzteWahlurne;
	private ActionListener al;
	private ItemListener il;
	private ActionListener ccl;
	//private ScannerListener scl;

	private boolean beenden = false;

	private enum Startmodus {
		TALLY_MODE, READ_OLD_RESULT_MODE
	};

	/**
	 * ItemListener, f�r den Fall, dass eine Checkbox eines Kandidaten ge�ndert
	 * wird.
	 * 
	 * @return the il
	 */
	public ItemListener getItemListener() {
		return il;
	}

	/**
	 * Gibt den ActionListener zur�ck, der daf�r zust�ndig ist, wenn auf der
	 * linken Men�leiste ein Button gedr�ckt wird.
	 * 
	 * @return the al
	 */
	public ActionListener getControllerListener() {
		return al;
	}

	/**
	 * Gibt den ScannerListener zur�ck, der daf�r zust�ndig ist, die Bilder
	 * zur�ck zu liefern.
	 * 
	 * @return the scl
	 */
//	public ScannerListener getScannerList() {
//		return scl;
//	}

	/**
	 * Gibt den ActionListener zur�ck, der sich darum k�mmert, wenn ein
	 * angezeigter Kandidat ver�ndert werden soll. Wird aus dem PopUpMen� heraus
	 * ausgel�st.
	 * 
	 * @return
	 */
	public ActionListener getCandidateClickListener() {
		return ccl;
	}

	public Tallying_C() {
		Startmodus startmodus = Startmodus.TALLY_MODE;
		// pr�fen, ob eine AlteDatei oder ob eine neue Ausz�hlung gestartet
		// werden soll:
//		int erg = JOptionPane
//				.showConfirmDialog(
//						tallyGui,
//						"Wollen Sie eine neue Ausz�hlung beginnen? Wenn nicht, k�nnen Sie nachfolgend eine alte Ausz�hlung zur Einsicht �ffnen.",
//						"Warnung:", JOptionPane.YES_NO_CANCEL_OPTION,
//						JOptionPane.QUESTION_MESSAGE);
		int erg = JOptionPane
				.showConfirmDialog(
						tallyGui,
						"<html><body>Herzlich Willkommen zur Ausz�hlhilfe f�r Wahlzettel.<br>" +
						"<ul>" +
						"<li>Wollen Sie eine neue Ausz�hlung beginnen, dr�cken Sie <b>Ja</b>.<br>" +
						"<li>Wollen Sie eine gespeicherte Urne laden, dr�cken Sie <b>Nein</b><br>" +
						"<br>" +
						"<li>Wollen Sie stattdessen das Programm beenden klicken Sie auf <b>Abbrechen</b>" +
						"</ul>" +
						"<br>" +
						"Wollen Sie die Ausz�hlung beginnen?" +
						"</body></html>",
						"Warnung:", JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE);
		
		//"<html><body><center>Die Id des Wahlzettels ist:<br /><br /><font size=24>"+retId+"</font><br /><br />Bitte notieren Sie die Id auf dem Wahlzettel.</center></body></html>",
		
		
		switch (erg) {
		case JOptionPane.YES_OPTION:
			// Kandidaten werden aus Designdatei gelesen
			startmodus = Startmodus.TALLY_MODE;

			break;
		case JOptionPane.NO_OPTION:
			// Kandidaten werden aus alter Datei gelesen
			startmodus = Startmodus.READ_OLD_RESULT_MODE;
			break;
		default:
			System.exit(JFrame.NORMAL);
		}

		// ---------------------------------------------------------------
		// Die Kandidatenliste f�r die ResultGui laden
		ArrayList<Integer> canList;
		ArrayList<CandidateImportInterface> candidates = new ArrayList<CandidateImportInterface>();
		try {
			canList = BallotCardDesign.getInstance().getCandidateIds();

			for (int i : canList) {
				candidates.add(BallotCardDesign.getInstance().getCandidate(i));
			}
		} catch (Exception e3) {
			JOptionPane
			.showConfirmDialog(
					tallyGui,
					e3.toString()+" "+e3.getStackTrace(),
					"Fehler:", JOptionPane.DEFAULT_OPTION,
					JOptionPane.ERROR_MESSAGE);
			JOptionPane
					.showConfirmDialog(
							tallyGui,
							"Die Kandidatenliste konnte nicht gelesen werden. Das Programm wird beendet.",
							"Fehler:", JOptionPane.DEFAULT_OPTION,
							JOptionPane.ERROR_MESSAGE);
			System.exit(JFrame.NORMAL);
		}

		
		// ---------------------------------------------------------------
		// Es wird versucht die Wahlzettelbreite aus der Configdatei zu lesen.
		int initialPaperWidth;
		try {
			initialPaperWidth = Integer.valueOf(ConfigHandler.getInstance()
					.getConfigValue(ConfigVars.PAPERSIZE));
		} catch (NumberFormatException | ConfigFileException e1) {
			// Die Configvariable kann nicht gefunden werden, also wird einfach
			// ein Standardwert angenommen
			initialPaperWidth = 700;
			// e1.printStackTrace();
		}

		// ---------------------------------------------------------------
		// es werden die Instanzen der Listener erzeugt, so dass diese vorhanden
		// sind, wenn sie �bergeben werden.
		al = new ControllerListener();
		il = new CheckBoxChangeEvent();
		ccl = new CandidateClickListener();
		//scl = new ScannerList();

		// ---------------------------------------------------------------
		// Die Positionierung auf den Monitoren bestimmen und diese Erzeugen:
		// Monitore abfragen:
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		// Maximalanzahl an Bildschirmen:
		int maxMonitore = gs.length;
		// die TallyGui richtig bestimmen, wenn Konfiguration nicht valide, dann
		// die erste Anzeige (0) nehmen.
		int sollMonitorTallyGui;
		try {
			sollMonitorTallyGui = Integer.valueOf(ConfigHandler.getInstance()
					.getConfigValue(ConfigVars.MONITORTALLYGUI));
			if (!(sollMonitorTallyGui > -1 && sollMonitorTallyGui < maxMonitore))
				throw new IndexOutOfBoundsException();
		} catch (NumberFormatException | ConfigFileException
				| IndexOutOfBoundsException e2) {
			sollMonitorTallyGui = 0;
		}
		// die ResultGui richtig bestimmen, wenn Konfiguration ung�ltig, dann
		// versuchen die zweite Anzeige (1) zu nehmen.
		// Ist sie nicht vorhanden, dann auch die erste Anzeige
		int sollMonitorResultGui;
		try {
			sollMonitorResultGui = Integer.valueOf(ConfigHandler.getInstance()
					.getConfigValue(ConfigVars.MONITORRESULTGUI));
		} catch (NumberFormatException | ConfigFileException
				| IndexOutOfBoundsException e2) {
			sollMonitorResultGui = 1;
		}
		if (!(sollMonitorResultGui > -1 && sollMonitorResultGui < maxMonitore))
			sollMonitorResultGui = 0;

		// die GUIs erzeugen und denen das richtige Fenster zuweisen.
		tallyGui = new TallyGui(this, initialPaperWidth,
				gs[sollMonitorTallyGui].getDefaultConfiguration());
		resultGui = new ResultGui(candidates,
				gs[sollMonitorResultGui].getDefaultConfiguration());

		// ---------------------------------------------------------------
		WahlurneInterface realeWahlurne;

		// ---------------------------------------------------------------
		// Dient nur dazu, dass das Frontent schon richtig initialisiert ist,
		// wenn die ersten Eingaben gemacht werden
		// Daher werden Fehler auch einfach "geschluckt", da dies nur ein
		// Mustermodell ist und nicht das echte.
		try {
			realeWahlurne = new Wahlurne("Wahl Vorstand", new Wahlhelfer(
					"test111", "test1"), new Wahlhelfer("test222", "test2"),
					new RegelChecker(), true);
			geschuetzteWahlurne = (WahlurneInterface) Proxy.newProxyInstance(
					realeWahlurne.getClass().getClassLoader(), realeWahlurne
							.getClass().getInterfaces(),
					new WahlhelferInvocationHandler(realeWahlurne));
			realeWahlurne.addObserver(resultGui);
			realeWahlurne.addObserver(tallyGui);
			geschuetzteWahlurne.updateModel();
		} catch (WahlhelferException e) {
			// do nothing
		}

		resultGui.setVisible(true);
		tallyGui.setVisible(true);

		if (startmodus == Startmodus.TALLY_MODE) {

			

			// ---------------------------------------------------------- //
			// Daten f�r das richtige Modell sammeln //
			//  ---------------------------------------------------------- //
			//Wahlhelfer eintragen lassen, dies solange ausf�hren bis zwei //
			//Wahlfelfer vorhanden sind, oder das Programm beendet werden soll.
			RegisterPollWorker rpw = new RegisterPollWorker(tallyGui); 
			// N�chste Zeile ist Dummycode 
			//rpw.setDummies("Roman", "fooobarr", "J�ris", "fooobarr"); 
			Wahlhelfer[] wahlhelfer = null;
			while (wahlhelfer == null) { 
				  wahlhelfer = rpw.showDialog(); 
				  if (wahlhelfer == null) { 
					  int nochmal = JOptionPane.showConfirmDialog( null,"Zwei Wahlhelfer sind zwingend erforderlich. Soll das Programm beendet werden?", "Warnung ", JOptionPane.YES_NO_OPTION); 
					  if (nochmal ==JOptionPane.YES_OPTION) { 
						   //beenden aufrufen 
						   System.exit(0); 
					  }
				  }
			} 
			// Nun die Daten vom Wahlleiter best�tigen lassen: 
			Wahlleiter wl= new Wahlleiter();
			  
			// Name des Wahlvorstands auslesen: 
			String wahlvorstand; 
			try {
				wahlvorstand = wl.checkWahlleiter(); 
			} catch  (WahlleiterNichtKorrektException e) { 
				// Wahlvorstand wird auf ung�ltig gesetzt. 
				wahlvorstand = "Ung�ltiger Wahlvorstand"; 
				// Fehlerausgabe: 
				JOptionPane .showConfirmDialog( tallyGui, "Die Authentifizierung des Wahlleiters ist fehlgeschlagen. Das Programm wird beendet. ("+ e.getMessage() + ")", "Fehler:", JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE); 
				// Programm beenden: 
				System.exit(0);
			}
			  
			  
			 
			// -------------------------------------------------------
			// DUMMYCODE!!!!!!!!!!!!!!!
			// um die AnmeldeProzedur zu automatieren:
//			String wahlvorstand = "ich bin ich";
//			Wahlhelfer[] wahlhelfer = new Wahlhelfer[2];
//			// try {
//			try {
//				wahlhelfer[0] = new Wahlhelfer("Roman", "asdfasdf");
//				wahlhelfer[1] = new Wahlhelfer("J�ris", "asdfasdf");
//			} catch (WahlhelferException e) {
//				tallyGui.sendFehlerausgabe(e);
//			} catch (Exception e) {
//				tallyGui.sendFehlerausgabe(e);
//			}
			// DUMMYCODE ENDE

			// -------------------------------------------------------
			// Reale und die gesch�tzte Urne initialisieren und die GUIs
			// registrieren:
			realeWahlurne = new Wahlurne(wahlvorstand, wahlhelfer[0],
					wahlhelfer[1], new RegelChecker(), false);
			geschuetzteWahlurne = (WahlurneInterface) Proxy.newProxyInstance(
					realeWahlurne.getClass().getClassLoader(), realeWahlurne
							.getClass().getInterfaces(),
					new WahlhelferInvocationHandler(realeWahlurne));
			realeWahlurne.addObserver(resultGui);
			realeWahlurne.addObserver(tallyGui);
			geschuetzteWahlurne.updateModel();

			// -------------------------------------------------------
			// Den Login der Wahlhelfer anfordern:
			lpw = new LoginPollWorker(this, tallyGui, wahlhelfer[0].getName(),
					wahlhelfer[1].getName());
			lpw.setVisible(true);
		}

		// Es soll eine alte Instanz geladen werden:
		if (startmodus == Startmodus.READ_OLD_RESULT_MODE) {
			// Datei ausw�hlen lassen
			String filename = selectUrneFromFilesystem();

			try {

				realeWahlurne = loadUrne(filename);
				geschuetzteWahlurne = (WahlurneInterface) Proxy
						.newProxyInstance(realeWahlurne.getClass()
								.getClassLoader(), realeWahlurne.getClass()
								.getInterfaces(),
								new ReadonlyInvocationHandler(realeWahlurne));
				realeWahlurne.addObserver(resultGui);
				realeWahlurne.addObserver(tallyGui);
				geschuetzteWahlurne.updateModel();
			} catch (Exception e) {
				JOptionPane.showConfirmDialog(tallyGui, e.getMessage(),
						"Fehler:", JOptionPane.DEFAULT_OPTION,
						JOptionPane.ERROR_MESSAGE);
				// e.printStackTrace();
			}
		}

	}

	private String selectUrneFromFilesystem() {
		final JFileChooser chooser = new JFileChooser("Wahlurne ausw�hlen");
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		// auf das aktuelle Verzeichnis setzen:
		File file = new File("").getAbsoluteFile();
		chooser.setCurrentDirectory(file);
		// auf XML Dateien begrenzen:
		chooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().matches(".*\\.(xml)");
			}

			@Override
			public String getDescription() {
				return "XML-Dateien (*.xml)";
			}
		});
		// anzeigen
		chooser.setVisible(true);
		final int result = chooser.showOpenDialog(null);

		// auswerten:
		if (result == JFileChooser.APPROVE_OPTION) {
			File inputVerzFile = chooser.getSelectedFile();
			return inputVerzFile.getPath();
		}
		return null;
	}

	public class CandidateClickListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				// Aufruf besteht immer aus CANDIDATE_change_<<ID>>
				// Erstmal auf den HEader Candidate pr�fen:
				String actionCmd = arg0.getActionCommand();
				if (actionCmd.startsWith("CANDIDATE_")) {
					String cmd = actionCmd.substring(10, 16);
					String candidateStr = actionCmd.substring(17);
					// Kandidatennummer in Int wandeln.
					int candnr;
					try {
						candnr = Integer.parseInt(candidateStr);
					} catch (NumberFormatException e) {
						candnr = -1;
					}
					if (candnr<=0){
						cmd="nothingToDO";
					}

					// System.out.println(cmd+" Cand: "+candidateStr);
					if (cmd.equals("change")) {

						boolean repeat;
						do {
							repeat = false;
							// Nach neuer Kandidatennummer fragen:
							String newNumber = JOptionPane
									.showInputDialog(tallyGui,
											"Geben Sie bitte die korrigierte Kandidatennummer ein.");
							// nur wenn Ok geklickt wurde weitermachen:
							if (newNumber != null) {
								try {
									geschuetzteWahlurne
											.getAktiverWahlzettel()
											.changeStimme(candnr,
													Integer.parseInt(newNumber));
								} catch (NumberFormatException e) {
									JOptionPane
											.showConfirmDialog(
													tallyGui,
													"Bitte eine g�ltige Zahl eingeben.",
													"Warnung:",
													JOptionPane.DEFAULT_OPTION,
													JOptionPane.WARNING_MESSAGE);
									repeat = true;
								} catch (CandidateNotFoundException e) {
									// Dieser Fehler kann nur durch interne
									// Probleme auftreten!!
									JOptionPane
											.showConfirmDialog(
													tallyGui,
													"Der Kandidate wurde nicht gefunden. Der Vorgang wird abgebrochen.",
													"Warnung:",
													JOptionPane.DEFAULT_OPTION,
													JOptionPane.WARNING_MESSAGE);
									repeat = false;
								} catch (DoubleCandidateIdException e) {
									JOptionPane
											.showConfirmDialog(
													tallyGui,
													"Dieser Kandidat existiert bereits auf diesem Wahlzettel und \nkann daher nicht noch einmal festgelegt werden.",
													"Warnung:",
													JOptionPane.DEFAULT_OPTION,
													JOptionPane.WARNING_MESSAGE);
									repeat = true;
								} catch (CandidateNotKnownException e) {
									JOptionPane
											.showConfirmDialog(
													tallyGui,
													"Die Kandidaten Id ist nicht im Verzeichnis enthalten. Bitte eine g�ltige Kandidatennummer eingeben.",
													"Warnung:",
													JOptionPane.DEFAULT_OPTION,
													JOptionPane.WARNING_MESSAGE);
									repeat = true;
								}
								// Bei anderen Fehler tritt die umschlie�ende
								// Fehlerbehandlung ein. Und der wiederholende
								// Vorgang ist beendet.
							}
						} while (repeat);
						// Model updaten, damit die Notifikationen
						// weitergereicht werden.
						geschuetzteWahlurne.updateModel();

					} else if (cmd.equals("delete")) {
						// Fragen ob man sicher ist:
						int antwort = JOptionPane
								.showConfirmDialog(
										tallyGui,
										"Soll die Kandidatennummer "
												+ candnr
												+ " wirklich von diesem Stillzettel gel�scht werden?",
										"Warnung:", JOptionPane.YES_NO_OPTION,
										JOptionPane.QUESTION_MESSAGE);
						// nur wenn Yes geklickt wurde weitermachen:
						if (JOptionPane.YES_OPTION == antwort) {
							try {
								geschuetzteWahlurne.getAktiverWahlzettel()
										.removeStimme(candnr);
							} catch (Exception e) {
								tallyGui.sendFehlerausgabe(e);
							}
						}

						// Model updaten, damit die Notifikationen
						// weitergereicht werden.
						geschuetzteWahlurne.updateModel();

					} else if (cmd.equals("insert")) {
						boolean repeat;
						do {
							repeat = false;
							// Nach neuer Kandidatennummer fragen:
							String newNumber = JOptionPane
									.showInputDialog(tallyGui,
											"Geben Sie bitte die gew�nschte Kandidatennummer ein.");
							// nur wenn Ok geklickt wurde weitermachen:
							if (newNumber != null) {
								try {
									geschuetzteWahlurne
											.getAktiverWahlzettel()
											.addStimme(
													new Stimme(
															Integer.parseInt(newNumber)));
								} catch (NumberFormatException e) {
									JOptionPane
											.showConfirmDialog(
													tallyGui,
													"Bitte eine g�ltige Zahl eingeben.",
													"Warnung:",
													JOptionPane.DEFAULT_OPTION,
													JOptionPane.WARNING_MESSAGE);
									repeat = true;
								} catch (DoubleCandidateIdException e) {
									JOptionPane
											.showConfirmDialog(
													tallyGui,
													"Dieser Kandidat existiert bereits auf diesem Wahlzettel und kann \ndaher nicht noch einmal festgelegt werden.",
													"Warnung:",
													JOptionPane.DEFAULT_OPTION,
													JOptionPane.WARNING_MESSAGE);
									repeat = true;
								} catch (CandidateNotKnownException e) {
									JOptionPane
											.showConfirmDialog(
													tallyGui,
													"Die Kandidaten Id ist nicht im Verzeichnis enthalten. Bitte eine g�ltige Kandidatennummer eingeben.",
													"Warnung:",
													JOptionPane.DEFAULT_OPTION,
													JOptionPane.WARNING_MESSAGE);
									repeat = true;
								} // Bei anderen Fehler tritt die umschlie�ende
									// Fehlerbehandlung ein. Und der
									// wiederholende Vorgang ist beendet.
							}
						} while (repeat);
						// Model updaten, damit die Notifikationen
						// weitergereicht werden.
						geschuetzteWahlurne.updateModel();
					} else {
						// verwerfen, da kein erlaubtes Kommando!
					}
				} else if (actionCmd.startsWith("WAHLZETTEL_")) {
					if (actionCmd.startsWith("WAHLZETTEL_GUELTIG")) {
						// Wird auf g�ltig gesetzt, aber dann pr�fen, ob dies
						// auch angezeigt werden darf
						geschuetzteWahlurne.getAktiverWahlzettel().setValid(
								true);
						// Pr�fen, ob es momentan auf g�ltig gesetzt werden
						// kann:
						RegelChecker rc = new RegelChecker();
						if (!rc.checkWahlzettel(geschuetzteWahlurne
								.getAktiverWahlzettel()).equals(Validity.VALID)) {
							JOptionPane
									.showConfirmDialog(
											tallyGui,
											"Die �berpr�fung des Wahlzettels verhindert, dass der Wahlzettel als g�ltig angezeigt werden kann.",
											"Warnung:",
											JOptionPane.DEFAULT_OPTION,
											JOptionPane.WARNING_MESSAGE);
						}
						geschuetzteWahlurne.updateModel();

					} else if (actionCmd.startsWith("WAHLZETTEL_UNGUELTIG")) {
						geschuetzteWahlurne.getAktiverWahlzettel().setValid(
								false);
					}

					geschuetzteWahlurne.updateModel();
				}else if (actionCmd.startsWith("PARTY_")) {
					if (actionCmd.startsWith("PARTY_CHANGE")) {
						// Wird auf g�ltig gesetzt, aber dann pr�fen, ob dies
						// auch angezeigt werden darf
						boolean repeat;
						do {
							repeat = false;
							// Nach neuer Kandidatennummer fragen:
							String newNumber = JOptionPane
									.showInputDialog(tallyGui,
											"Geben Sie bitte die gew�nschte Listennummer ein.");
							// nur wenn Ok geklickt wurde weitermachen:
							if (newNumber != null) {
								
								try {
									if (newNumber.equals("0")){
										throw new PartyNotExistsException("Null ist keine Listennummer.");
									}
									geschuetzteWahlurne
											.getAktiverWahlzettel()
											.setParty(Integer.parseInt(newNumber));
													
								} catch (NumberFormatException e) {
									JOptionPane
											.showConfirmDialog(
													tallyGui,
													"Bitte eine g�ltige Zahl eingeben.",
													"Warnung:",
													JOptionPane.DEFAULT_OPTION,
													JOptionPane.WARNING_MESSAGE);
									repeat = true;
								} catch (PartyNotExistsException e) {
									JOptionPane
											.showConfirmDialog(
													tallyGui,
													"Diese Listennummer existiert nicht und wird daher verworfen.",
													"Warnung:",
													JOptionPane.DEFAULT_OPTION,
													JOptionPane.WARNING_MESSAGE);
									repeat = true;
								} catch (Exception e) {
									JOptionPane
											.showConfirmDialog(
													tallyGui,
													"Ein interner Fehler ist aufgetreten. Liste kann nicht ge�ndert werden.",
													"Warnung:",
													JOptionPane.DEFAULT_OPTION,
													JOptionPane.WARNING_MESSAGE);
									repeat = true;
								} // Bei anderen Fehler tritt die umschlie�ende
									// Fehlerbehandlung ein. Und der
									// wiederholende Vorgang ist beendet.
							}
						} while (repeat);

					} else if (actionCmd.startsWith("PARTY_REMOVE")) {
						geschuetzteWahlurne.getAktiverWahlzettel().setParty(0);
					}

					geschuetzteWahlurne.updateModel();
				}
			} catch (Exception e) {
				tallyGui.sendFehlerausgabe(e);
			}
		}

	}

//	public class ScannerList implements ScannerListener {
//		public void update(ScannerIOMetadata.Type type,
//				ScannerIOMetadata metadata) {
//			if (ScannerIOMetadata.ACQUIRED.equals(type)) {
//				BufferedImage bi = metadata.getImage();
//			
//				erkenneBild(bi);
//				// variable[0]= bi;
//				// updateC();
//				// bildDa=true;
//				// System.out.println("Bild wurde geladen" + bi.getWidth());
//				// Controller informieren, dass Bild abgeholt werden kann.
//				// c.erkenneBild(bi);
//			} else if (type.equals(ScannerIOMetadata.NEGOTIATE)) {
//				ScannerDevice device = metadata.getDevice();
//				try {
//					// device.setShowUserInterface(true);
//					// device.setShowProgressBar(true);
//					device.setResolution(300);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	};

	public class CheckBoxChangeEvent implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			// Pr�fen, ob ItemEvent wirklich ein TallyCheckbox ist, dies wird
			// vorausgesetzt, um weiter zu machen:
			if (e.getSource() instanceof TallyCheckbox) {
				try {
					geschuetzteWahlurne
							.getAktiverWahlzettel()
							.getStimme(((TallyCheckbox) e.getSource()).getCandId())
							.change(((TallyCheckbox) e.getSource())
									.getPosition(),
									((TallyCheckbox) e.getSource())
											.isSelected());
					geschuetzteWahlurne.getAktiverWahlzettel().reduceVotes();
					geschuetzteWahlurne.updateModel();
				} catch (Exception e1) {
					tallyGui.sendFehlerausgabe(e1);
				}
			}

		}
	}

	public class ControllerListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				// String in ENUM umsetzen lassen, zur leichteren Bearbeitung
				ControllerCalls.Calls c = ControllerCalls.getValue(e
						.getActionCommand());
				// die Anfrage wird ans Model weitergeleitet. Sollte dies nicht
				// erlaubt sein,
				// so wird ein Fehler geschmissen.

				// Controller Befehle unterscheiden
				switch (c) {
				case LOGIN:
					String[] pwds = lpw.getPasswords();
					geschuetzteWahlurne.login(pwds[0], pwds[1]);

					if (beenden == true && !geschuetzteWahlurne.isGesperrt()) {
						beendenAbschliessen();
					}

					break;
				case LOADWAHLURNE:
					String filename = selectUrneFromFilesystem();
					try {
						Wahlurne realeWahlurne = loadUrne(filename);
						geschuetzteWahlurne = (WahlurneInterface) Proxy
								.newProxyInstance(realeWahlurne.getClass()
										.getClassLoader(), realeWahlurne
										.getClass().getInterfaces(),
										new ReadonlyInvocationHandler(
												realeWahlurne));
						realeWahlurne.addObserver(resultGui);
						realeWahlurne.addObserver(tallyGui);
						geschuetzteWahlurne.updateModel();
					} catch (Exception ex) {
						JOptionPane.showConfirmDialog(tallyGui,
								ex.getMessage(), "Fehler:",
								JOptionPane.DEFAULT_OPTION,
								JOptionPane.ERROR_MESSAGE);
						// ex.printStackTrace();
					}
					break;
				case SCAN:
					scanBallot();

					break;
				case BARCODE:
					barcodeBallot();
					break;
				case LOADIMAGE:
					loadImage();

					break;
				case LOADBALLOT:
					loadBallot();

					break;
				case EDITBALLOT:
					geschuetzteWahlurne.editAktiverWahlzettel();
					break;
				case SUBMITCHANGES:
					geschuetzteWahlurne.submitEditWahlzettel();
					break;
				case DISCARDCHANGES:
					geschuetzteWahlurne.discardEditWahlzettel();
					break;
				case SUBMITBALLOT:
					int antwort = JOptionPane
							.showConfirmDialog(
									tallyGui,
									"Der Wahlzettel wird nun gespeichert. Ist der Wahlzettel korrekt?",
									"Warnung:", JOptionPane.YES_NO_OPTION,
									JOptionPane.QUESTION_MESSAGE);
					if (antwort == JOptionPane.YES_OPTION) {
						if (geschuetzteWahlurne.getAktiverWahlzettel().isValid().equals(Validity.REDUCE_CANDIDATES)){
							antwort = JOptionPane
									.showConfirmDialog(
											tallyGui,
											"<html><body>Achtung, der Wahlzettel enth�lt mehr Stimmen als erlaubt und wird automatisch korrigiert. <br>Die orange markierten Stimmen werden nicht ins Ergebnis �bernommen. <br>Weitermachen?</body></html>",
											"Warnung:", JOptionPane.YES_NO_OPTION,
											JOptionPane.QUESTION_MESSAGE);
						}
						if (antwort==JOptionPane.YES_OPTION){
						int retId = geschuetzteWahlurne.commitWahlzettel();
						JOptionPane
								.showConfirmDialog(
										tallyGui,
										"<html><body><center>Die Id des Wahlzettels ist:<br /><br /><font size=24>"+retId+"</font><br /><br />Bitte notieren Sie die Id auf dem Wahlzettel.</center></body></html>",
										"Id notieren:", JOptionPane.DEFAULT_OPTION,
										JOptionPane.PLAIN_MESSAGE);
						}
//						JOptionPane
//						.showConfirmDialog(
//								tallyGui,
//								"Die Id des Wahlzettels ist: "
//										+ retId
//										+ ". Bitte notieren Sie die Id auf dem Wahlzettel.",
//								"Hinweis:", JOptionPane.DEFAULT_OPTION,
//								JOptionPane.INFORMATION_MESSAGE);

					}
					break;
				case DISCARDBALLOT:
					geschuetzteWahlurne.discardWahlzettel();
					break;
				case LOCKUSER:
					geschuetzteWahlurne.logout();
					break;
				case CLOSEPROGRAM:
					beendenEinleiten();

					break;
				case CLOSEPROGRAMDIRECT:
					if (geschuetzteWahlurne.isStatusREADONLY()) {
						System.exit(JFrame.NORMAL);
					} else {
						throw new VotingWarning("Der Aufruf ist nicht erlaubt.");
					}
					break;
				case INVALID:
					throw new VotingWarning(
							"Der Aufruf ist unbekannt. Bitte kontaktieren Sie den Entwickler.");
				default:
					break;
				}
			} catch (Exception ex) {
				tallyGui.sendFehlerausgabe(ex);
			}
			// Gucken, ob das Loginformular angezeigt werden muss. Den Zustand
			// der restlichen Buttons wird direkt durch die GUI gesteuert.
			if (geschuetzteWahlurne.isGesperrt()) {
				lpw.setVisible(true);
			}
		}
	}

	
	
	/**
	 * leitet den ersten Schritt des Beendens ein. Druckt das Ergebnis und
	 * fordert den Login wieder an.
	 * 
	 * @throws Exception
	 */
	private void beendenEinleiten() throws Exception {
		int res = JOptionPane
				.showConfirmDialog(
						tallyGui,
						"Sie wollen die Ausz�hlung beenden. Anschlie�end wird ein Protokoll \n gedruckt und das Programm beendet. Sind Sie sich sicher?",
						"Protokoll anfordern: ", JOptionPane.OK_CANCEL_OPTION);
		if (res == JOptionPane.YES_OPTION) {
			// Protokolldruck anzeigen
			ergebnisDrucken();
			// Abgleich abfagen:
			res = JOptionPane
					.showConfirmDialog(
							tallyGui,
							"Das Protokoll wurde an den Drucker gesendet. \n"
									+ "Bitte kontrollieren Sie, ob das Ergebnis auf dem Bildschirm identisch mit dem \nProtokollergebnis ist. "
									+ "Unterschreiben Sie dann das Protokoll. \nDies ist die letzte Gelegenheit das Beenden abzubrechen. Sind die Ergebnisse identisch?",
							"Protokoll anfordern: ", JOptionPane.YES_NO_OPTION);
			if (res == JOptionPane.YES_OPTION) {
				JOptionPane
						.showConfirmDialog(
								null,
								"Um den Vorgang abzuschlie�en, m�ssen Sie sich nun gleich nochmal am System anmelden. \nAnschlie�end erhalten Sie auch die M�glichkeit die Wahlurne an einem beliebigen Ort zu speichern.",
								"Hinweis:", JOptionPane.DEFAULT_OPTION,
								JOptionPane.INFORMATION_MESSAGE);

				// Den Benutzer auffordern nochmal sich zu authentifizieren
				geschuetzteWahlurne.logout();
				beenden = true;
				

			}
		}
	}

	/**
	 * schlie�t das Beenden ab. Wird aufgerufen, wenn der Login erfolgreich war.
	 * Nun wird die Wahlurne gespeichert und das Programm beendet.
	 * 
	 * @throws UrneSpeichernException
	 */
	private void beendenAbschliessen() throws UrneSpeichernException {
		geschuetzteWahlurne.saveUrne("config/urne.xml");

		System.exit(JFrame.NORMAL);
	}

	/**
	 * L�dt ein Bild von der Platte aus dem Ordner Wahlzettel und versucht es zu erkennen. Daf�r wird das
	 * Bild angefordert und anschlie�end eingelesen und an die Erkennung
	 * geschickt.
	 */
	private void loadImage() {
		int erlaubt=0;
		try {
			erlaubt=Integer.valueOf(ConfigHandler.getInstance()
					.getConfigValue(ConfigVars.LOADFROMFILE));
		} catch (ConfigFileException | NumberFormatException e) {
			tallyGui.sendFehlerausgabe(new Exception("Der Wert aus der Config.xml konnte nicht gelesen werden. Daher wird die Option nicht erlaubt."));
		} 
		if (erlaubt>0){
			String bild = JOptionPane.showInputDialog(tallyGui,
				"Geben Sie das Bild an:");
			if (bild!=null){
				BufferedImage bi = imageReader("wahlzettel/"
				+ bild);
				if (bi!=null){
					// Wahlzettel erkennung starten
					erkenneBild(bi);
				}
			}
		}
	}
	
	/**
	 * Zeigt ein Form an, um einen Barcode zu empfangen und wertet dieses anschlie�end aus.
	 * 
	 */
	private void barcodeBallot() {
		BarcodeReaderForm bcrf= new BarcodeReaderForm(null);
		String barcode=bcrf.readBarcode();
		System.out.println(barcode);
		
		//Wenn abgebrochen oder leerer String dann sowieso abbrechen.
		if (!(barcode == "")) {
			try {
				// Wahlzettel erkennung starten
				Wahlzettel wz = null;

				// n�chste Id anfragen, zum Erzeugen des Wahlzettels
				int id = geschuetzteWahlurne.getNextId();

				// Decodierung starten
				wz = VotingQRCode.decodeStringToWahlzettel(id,
						new RegelChecker(), barcode);
				
				//an Urne schicken
				geschuetzteWahlurne.setAktiverWahlzettel(wz);

				// wenn
				Validity v= wz.isValid();
				if ((v.equals(Validity.VALID)) != wz.getValidFlag()) {
					JOptionPane
							.showConfirmDialog(
									tallyGui,
									"Die �berpr�fung hat festgestellt, dass dieser Wahlzettel ung�ltig ist, obwohl er nicht als solcher markiert war.",
									"Warnung:", JOptionPane.DEFAULT_OPTION,
									JOptionPane.WARNING_MESSAGE);

				}
			} catch (ElectionIdIsNotEqualException e) {
				tallyGui.sendFehlerausgabe(new OCRException(
						e.getMessage()));
			}catch (Exception e) {
				tallyGui.sendFehlerausgabe(new OCRException(
						"QR Code konnte nicht korrekt erkannt werden."));
			}
		}
	}

	/**
	 * L�dt eine gespeicherte Wahlurne von der Festplatte in den Speicher.
	 * @param filename
	 * @return
	 * @throws UrneLadenException
	 */
	private static Wahlurne loadUrne(String filename) throws UrneLadenException {
		// filename="config/urne.xml";
		try {
			Serializer deserializer = new Persister();
			File source = new File(filename);
			return deserializer.read(Wahlurne.class, source);
			// System.out.println(example.election_name);

		} catch (Exception e) {
			throw new UrneLadenException(
					"Die Urne kann nicht geladen werden. Der Vorgang wird abgebrochen.");
		}
	}

	/**
	 * Ausgelagerte Methode, um die Hauptcontrollermethode des ActionListeners
	 * �bersichtlicher zu gestalten. Fragt nach dem zu ladenen Ballot und
	 * versucht dieses im Model zu laden. Fehler werden hier direkt behandelt,
	 * da es sich um einen abgeschlossenen Vorgang handelt, der keine weiteren
	 * Auswirkungen sonst h�tte.
	 */
	private void loadBallot() {
		boolean repeat;
		do {
			repeat = false;
			// Nach Wahlzettelnummer fragen:
			String ballotNumber = JOptionPane.showInputDialog(tallyGui,
					"Geben Sie die Nummer des zu ladenen Wahlzettels ein:");
			// nur wenn Ok geklickt wurde weitermachen:
			if (ballotNumber != null) {
				try {
					geschuetzteWahlurne.loadWahlzettel(Integer
							.parseInt(ballotNumber));
				} catch (NumberFormatException e) {
					JOptionPane.showConfirmDialog(tallyGui,
							"Bitte eine g�ltige Zahl eingeben.", "Warnung:",
							JOptionPane.DEFAULT_OPTION,
							JOptionPane.WARNING_MESSAGE);
					repeat = true;
					
				} catch (Exception e) {
					//Wenn ein Zettel geladen wird, der nihcht drin ist.
					JOptionPane
							.showConfirmDialog(
									tallyGui,
									"Der Stimmzettel ist nicht in der Urne. Der Vorgang wird abgebrochen.",
									"Warnung:", JOptionPane.DEFAULT_OPTION,
									JOptionPane.WARNING_MESSAGE);
					repeat = false;
				} 
//				catch (CandidateNotFoundException e) {
//					// Dieser Fehler kann nur durch interne Probleme auftreten!!
//					JOptionPane
//							.showConfirmDialog(
//									tallyGui,
//									"Der Kandidate wurde nicht gefunden. Der Vorgang wird abgebrochen.",
//									"Warnung:", JOptionPane.DEFAULT_OPTION,
//									JOptionPane.WARNING_MESSAGE);
//					repeat = false;
//				} catch (DoubleCandidateIdException e) {
//					JOptionPane
//							.showConfirmDialog(
//									tallyGui,
//									"Dieser Kandidat existiert bereits auf diesem Wahlzettel und kann daher nicht noch einmal festgelegt werden.",
//									"Warnung:", JOptionPane.DEFAULT_OPTION,
//									JOptionPane.WARNING_MESSAGE);
//					repeat = true;
//				} catch (CandidateNotKnownException e) {
//					JOptionPane
//							.showConfirmDialog(
//									tallyGui,
//									"Die Kandidaten Id ist nicht im Verzeichnis enthalten. Bitte eine g�ltige Kandidatennummer eingeben.",
//									"Warnung:", JOptionPane.DEFAULT_OPTION,
//									JOptionPane.WARNING_MESSAGE);
//					repeat = true;
//				} catch (Exception e) {
//					JOptionPane.showConfirmDialog(tallyGui,
//							"Beim Bearbeiten ist ein Fehler aufgetreten",
//							"Warnung:", JOptionPane.DEFAULT_OPTION,
//							JOptionPane.WARNING_MESSAGE);
//					repeat = true;
//					tallyGui.sendFehlerausgabe(e);
//				}
			}
		} while (repeat);
	}
	
	/**
	 * Schickt das Wahlergebnis an den Drucker. Hierzu wird ein Druckdialog angezeigt, bei dem der Drucker gew�hlt werden kann. Wird dies
	 * abgebrochen, oder tritt beim Erzeugen des Protokolls ein fehler auf, so wird um einen erneuten Druck gefragt.
	 * 
	 * Das Protokoll wird in der Klasse ProtocolPrinter erzeugt und gedruckt. In dieser m��te auch das Layout angepasst werden, wenn etwas 
	 * ge�ndert werden soll.
	 */
	private void ergebnisDrucken(){
		//Neuer Drucker:
		ProtocolPrinter printer= new ProtocolPrinter();
		boolean keinDruck=false;
		boolean gedruckt=false;
		do{
			gedruckt=printer.print(tallyGui,geschuetzteWahlurne.getErgebnis(),geschuetzteWahlurne.getWahlvorstand(), geschuetzteWahlurne.getWh1().getName(), geschuetzteWahlurne.getWh2().getName(), geschuetzteWahlurne.getErstelldatum());
			if (!gedruckt){
				int res = JOptionPane
					.showConfirmDialog(
						tallyGui,
						"Der Autrag wurde nicht gedruckt. Ohne Druck fortfahren?",
						"Druck wurde abgebrochen: ", JOptionPane.YES_NO_OPTION);
				if (res == JOptionPane.YES_OPTION)keinDruck=true;
			};
		}while(!gedruckt && !keinDruck);		
	}
	

	/**
	 * Ist zum Scannen und erkennen des Stimmzettels zust�ndig. Wird aus dem
	 * Hauptaufruf des ActionListeners ausgelagert, um diesen �bersichtlicher zu
	 * gestalten.
	 * 
	 * @throws Exception
	 */
	private void scanBallot() throws Exception {
		// Wahlzettel scannen, dazu Dialog anzeigen usw....
		//WahlzettelScanner wzs = WahlzettelScanner.getInstance(this);
		//wzs.scan();
	}

	/**
	 * Versucht das Bild zu erkennen. Erstmal anhand des QR Codes und
	 * anschlie�end anhand der manuellen Erkennung.
	 * 
	 * @param image
	 */
	public void erkenneBild(BufferedImage image) {
		
//		// Wahlzettel ans Modell �bergeben
//		int id = geschuetzteWahlurne.getNextId();
//
//		// Wahlzettel scannen
//		// String bild = JOptionPane.showInputDialog(tallyGui,
//		// "Geben Sie das Bild an:");
//		// BufferedImage bi =
//		// imageReader("C:/Users/Roman/workspace/eVoting/wahlzettel/"
//		// + bild);
//
//		// Wahlzettel erkennung starten
//		Wahlzettel wz=null;
//
//		BildErfassen bt = null;
//		try {
//			bt = new BildErfassen(image, 300);
//		
//		try {
//			
//			wz = bt.qrCodeFinden(id, new RegelChecker());
//			// throw new OCRException("fff");
//		} catch (Exception e) {
//			System.out.println("Manuelle Erkennung!");
//			try {
//				wz = bt.wahlzettelManuellErkennen(id, new RegelChecker());
//				
//			} catch (Exception e1) {
//				tallyGui.sendFehlerausgabe(e1);
//			}
//
//		}
//		
//		
//			geschuetzteWahlurne.setAktiverWahlzettel(wz);
//			// wenn
//			if (wz.isValid() != wz.getValidFlag()) {
//				JOptionPane
//						.showConfirmDialog(
//								tallyGui,
//								"Die �berpr�fung hat festgestellt, dass dieser Wahlzettel ung�ltig ist, obwohl er nicht als solcher markiert war.",
//								"Warnung:", JOptionPane.DEFAULT_OPTION,
//								JOptionPane.WARNING_MESSAGE);
//
//			}
//		} catch (Exception e) {
//			tallyGui.sendFehlerausgabe(new OCRException("Bild konnte nicht erkannt werden."));
//		}
		

	}

	/**
	 * L�dt eine Wahlzetteldatei als Bild von der Festplatte. Dies spart den
	 * Scanvorgang
	 * 
	 * @param filename
	 *            String Dateiname zu der Datei
	 * @return BufferedImage das geladene Bild
	 */
	private BufferedImage imageReader(String filename) {
		BufferedImage _bufferedImage;
		try {
			_bufferedImage = ImageIO.read(new File(filename));
			return _bufferedImage;
		} catch (IOException e1) {
			JOptionPane.showConfirmDialog(tallyGui, "Das Bild aus der Datei "
					+ filename + "konnte nicht geladen werden.", "Warnung:",
					JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);

		}
		return null;

	}

}
