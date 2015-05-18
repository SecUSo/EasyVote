package de.tud.vcd.eVotingTallyAssistance.gui.tallyGui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Observable;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import de.tud.vcd.common.BallotCardDesign;
import de.tud.vcd.eVotingTallyAssistance.common.ConfigHandler;
import de.tud.vcd.eVotingTallyAssistance.common.ConfigHandler.ConfigVars;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.ConfigFileException;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.VotingException;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.VotingWarning;
import de.tud.vcd.eVotingTallyAssistance.controller.Tallying_C;
import de.tud.vcd.eVotingTallyAssistance.gui.tallyGui.ControlPaneProgress.States;
import de.tud.vcd.eVotingTallyAssistance.model.RegelChecker;
import de.tud.vcd.eVotingTallyAssistance.model.RegelChecker.Validity;
import de.tud.vcd.eVotingTallyAssistance.model.Stimme;
import de.tud.vcd.eVotingTallyAssistance.model.Wahlurne;
import de.tud.vcd.eVotingTallyAssistance.model.WahlurneInterface;


/**
 * Hauptanzeigefenster, welches die Auszählung steuert. Sie sollte auf dem
 * Hauptmonitor gestartet werden, der mindestens so groß sein sollte, dass ein
 * hochkant gestelltes DIN-A4 Blatt von der Höhe her auf den Bildschirm passt.
 * Auf dieser Oberfläche sitzen zudem die Hauptsteuerelemente und die Anzeige
 * des erkannten Wahlzettels. Die Fehlerausgabe geschieht zudem auch über diese
 * Oberfläche.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 */
public class TallyGui extends JFrame implements java.util.Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BallotCard ballotcard;
	private final Tallying_C controller;
	private JPanel container = new JPanel(new GridLayout(1, 1));
	private boolean sizeSubmit = false;
	// die einzelnen Controls:

	ControlPaneSizeChanger sizeChanger;
	ControlPaneScan paneScan;
	ControlPaneEdit paneEdit;
	ControlPaneSubmit paneSubmit;
	ControlPaneLock paneLock;
	ControlPaneProgress paneProgress;
	ControlPaneReadOnly paneReadonly;

	JTextArea fehlerausgabe;
	JProgressBar progressBar;

	/**
	 * 
	 * Erzeugt das Auszählfenster. Es wird mit einer Menüleiste am linken Rand
	 * erzeugt, die die Interaktion bietet. Dabei wird darauf geachtet, dass nur
	 * die Buttons klickbar sind, die im Zusammenhang auch Sinn machen und
	 * erlaubt sind. Zudem wird eine Fortschrittsanzeige angezeigt, um den
	 * Benutzer noch weiter zu führen. In der rechten Hälfte (eher 5/6) wird der
	 * erkannte Wahlzettel angezeigt.
	 * 
	 * @param c
	 *            Tallying_C der zuständige Kontroller für die Funktionsaufrufe
	 *            der Buttons
	 * @param paperwidth
	 *            int Papierbreite des weißen Feldes in der Mitte in Pixeln
	 * @param monitor
	 *            GraphicsConfiguration der gewünschte Monitor auf dem die GUI
	 *            angezeigt werden soll.
	 */
	public TallyGui(Tallying_C c, int paperwidth, GraphicsConfiguration monitor) {
		super(monitor);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.controller = c;

		// Standardfenster auf Fullscreen setzen und Elemente entfernen:
		setUndecorated(true);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(getBounds().x + 0, getBounds().y + 0, screenSize.width,
				screenSize.height);

		// Steuerleiste als JPanel anlegen und Platz festmachen:
		int breiteControl = 250;
		JPanel control = new JPanel();
		control.setPreferredSize(new Dimension(breiteControl, screenSize.height));
		control.setMaximumSize(new Dimension(breiteControl, screenSize.height));
		control.setMinimumSize(new Dimension(breiteControl, screenSize.height));
		control.setBorder(BorderFactory.createRaisedBevelBorder());

		// den Inhalt der Steuerleiste erzeugen:
		Box controlPanel = Box.createVerticalBox();
		// Titel einfügen:
		JLabel caption = new JLabel("Auszählen");
		// caption.setHorizontalTextPosition(JLabel.CENTER);
		caption.setFont(new Font("Verdana", Font.BOLD, 22));
		controlPanel.add(caption);

		// ActionListener laden
		ActionListener al = controller.getControllerListener();
		// Initialisierung der einzelnen Steuerbereiche!
		sizeChanger = new ControlPaneSizeChanger("Größer anpassen");
		paneScan = new ControlPaneScan(al, "Scannen...");
		paneEdit = new ControlPaneEdit(al, "Editieren...");
		paneSubmit = new ControlPaneSubmit(al, "Bestätigen...");
		paneLock = new ControlPaneLock(al, "Sperren...");
		paneProgress = new ControlPaneProgress("Fortschritt");
		paneReadonly = new ControlPaneReadOnly(al, "Wahlzettel ansehen");

		// Größe ändern Schaltflächen. Diese ActionListener sind hier
		// registriert und nicht im Model, da es sich um eine reine
		// Anzeigefunktion handelt und je nach Modell unterschiedlich sein
		// könnte.

		try {
			sizeChanger.getButton("plus").addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							// ballotcard.changeSize(20);
							int stepsize;
							try {
								stepsize = Integer.valueOf(ConfigHandler
										.getInstance().getConfigValue(
												ConfigVars.PLUSMINUSSTEP));
							} catch (NumberFormatException
									| ConfigFileException e1) {
								// wenn die Datei nicht gefunden wurde, oder
								// falsch konfiguriert ist, dann einfach 10 als
								// Wert annehmen.
								stepsize = 10;
							}
							try {
								ballotcard = new BallotCard(ballotcard
										.getPaperWidth() + stepsize, ballotcard
										.getItemListener(), ballotcard
										.getCandidateClickListener());
								container.removeAll();
								container.add(ballotcard);
								validate();
								repaint();
								// update um den Dummy aus dem Init zu laden
								update(null,
										new Wahlurne("foo", null, null,
												new RegelChecker(),
												controller.geschuetzteWahlurne
														.isStatusREADONLY()));
							} catch (Exception e1) {
								sendFehlerausgabe(e1);
							}

						}
					});

			sizeChanger.getButton("minus").addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							int stepsize;
							try {
								stepsize = Integer.valueOf(ConfigHandler
										.getInstance().getConfigValue(
												ConfigVars.PLUSMINUSSTEP));
							} catch (NumberFormatException
									| ConfigFileException e1) {
								// wenn die Datei nicht gefunden wurde, oder
								// falsch konfiguriert ist, dann einfach 10 als
								// Wert annehmen.
								stepsize = 10;
							}
							try {
								ballotcard = new BallotCard(ballotcard
										.getPaperWidth() - stepsize, ballotcard
										.getItemListener(), ballotcard
										.getCandidateClickListener());
								container.removeAll();
								container.add(ballotcard);
								validate();
								repaint();
								// update um den Dummy aus dem Init zu laden
								update(null,
										new Wahlurne("foo", null, null,
												new RegelChecker(),
												controller.geschuetzteWahlurne
														.isStatusREADONLY()));
							} catch (Exception e1) {
								sendFehlerausgabe(e1);
							}

						}
					});

			sizeChanger.getButton("save").addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if (JOptionPane.YES_OPTION == JOptionPane
									.showConfirmDialog(
											null,
											"Die Initialisierung wird nun mit dem Speichern der angezeigten Wahlzettelgröße beendet. Eine anschließende Veränderung der Größe ist nicht mehr möglich. Weitermachen?",
											"Warnung:",
											JOptionPane.YES_NO_OPTION,
											JOptionPane.QUESTION_MESSAGE)) {

								// Paperwidth in der Configdatei speichern
								try {
									ConfigHandler.getInstance().setConfigValue(
											ConfigVars.PAPERSIZE,
											String.valueOf(ballotcard
													.getPaperWidth()));
								} catch (ConfigFileException e2) {
									// naja, wenn er nicht schreiben kann ist es
									// nicht so schlimm, dann merkt er sich es
									// für den nächsten Start eben nicht.
								}
								sizeSubmit = true;
								update(null,
										new Wahlurne("dummy", null, null,
												new RegelChecker(),
												controller.geschuetzteWahlurne
														.isStatusREADONLY()));
							}
						}
					});
		} catch (Exception e1) {
			VotingException ve = new VotingException(
					"Das Modul zum Anpassen der Größenänderung konnte nicht korrekt initialisiert werden. Das Programm ist somit nicht einsatzfähig.");
			sendFehlerausgabe(ve);
		}

		// Die Steuerungsfelder einfügen:
		controlPanel.add(sizeChanger);
		controlPanel.add(paneScan);
		controlPanel.add(paneEdit);
		controlPanel.add(paneSubmit);

		controlPanel.add(paneLock);
		controlPanel.add(paneReadonly);
		controlPanel.add(paneProgress);

		// DUMMYCODE!!!!!!
		// NOT AUS KNOPF HINZUFÜGEN!!!!
		// Wird noch geändert:
		JButton btnNewButton = new JButton("NOT AUS!!!!");
		btnNewButton.setBackground(Color.RED);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(NORMAL);
			}
		});
		//controlPanel.add(btnNewButton);
//		JButton logbutton = new JButton("Log");
//		logbutton.setBackground(Color.RED);
//		logbutton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				ArrayList<VotingLogEntry> items=VotingLogger.getInstance().getVotingLogEntries(LogLevel.INFO);
//				String text="";
//				for (VotingLogEntry s:items){
//					text+=s.getMsg()+"<br>";
//				}
//				JOptionPane
//				.showConfirmDialog(
//						null,
//						"<html>"+text+"</html>",
//						"Log", JOptionPane.DEFAULT_OPTION,
//						JOptionPane.PLAIN_MESSAGE);
//		
//			}
//		});
//		controlPanel.add(logbutton);
		// DUMMYCODE ENDE!!!!!

		control.add(controlPanel);
		// //////////////////////////
		// Anzeigefeld
		// //////////////////////////

		// Hauptfeld zusammen bauen
		Box mainGui = Box.createHorizontalBox();
		//mainGui.setBackground(Color.YELLOW);
		control.add(controlPanel);
		mainGui.add(control);
		// mainGui.add(filler);
		mainGui.add(Box.createGlue());

		Box middleGui = Box.createVerticalBox();
		middleGui.add(Box.createGlue());

		try {
			ballotcard = new BallotCard(paperwidth, c.getItemListener(),
					c.getCandidateClickListener());
		} catch (Exception e) {
			sendFehlerausgabe(e);
		}
		middleGui.add(container);
		container.add(ballotcard);
		middleGui.add(Box.createGlue());

		mainGui.add(middleGui);
		mainGui.add(Box.createGlue());
		add(mainGui, BorderLayout.CENTER);
		setBackground(Color.YELLOW);
	}

	/**
	 * Update-Routine zum aktualisieren der TallyGUI. Fehlermeldungen werden
	 * dabei direkt ausgegeben.
	 * 
	 * @param arg0
	 *            Observable: Auslöser
	 * @param arg0
	 *            Object: übergebenes Objekt in diesem Fall die Wahlurne, aus
	 *            der die Infos für die Darstellung bezogen werden.
	 * 
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		// hier auf die Modeländerungen eingehen!!!!!
		try {

			if (arg1 instanceof Wahlurne) {
				WahlurneInterface wu = (WahlurneInterface) arg1;
				int anz = 0;
				int anzParties=0;
				int lastParty=-1;
				// Wenn im InitStatus einen Dummywahlzettel anzeigen, da edit
				// und so nicht erreichbar ist das ganze ungefährlich aber dient
				// dem einstellen des Wahlzettels.
				if (wu.isStatusINIT() && !sizeSubmit) {
					anz = 2*BallotCardDesign.getInstance().getMaxFelderEachSide();
					ArrayList<Stimme> st = new ArrayList<Stimme>();
					for (int i = 0; i < anz; i++) {
						st.add(new Stimme(-1));
					}
					for (int i = 0; i < st.size(); i++) {
						ballotcard.showVote(i, st.get(i),i);
					}
					ballotcard.setId(-1);
					ballotcard.setInvalidShow(false);
				} else {
					// Die richtigen Stimmen anzeigen:
					if (!wu.isGesperrt() && wu.getAktiverWahlzettel() != null) {
						anz = wu.getAktiverWahlzettel().getStimmen().size();

						ArrayList<Stimme> st = wu.getAktiverWahlzettel()
								.getStimmen();

						// Stimmen zur Anzeige sortieren:
						// ist hier implementiert und nicht im Model, da es
						// vielleicht auch wahlen mit Reihenfolge geben könnte
						// (nicht bei uns aber wer weiß)
						java.util.Collections.sort(st);

						// Stimmen auf der Gui anzeigen
						for (int i = 0; i < st.size(); i++) {
							int id= st.get(i).getId();
							int thisParty= id /100;
							if (thisParty!=lastParty){
								//Noch ein Feld für die Party ausgeben:
								 String[] pListe= BallotCardDesign.getInstance().getParties();
								 if ((i+anzParties+1)==BallotCardDesign.getInstance().getMaxFelderEachSide()){
									 ballotcard.showVote(i+anzParties, "", -2);
									 anzParties++;
								 }
								ballotcard.showVote(i+anzParties, "Liste "+(thisParty)+": "+pListe[thisParty-1], -2);
								anzParties++;
								lastParty=thisParty;
							}else{
								
							}
							ballotcard.showVote(i+anzParties, st.get(i), i);
						}
						ballotcard.setId(wu.getAktiverWahlzettel().getId());
						ballotcard.setInvalidShow(true);
						ballotcard.setValidStatus(wu.getAktiverWahlzettel()
								.isValid(), wu.getAktiverWahlzettel().countVotes());
						
						Validity v= wu.getAktiverWahlzettel().isValid();
						
						if (!v.equals(Validity.INVALID)){
							if (wu.isEqualToPartyVote(wu.getAktiverWahlzettel())){
								ballotcard.showParty(wu.getAktiverWahlzettel().getParty(), true);
							}else{
								ballotcard.showParty(wu.getAktiverWahlzettel().getParty(), false);
							}
						}else{
							ballotcard.showParty(-1, false);
						}
					} else {
						ballotcard.setId(-1);
						ballotcard.setInvalidShow(false);
						ballotcard.showParty(-2, false);
					}
					
				}
				ballotcard.setVisibleCandidates(anz+anzParties);

				// Editstatus verarbeiten
				if (((WahlurneInterface) arg1).isEditWahlzettel() == true) {
					// Es editierbar anzeigen
					ballotcard.setEditable(true);
					// System.out.println("Set Editable...!");
				} else {
					// es nicht editierbar anzeigen
					ballotcard.setEditable(false);
					// System.out.println("Unset Editable...!");
				}

				// Buttonstatus erneuern:
				sizeChanger.getComponent("plus").setEnabled(
						!sizeSubmit && wu.isStatusINIT());
				sizeChanger.getComponent("minus").setEnabled(
						!sizeSubmit && wu.isStatusINIT());
				sizeChanger.getComponent("save").setEnabled(
						!sizeSubmit && wu.isStatusINIT());
				paneScan.getComponent("scan").setEnabled(
						sizeSubmit
								&& (wu.isStatusWAITING() || wu.isStatusINIT())
								&& !wu.isStatusREADONLY());//
				paneScan.getComponent("loadImage").setEnabled(
						sizeSubmit
								&& (wu.isStatusWAITING() || wu.isStatusINIT())
								&& !wu.isStatusREADONLY());//
				paneScan.getComponent("barcode").setEnabled(
						sizeSubmit
								&& (wu.isStatusWAITING() || wu.isStatusINIT())
								&& !wu.isStatusREADONLY());//
				paneScan.getComponent("load")
						.setEnabled(
								sizeSubmit
										&& (wu.isStatusWAITING()
												|| wu.isStatusINIT() || wu
													.isStatusREADONLY()));//
				paneEdit.getComponent("edit").setEnabled(
						wu.isStatusOPENBALLOT() && !wu.isStatusREADONLY());
				paneEdit.getComponent("submit").setEnabled(
						wu.isStatusEDIT() && !wu.isStatusREADONLY());
				paneEdit.getComponent("discard").setEnabled(
						wu.isStatusEDIT() && !wu.isStatusREADONLY());
				paneSubmit.getComponent("submit").setEnabled(
						wu.isStatusOPENBALLOT() && !wu.isStatusREADONLY());
				paneSubmit.getComponent("cancel").setEnabled(
						wu.isStatusOPENBALLOT() && !wu.isStatusREADONLY());
				paneLock.getComponent("lock").setEnabled(
						(wu.isStatusWAITING() || wu.isStatusINIT())
								&& !wu.isStatusREADONLY());
				paneLock.getComponent("close").setEnabled(
						wu.isStatusWAITING() || wu.isStatusINIT()
								|| wu.isStatusREADONLY());

				paneReadonly.getComponent("load").setEnabled(sizeSubmit);
				paneReadonly.getComponent("loadUrne").setEnabled(sizeSubmit);

				boolean sichtbar = !wu.isStatusREADONLY();
				paneScan.setVisible(sichtbar);
				paneEdit.setVisible(sichtbar);
				paneSubmit.setVisible(sichtbar);
				paneLock.setVisible(sichtbar);
				paneReadonly.setVisible(!sichtbar);

				// Die Progressbar verändern
				//
				States nextState = States.UNDEFINED;
				if (sizeSubmit && wu.isStatusREADONLY()) {
					nextState = States.READONLY;
				} else if (!sizeSubmit && wu.isStatusINIT()) {
					nextState = States.INIT;
				} else if (wu.isStatusOPENBALLOT() && !wu.isStatusEDIT()) {
					nextState = States.SCANNED;
				} else if (wu.isStatusEDIT()) {
					nextState = States.EDIT;
				} else if (sizeSubmit
						&& (wu.isStatusWAITING() || wu.isStatusINIT())) {
					nextState = States.WAITING;
				}

				paneProgress.setStatus(nextState);

			}
		} catch (Exception e) {
			sendFehlerausgabe(e);
		}

	}

	/**
	 * Macht die auftretenden Fehler auf der GUI sichtbar. Dabei wird je nach
	 * Type zwischen Warning und Error unterschieden. Wenn es kein eigener
	 * Fehler ist, wird zudem noch der Stacktrace angezeigt. Ist zwar vielleicht
	 * erstmals ein wenig überfordernd, jedoch hilft dies beim Einschätzen des
	 * Problems.
	 * 
	 * @param e
	 *            : Exception die auftrat.
	 */
	public void sendFehlerausgabe(Exception e) {
		int errortype = JOptionPane.PLAIN_MESSAGE;
		String msg = e.getMessage();
		if (e instanceof VotingWarning) {
			errortype = JOptionPane.WARNING_MESSAGE;
		} else if (e instanceof VotingException) {
			errortype = JOptionPane.ERROR_MESSAGE;
		} else {
			errortype = JOptionPane.ERROR_MESSAGE;

			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);

			msg = sw.toString();
		}
		;

		JOptionPane.showConfirmDialog(null, msg, "Fehlermeldung:",
				JOptionPane.DEFAULT_OPTION, errortype);

	}

}
