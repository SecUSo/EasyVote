/**
 * 
 */
package de.tud.vcd.eVotingTallyAssistance.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import de.tud.vcd.common.BallotCardDesign;
import de.tud.vcd.common.CandidateImportInterface;
import de.tud.vcd.eVotingTallyAssistance.common.ConfigHandler;
import de.tud.vcd.eVotingTallyAssistance.common.ConfigHandler.ConfigVars;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.ConfigFileException;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.ErgebnisException;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.UrneDoppelteIdGefunden;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.UrneEditOffen;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.UrneIdUnbekannt;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.UrneSpeichernException;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.UrneWahlzettelOffen;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.WahlhelferException;
import de.tud.vcd.eVotingTallyAssistance.model.RegelChecker.Validity;
import de.tud.vcd.votedevice.municipalElection.model.Candidate;
import de.tud.vcd.votedevice.municipalElection.model.Party;


/**
 * Das zentrale Element des Modells, die Wahlurne. Hierdrin werden alle
 * Wahlzettel abgelegt und das Ergebnis berechnet. Zudem wird der Status
 * bestimmt, was nun möglich ist. Dies kann über speziell herausgeführte
 * Funktionen geprüft werden, damit die GUIs den korrekten Status des Modells
 * wiedergeben. Diesem Modell können zudem Observer registriert werden, um sie
 * über Aktualisierungen zu informieren.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 */
@Root(name = "urne")
public class Wahlurne extends Observable implements Serializable,
		WahlurneInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Path("Wahlzettelablage")
	@ElementMap(entry = "wahlzettel", key = "id", attribute = true, inline = true)
	private HashMap<Integer, Wahlzettel> wahlzettel;
	@Element
	private Wahlergebnis ergebnis;
	@Element
	private String wahlvorstand;

	@Element
	private Date erstelldatum;

	private boolean readonly = false;

	private Wahlzettel aktiverWahlzettel = null;
	private Wahlzettel editBackupWahlzettel = null;// um den Wahlzettel bei
													// einem Verwerfen wieder
													// herstellen zu können
	private Wahlzettel originalWahlzettel = null;//originärer Zustand vor der ersten Änderung, um Log schreiben zu können												
	
	
	
	private boolean editWahlzettel;

	private RegelChecker rc;
	private int lastId;

	private Wahlhelfer wh1;
	private Wahlhelfer wh2;
	
	private HashMap<Integer, Party> partyList;

	/**
	 * Erzeugt die Wahlurne mit dem Namen des Wahlvorstandes und zwei
	 * Wahlhelfern. Zudem wird ein Regelchecker übergeben, um die Wahlzettel zu
	 * überprüfen. Wenn der ReadOnlyStatus gesetzt ist, verhält sich die
	 * Statusberechnung anders, so dass unerlaubte Zustände vermieden werden, da
	 * niemals ein Editieren erlaubt sein darf.
	 * 
	 * @param wahlvorstand
	 *            String Name des Wahlvorstand
	 * @param wahlhelfer1
	 *            Wahlhelfer der erste Wahlhelfer
	 * @param wahlhelfer2
	 *            Wahlhelfer der zweite Wahlhelfer
	 * @param rc
	 *            RegelChecker zum kontrollieren
	 * @param readonly
	 *            Boolean
	 */
	public Wahlurne(String wahlvorstand, Wahlhelfer wahlhelfer1,
			Wahlhelfer wahlhelfer2, RegelChecker rc, boolean readonly) {
		VotingLogger.getInstance().flush();
		try {
			VotingLogger.getInstance().log(
					"Dieser Log wurde an folgender Maschine erstellt: "
							+ ConfigHandler.getInstance().getConfigValue(
									ConfigVars.MACHINEID));
		} catch (ConfigFileException e1) {
			VotingLogger
					.getInstance()
					.log("Dieser Log wurde an folgender Maschine erstellt: UNBEKANNT");
		}
		this.readonly = readonly;
		// Wahlvorstand muss bereits zum Anlegen der Urne existieren:
		this.wahlvorstand = wahlvorstand;
		// Wahlhelfer übergeben:
		wh1 = wahlhelfer1;
		wh2 = wahlhelfer2;
		// und gleich Login einfordern:

		this.rc = rc;
		// Das aktuelle Datum in die Urne schreiben
		this.erstelldatum = Calendar.getInstance().getTime();
		// Initialisiert die Wahlzettel und das Zwischenergebnis:
		wahlzettel = new HashMap<Integer, Wahlzettel>();
		ergebnis = new Wahlergebnis(wahlvorstand);
		lastId = 0;
		editWahlzettel = false;
		VotingLogger.getInstance().log(
				"Die Initialisierung erfolge am "
						+ DateFormat.getDateTimeInstance().format(erstelldatum));
		try {
			doFillPartylist();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Erzeugt die Wahlurne beim Laden aus einer XML Datei.
	 * 
	 * @param wahlzettel
	 * @param ergebnis
	 * @param wahlvorstand
	 * @param erstelldatum
	 * @throws WahlhelferException
	 */
	public Wahlurne(
			@ElementMap(entry = "wahlzettel", key = "id", attribute = true, inline = true) HashMap<Integer, Wahlzettel> wahlzettel,
			@Element(name = "ergebnis") Wahlergebnis ergebnis,
			@Element(name = "wahlvorstand") String wahlvorstand,
			@Element(name = "erstelldatum") Date erstelldatum)
			throws WahlhelferException {
		// Readonly status setzen:
		readonly = true;
		// Wahlvorstand muss bereits zum Anlegen der Urne existieren:
		this.wahlvorstand = wahlvorstand;
		// Wahlhelfer anonym eintragen, da sie nicht mitgespeichert wurden.
		String wahlhelferAnonymName = "AnonymReadonly";
		String wahlhelferAnonymKennwort = "AnonymReadonly";
		// Wahlhelfer übergeben:

		wh1 = new Wahlhelfer(wahlhelferAnonymName, wahlhelferAnonymKennwort);
		wh2 = new Wahlhelfer(wahlhelferAnonymName, wahlhelferAnonymKennwort);
		// und gleich Login einfordern:
		this.rc = new RegelChecker();
		// Das aktuelle Datum in die Urne schreiben
		this.erstelldatum = erstelldatum;
		// Initialisiert die Wahlzettel und das Zwischenergebnis:
		this.wahlzettel = wahlzettel;
		this.ergebnis = ergebnis;
		lastId = 0;
		editWahlzettel = false;

		try {
			doFillPartylist();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		login(wahlhelferAnonymKennwort, wahlhelferAnonymKennwort);

	}
	
	
	public boolean isEqualToPartyVote(Wahlzettel wz){
		//Gar keine Partei gewählt
		if (!wz.isPartyVoted()){
			return false;
		}
		//Partei ist unbekannt:
		int aktParty=wz.getParty();
		Party p= partyList.get(aktParty);
		if (p==null){
			return false;
		}
		//Nun vergleichen:
		boolean isEqual=true;
		ArrayList<Candidate> cAl= p.getCandidates();
		
		//Alle vorhandenen Stimmen Ids auslesen und für schnelleren Zugriff in Hash packen:
		HashMap<Integer, Candidate> candIds=new HashMap<Integer, Candidate>();
		for(Candidate c: cAl){
			candIds.put(c.getId(), c);
		}
		//Für jeden Kandidtaen aus dem aktiven Wahlzettel fragen, ob er auch im Vergleich existiert und anschließend aus der Liste nehmen:
		for (Stimme st: wz.getStimmen()){
			//ist die Stimme in der Id Liste, wenn ja ausbenen und aus der Liste entfernen
			Candidate c = candIds.remove(st.getId());
			if (c!=null){
				//Ist die Stimmanzahl gleich?
				if (st.getValue()==c.getCountedVotes()){
					
				}else{
					//Eine Abweichunug wurde gefunden, daher kann alles abgebrochen werden
					isEqual=false;
					break;
				}
			}else{
				//Kandidat wurde nicht gefunden, daher kann alles abgebrochen werden
				isEqual=false;
				break;
			}
			
		}
		if (!isEqual){
			return false;
		}
		//Nun noch prüfen, ob die Liste leer ist, wenn nicht wurden nicht alle Kandidaten bedacht
		if (!candIds.isEmpty()){
			return false;
		}
		//Da keine Abweichung gefunden wurde, stimmen die Stimmverteilungen überein:
		return true;
	}

	private void doFillPartylist() throws Exception{
		partyList= new HashMap<Integer, Party>();
		//Alle Parteien aus der wahlzettel.xml lesen
		String[] partiesStr= BallotCardDesign.getInstance().getParties();
		for(int i=1;i<=partiesStr.length;i++){
			Party e= new Party(i, partiesStr[i-1],BallotCardDesign.getInstance().getDesignValue(BallotCardDesign.DesignKeys.VOTESPROKANDIDAT), BallotCardDesign.getInstance().getDesignValue(BallotCardDesign.DesignKeys.MAXSTIMMEN));
			partyList.put(i, e);
		}
		//Alle Kandidaten nun in die Parteien füllen:
		//InputStream filename=getClass().getClassLoader().getResource("wahlzettel.xml").openStream();
		String filename = ConfigHandler.getJarExecutionDirectory()+ ConfigHandler.getInstance().getConfigValue(
				ConfigVars.WAHLZETTELDESIGNDATEI);
		System.out.println(filename);
		InputStream file=new FileInputStream(filename);
		ArrayList<CandidateImportInterface> cii= BallotCardDesign.getInstance(file).getCandidates();
		for(CandidateImportInterface ci : cii){
			Candidate c= new Candidate(ci);
			int partei=c.getId()/100;
			partyList.get(partei).addCandidate(ci);
		}
		//System.out.println("Mal sehn");
		for(Party p: partyList.values()){
			p.setVoted(true);
			p.distributeVotes(BallotCardDesign.getInstance().getDesignValue(BallotCardDesign.DesignKeys.MAXSTIMMEN));
		}
		
		
		//Hier gehts weiter. Sind nun alle Kandidaten in der Partei und nun muss für alle die autoverteilung gemacht werden und anschließend eine Vergelichsprozedur entwekcelt werdemn.
		//cii.get(1).
		
	}
	
	@Override
	public boolean isEditWahlzettel() {
		return editWahlzettel;
	}

	/**
	 * Speichert die Urne unter dem übergebenen Dateinamen ab, wenn nicht über
	 * den Dateiwahldialog ein anderes Ziel gewählt wurde.
	 * 
	 * @param filename
	 *            String Dateiname der alternativ verwendet werden soll
	 */
	public void saveUrne(String filename) throws UrneSpeichernException {
		JFileChooser chooser = new JFileChooser("");
		chooser.setMultiSelectionEnabled(false);
		chooser.setCurrentDirectory(new File("").getAbsoluteFile());
		chooser.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory()
						|| f.getName().toLowerCase().endsWith(".xml");
			}

			public String getDescription() {
				return "XML-Dateien (*.xml)";
			}
		});

		int result = chooser.showSaveDialog(null);

		File choosenFile = null;
		if (result == JFileChooser.APPROVE_OPTION)
			choosenFile = chooser.getSelectedFile();

		if (choosenFile == null) {
			DateFormat dfmt = new SimpleDateFormat("yy-MM-dd'_um_' hh-mm-ss");
			String df = dfmt.format(new Date());
			filename = "Wahlurne_" + df + ".xml";
		} else {

			filename = choosenFile.getAbsolutePath();
			if (!filename.toLowerCase().endsWith(".xml"))
				filename = filename + ".xml";
		}

		Serializer serializer = new Persister();
		File file = new File(filename);
		// und versucht diesen zu speichern
		try {
			// System.out.println("Konfig schreiben");
			serializer.write(this, file);
			JOptionPane.showConfirmDialog(null, "Die Wahlurne wurde erfolgreich gespeichert.", "Hinweis:", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
			
		} catch (Exception e) {
			throw new UrneSpeichernException(
					"Die Wahlurne konnte nicht in die angegebene Datei gesichert werden.");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tud.eVotingTallyAssistance.model.WahlurneInterface#isGesperrt()
	 */
	@Override
	public boolean isGesperrt() {
		if (wh1 != null && wh2 != null && wh1.isActive() && wh2.isActive()) {
			return false;
		} else {
			return true;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tud.eVotingTallyAssistance.model.WahlurneInterface#login(java.lang.String
	 * , java.lang.String)
	 */
	@Override
	public boolean login(String pwd1, String pwd2) throws WahlhelferException {
		setChanged();
		notifyObservers("Login");
		return (wh1.login(pwd1) && wh2.login(pwd2));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tud.eVotingTallyAssistance.model.WahlurneInterface#logout()
	 */
	@Override
	public boolean logout() throws Exception {
		if (this.aktiverWahlzettel == null) {
			return (wh1.logout() && wh2.logout());
		} else {
			throw new UrneWahlzettelOffen(
					"Noch ein aktiver Wahlzettel vorhanden. Vorgang vorher beenden.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tud.eVotingTallyAssistance.model.WahlurneInterface#getNextId()
	 */
	@Override
	public int getNextId() {
		return lastId + 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tud.eVotingTallyAssistance.model.WahlurneInterface#
	 * createBackupOfAktiverWahlzettel()
	 */
	@Override
	public void createBackupOfAktiverWahlzettel() {
		editBackupWahlzettel = aktiverWahlzettel.copyWahlzettel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tud.eVotingTallyAssistance.model.WahlurneInterface#getAktiverWahlzettel()
	 */
	@Override
	public Wahlzettel getAktiverWahlzettel() {
		return aktiverWahlzettel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tud.eVotingTallyAssistance.model.WahlurneInterface#getErgebnis()
	 */
	@Override
	public Wahlergebnis getErgebnis() {
		return ergebnis;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tud.eVotingTallyAssistance.model.WahlurneInterface#getWahlvorstand()
	 */
	@Override
	public String getWahlvorstand() {
		return wahlvorstand;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tud.eVotingTallyAssistance.model.WahlurneInterface#getWh1()
	 */
	@Override
	public Wahlhelfer getWh1() {
		return wh1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tud.eVotingTallyAssistance.model.WahlurneInterface#getWh2()
	 */
	@Override
	public Wahlhelfer getWh2() {
		return wh2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tud.eVotingTallyAssistance.model.WahlurneInterface#getErstelldatum()
	 */
	@Override
	public Date getErstelldatum() {
		return erstelldatum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tud.eVotingTallyAssistance.model.WahlurneInterface#setAktiverWahlzettel
	 * (tud.eVotingTallyAssistance.model.Wahlzettel)
	 */
	@Override
	public void setAktiverWahlzettel(Wahlzettel aktiverWahlzettel)
			throws UrneWahlzettelOffen {
		if (this.aktiverWahlzettel == null) {
			this.aktiverWahlzettel = aktiverWahlzettel;
		} else {
			throw new UrneWahlzettelOffen("Noch ein Wahlzettel in Bearbeitung.");
		}
		setChanged();
		notifyObservers(this);

	}

	/**
	 * Erlaubt das Editieren des aktuellen Wahlzettels. Hierfür wird eine Kopie
	 * des bisherigen angelegt, um diesen wiederherstellen zu können.
	 */
	@Override
	public void editAktiverWahlzettel() throws UrneWahlzettelOffen {
		if (this.aktiverWahlzettel != null) {

			this.editWahlzettel = true;
			this.editBackupWahlzettel = aktiverWahlzettel.copyWahlzettel();
//			VotingLogger.getInstance().log(
//					"Am Stimmzettel " + this.editBackupWahlzettel.getId()
//							+ " werden Veränderungen durchgeführt.");

			if (this.originalWahlzettel==null){
				this.originalWahlzettel=aktiverWahlzettel.copyWahlzettel();
			}
		} else {
			throw new UrneWahlzettelOffen("Kein Wahlzettel in Bearbeitung.");
		}
		setChanged();
		notifyObservers(this);
	}

	/**
	 * Bestätigt die Änderungen am Wahlzettel und verwirft den zuvor gesicherten
	 * und beendet den Edit Modus
	 */
	@Override
	public void submitEditWahlzettel() {
//		VotingLogger.getInstance().log(
//				"Am Stimmzettel " + this.editBackupWahlzettel.getId()
//						+ " wurde folgendes verändert:");
		// Wahlzettel vergleichen:

//		for (Stimme ast : aktiverWahlzettel.getStimmen()) {
//			// gucken, ob es die Stimme auch im alten Stimmzettel gab:
//			Stimme st = editBackupWahlzettel.getStimmeByCandId(ast.getId());
//			if (st == null) {
//				VotingLogger.getInstance().log(
//						"Im Stimmzettel " + this.editBackupWahlzettel.getId()
//								+ " wurde die Stimme " + ast.getId() + " mit "
//								+ ast.getValue() + " Stimmen hinzugefügt.");
//
//			} else {
//				// prüfen, ob die Stimmen anders sind
//				if (ast.getValue() > st.getValue()) {
//					VotingLogger.getInstance().log(
//							"Im Stimmzettel "
//									+ this.editBackupWahlzettel.getId()
//									+ " wurde die Stimme " + ast.getId()
//									+ " um " + (ast.getValue() - st.getValue())
//									+ " erhöht");
//				}
//				if (ast.getValue() < st.getValue()) {
//					VotingLogger.getInstance().log(
//							"Im Stimmzettel "
//									+ this.editBackupWahlzettel.getId()
//									+ " wurde die Stimme " + ast.getId()
//									+ " um " + (ast.getValue() - st.getValue())
//									+ " verringert.");
//				}
//			}
//		}
		// jetzt noch die gelöschten prüfen:
//		for (Stimme ast : editBackupWahlzettel.getStimmen()) {
//			Stimme st = aktiverWahlzettel.getStimmeByCandId(ast.getId());
//			if (st == null) {
//				VotingLogger.getInstance().log(
//						"Im Stimmzettel " + this.editBackupWahlzettel.getId()
//								+ " wurde die Stimme " + ast.getId() + " mit "
//								+ ast.getValue() + " Stimmen entfernt.");
//
//			}
//		}
		// ----------
		this.editWahlzettel = false;
		editBackupWahlzettel = null;
		setChanged();
		notifyObservers(this);
	}

	/**
	 * verwirft die Änderungen am Wahlzettel und beendet den EditModus
	 */
	@Override
	public void discardEditWahlzettel() {
//		VotingLogger.getInstance().log(
//				"Am Stimmzettel " + this.editBackupWahlzettel.getId()
//						+ " wurden die Veränderungen verworfen.");
		this.editWahlzettel = false;
		aktiverWahlzettel = editBackupWahlzettel.copyWahlzettel();
		editBackupWahlzettel = null;
		setChanged();
		notifyObservers(this);
	}

	/**
	 * weist die Observer an sich zu aktualisieren.
	 */
	@Override
	public void updateModel() {
		setChanged();
		notifyObservers(this);
		setChanged();
		notifyObservers(this.ergebnis);
	}

	/**
	 * prüft, ob der Wahlzettel gültig ist. Die Entscheidung wird getroffen auf
	 * Basis des Valid-Flags und dem Ergebnis des RegelCheckers.
	 * 
	 * @return Boolean Status ob der Wahlzettel gültig ist
	 */
	@Override
	public boolean isWahlzettelGueltig() {
		if (aktiverWahlzettel != null
				&& rc.checkWahlzettel(this.aktiverWahlzettel).equals(Validity.VALID)
				&& aktiverWahlzettel.isValid().equals(Validity.VALID)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Legt einen Wahlzettel in die Urne und gibt die Id zurück mit dem er
	 * eingefügt wurde.
	 * 
	 * @return int Id des Wahlzettels
	 */
	@Override
	public int commitWahlzettel() throws UrneDoppelteIdGefunden,
			ErgebnisException, UrneIdUnbekannt, UrneEditOffen {
		int retId = -1;

		if (editBackupWahlzettel == null) {

			// prüfen, ob der Key eventuell schon vorhanden ist, dann Fehler
			// werfen
			if (this.aktiverWahlzettel.getCommited() == 0) {
				if (wahlzettel.containsKey(this.aktiverWahlzettel.getId())) {
					throw new UrneDoppelteIdGefunden(
							"Id des neuen Wahlzettels schon vorhanden. Ein Einfügen deswegen nicht möglich. Id nicht eindeutig!");
				}
			} else {
				// Hier prüfen, ob er schon vorhanden ist und den alten
				// rausnehmen bevor der neue eingefügt wird!!!!!
				if (wahlzettel.containsKey(this.aktiverWahlzettel.getId())) {
					Wahlzettel oldWahlzettel = wahlzettel
							.get(this.aktiverWahlzettel.getId());
					// Alle Werte des Wahlzettels löschen.
					ArrayList<Stimme> st = oldWahlzettel.getStimmen();
					for (Stimme stimme : st) {
						ergebnis.sub(stimme.getId(), stimme.getValue());
					}
					// Zieht bei den abgegebenen Wahlzetteln wieder einen ab,
					// weil er später auf jeden Fall eingefügt wird
					ergebnis.subAnzahlWahlzettel();
					// wird zudem geprüft, ob die Id zu den ungültigen gehört:
					if (!rc.checkWahlzettel(oldWahlzettel).equals(Validity.VALID)
							|| !oldWahlzettel.isValid().equals(Validity.VALID)) {
						ergebnis.subAnzahlUngueltigeWahlzettel();
					}

				} else {
					throw new UrneIdUnbekannt(
							"Commited wert kann nicht stimmen, Zettel noch nicht in der Urne!");
				}
			}
			//Änderungen protokollieren
			logChanges(this.aktiverWahlzettel.getId());
			// den Wahlzettel in die Urne legen
			this.aktiverWahlzettel.setCommited();
			if (this.aktiverWahlzettel.getCommited() <= 1) {
				lastId = this.aktiverWahlzettel.getId();
				VotingLogger.getInstance().log(
						"Stimmzettel "
								+ this.aktiverWahlzettel.getId()
								+ " wurde im Ergebnis gespeichert.");

			} else {
				VotingLogger.getInstance().log(
						"Stimmzettel "
								+ this.aktiverWahlzettel.getId()
								+ " erneut gespeichert.");
			}
			retId = this.aktiverWahlzettel.getId();
			wahlzettel.put(this.aktiverWahlzettel.getId(),
					this.aktiverWahlzettel);
			
			
			
			// Ergebnis aktualisieren
			ergebnis.addAnzahlWahlzettel();
			ergebnis.setLastId(retId);
			// Das Ergebnis nur zählen, wenn der Stimmzettel gültig ist:
			if (!rc.checkWahlzettel(this.aktiverWahlzettel).equals(Validity.INVALID)
					&& !aktiverWahlzettel.isValid().equals(Validity.INVALID)) {
				ArrayList<Stimme> st = this.aktiverWahlzettel.getStimmen();
				for (Stimme stimme : st) {
					ergebnis.add(stimme.getId(), stimme.getValue());
				}

			} else {
				ergebnis.addAnzahlUngueltigeWahlzettel();
			}
			// Auf jeden Fall den Stimmzettel löschen
			this.aktiverWahlzettel = null;
			this.originalWahlzettel=null;
		} else {
			throw new UrneEditOffen("Edit noch offen!");
		}

		setChanged();
		notifyObservers(ergebnis);
		setChanged();
		notifyObservers(this);
		return retId;
	}
	
	private void logChanges(int id){
		HashMap<Integer, String> toLog= new HashMap<Integer, String>();
		
		Wahlzettel o=this.originalWahlzettel;
		Wahlzettel j=this.aktiverWahlzettel;
		
		//Abbrechen, wenn keine Änderungen gemacht wurden, dann existiert die Kopie auch nicht.
		if (o==null || j==null){
			return;
		}
		
		HashMap<Integer, Stimme> oHM= o.getStimmenIds();
		HashMap<Integer, Stimme> jHM= j.getStimmenIds();
		
		for (Integer jInt : jHM.keySet()){
			Stimme temp= oHM.remove(jInt);
			if (temp==null){
				//Stimme wurde neu hinzugefügt
				toLog.put(jInt, jInt+": hinzugefügt mit "+jHM.get(jInt).getValue() +" Stimmen.");
			}else{
				//Stimme wurde vielleicht geändert
				if (temp.getValue()!=jHM.get(jInt).getValue() ){
					toLog.put(jInt, jInt+": geändert von "+temp.getValue()+" auf "+jHM.get(jInt).getValue() +" Stimmen.");
				}
			}
		}
		//Alle noch im Array oHM vorhandene Stimmen müssen gelöschte Stimmen sein.
		for (Stimme st : oHM.values()){
			//Stimmen wurden entfernt
			toLog.put(st.getId(), st.getId()+": gelöscht ("+st.getValue()+" Stimmen)");
		}
		
		List<Integer> sortedList = new ArrayList<Integer>();
		sortedList.addAll(toLog.keySet());
		Collections.sort (sortedList);
		 
		Iterator<Integer> iter = sortedList.iterator();
		VotingLogger.getInstance().log("");
		VotingLogger.getInstance().log("An Stimmzettel "+id+" wurden folgende Änderungen durchgeführt:");
		while (iter.hasNext()) {
			Integer key = iter.next();
			VotingLogger.getInstance().log("     "+toLog.get(key));
		}
		
		//VotingLogger.getInstance().log(toLog.toString());
	}

	/**
	 * den aktuellen Wahlzettel nicht speichern, sondern komplett verwerfen
	 */
	@Override
	public void discardWahlzettel() {
		this.aktiverWahlzettel = null;
		setChanged();
		notifyObservers(this);
	}

	/**
	 * Lädt einen Wahlzettel aus der Urne in den aktiven Wahlzettel, wenn er
	 * nicht vorhanden ist, wird ein Fehler geworfen
	 * 
	 * @param id
	 *            int: Id des Wahlzettels in der Urne
	 * @exception Exception
	 *                wird geworfen, wenn die id nicht bekannt ist
	 */
	@Override
	public void loadWahlzettel(int id) throws UrneIdUnbekannt,Exception {
		if (!wahlzettel.containsKey(id)) {
			throw new UrneIdUnbekannt(
					"Id des neuen Wahlzettels nicht vorhanden. Laden nicht möglich.");
		}
		this.aktiverWahlzettel = wahlzettel.get(id).copyWahlzettel();
		// this.aktiverWahlzettel=new Wahlzettel(wahlzettel.get(id), )
		setChanged();
		notifyObservers(this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tud.eVotingTallyAssistance.model.WahlurneInterface#isStatusLOCK()
	 */
	@Override
	public boolean isStatusLOCK() {
		return isGesperrt();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tud.eVotingTallyAssistance.model.WahlurneInterface#isStatusEDIT()
	 */
	@Override
	public boolean isStatusEDIT() {
		if (editBackupWahlzettel != null) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tud.eVotingTallyAssistance.model.WahlurneInterface#isStatusOPENBALLOT()
	 */
	@Override
	public boolean isStatusOPENBALLOT() {
		if (aktiverWahlzettel != null && editBackupWahlzettel == null) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tud.eVotingTallyAssistance.model.WahlurneReadOnlyInterface#isStatusINIT()
	 */
	@Override
	public boolean isStatusINIT() {
		if (lastId == 0 && aktiverWahlzettel == null) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tud.eVotingTallyAssistance.model.WahlurneReadOnlyInterface#isStatusWAITING
	 * ()
	 */
	@Override
	public boolean isStatusWAITING() {
		if (aktiverWahlzettel == null && !isGesperrt()) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tud.eVotingTallyAssistance.model.WahlurneReadOnlyInterface#isStatusREADONLY
	 * ()
	 */
	@Override
	public boolean isStatusREADONLY() {
		return readonly;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tud.eVotingTallyAssistance.model.WahlurneReadOnlyInterface#addObserver
	 * (java.util.Observer)
	 */
	@Override
	public void addObserver(Observer o) {
		// Wird aus dem Parentobjekt übernommen, aber so ist es übersichtlicher.
		super.addObserver(o);
	}

}
