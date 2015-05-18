package de.tud.vcd.eVotingTallyAssistance.common;

import java.io.File;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import de.tud.vcd.eVotingTallyAssistance.common.ConfigHandler.ConfigVars;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.ConfigFileException;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.WahlzettelDesignKeyNotFoundException;


/**
 * Singleton zum Laden der Wahlzetteldaten und des Designs. Sowohl mögliche
 * Kandidaten als auch das Design sind hier vorhanden und werden bei der ersten
 * Verwendung aus einer XML Datei gelesen. Es existiert kein schreibender
 * Zugriff auf die Daten.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 */
@Root
public class WahlzettelDesign {

	/**
	 * erlaubte Werte für die Designangaben:
	 */
	public enum DesignKeys {
		ANZAHLPARTEIENGLEICHZEITIG, MANUELLESUNGUELTIG, QRCODELESEN, QR_X, QR_Y, QR_W, QR_H, STARTHOEHE, INFOBOXSTARTHOEHE, ABSTANDRAND, VSPACE, HSPACE, LINEHEIGHT, NAMEWIDTH, NRWIDTH, KANDIDATENPROSPALTE, VOTESPROKANDIDAT, MAXSTIMMEN, PAGEWIDTH, PAGEHEIGHT
	}

	@Element
	private String election_id;

	@Element
	private String election_name;

	//@Path("parties")
	@ElementArray
	private String[] party;    
	
	@Path("design")
	@ElementMap(entry = "property", key = "key", attribute = true, inline = true)
	private HashMap<DesignKeys, Integer> design;

	@Path("candidates")
	@ElementList(entry = "candidate", inline = true)
	private ArrayList<Candidate> candidates;

	private HashMap<Integer, Candidate> mapOfCandidates = null;

	private static WahlzettelDesign instance = null;

	/**
	 * liefert die Instanz des Singleton Pattern. So wird das Objekt nur einmal
	 * angelegt und verfügt über das Wissen.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static synchronized WahlzettelDesign ggetInstance() throws Exception {
		if (instance == null) {
			instance = loadXML();
		}
		return instance;
	}

	
	
	
	private WahlzettelDesign() {

	}

	/**
	 * @return the party
	 */
	public String[] getParty() {
		return party;
	}



	public static String getJarExecutionDirectory()
	  {
	    File propertiesFile = new File("");
	 
	    if (!propertiesFile.exists())
	    {
	      try
	      {
	        CodeSource codeSource = ConfigHandler.class.getProtectionDomain().getCodeSource();
	        File jarFile = new File(codeSource.getLocation().toURI().getPath());
	        String jarDir = jarFile.getParentFile().getPath();
	        propertiesFile = new File(jarDir + System.getProperty("file.separator") );
	        return propertiesFile.getAbsolutePath()+System.getProperty("file.separator");
	      }
	      catch (Exception ex)
	      {
	      }
	    }
	 
	    return System.getProperty("file.separator");
	  }
	



	/**
	 * lädt die XML Datei in die Klasse rein. Sollte die Datei nicht gefunden
	 * werden, wird null zurückgegeben.
	 * 
	 * @return
	 * @throws ConfigFileException
	 * @throws WahlzettelDesignKeyNotFoundException
	 * @throws Exception
	 */
	private static WahlzettelDesign loadXML() throws ConfigFileException,
			WahlzettelDesignKeyNotFoundException {

		String filename = getJarExecutionDirectory()+ ConfigHandler.getInstance().getConfigValue(
				ConfigVars.WAHLZETTELDESIGNDATEI);
		//System.out.println("WZD: "+filename);
		Serializer deserializer = new Persister();
		File source = new File(filename);
		//System.out.println("exist"+source.exists());
		WahlzettelDesign wzd;
		try {
			wzd = deserializer.read(WahlzettelDesign.class, source);
		} catch (Exception e) {
			throw new WahlzettelDesignKeyNotFoundException(
					"Das Lesen der Wahlzettel-XML Datei (" + filename
							+ ") ist fehlgeschlagen.");
		}
		return wzd;
		// System.out.println(example.election_name);

	}

	/**
	 * @return the election_id
	 */
	public String getElection_id() {
		return election_id;
	}

	/**
	 * @return the election_name
	 */
	public String getElection_name() {
		return election_name;
	}

	/**
	 * Liefert den Wert für eine Designvariable zurück. Als Key können alle
	 * Werte aus der Auflistung DesignKeys verwendet werden.
	 * 
	 * @param key
	 *            : Key aus der ENUM DesignKeys.
	 * @return int : Wert der Variable
	 */
	public int getDesignValue(DesignKeys key) {
		int value;
		try {
			value = design.get(key);
		} catch (Exception e) {
			// Wenn der Wert nicht vorhanden ist, dann einfach auf 0 setzen und
			// warnen
			JOptionPane
					.showConfirmDialog(
							null,
							"Der Wert von "
									+ key
									+ " konnte nicht gelesen werden. Dieser muss in der Wahlzettel-Datei nachgetragen werden. Er wird solange auf 0 gesetzt.",
							"Fehlermeldung:", JOptionPane.DEFAULT_OPTION,
							JOptionPane.ERROR_MESSAGE);
			value = 0;
		}
		// if (value!=null)throw new
		// WahlzettelDesignKeyNotFoundException("Der Key"+ key +
		// " wurde nicht gefunden.");
		return value;
	}

	/**
	 * Liefert den angeforderten Kandidaten aus der Liste zurück. Wenn es die
	 * erste Anforderung ist, wird die Liste erstmal umkopiert in eine HashMap
	 * zur schnelleren Bearbeitung ab dem zweiten Zugriff auf einen Kandidaten.
	 * 
	 * @param id
	 *            : Id des Kandidaten
	 * @return : den Kandidaten oder null, falls diese ID nicht existiert.
	 */
	public Candidate getCandidate(int id) {
		if (mapOfCandidates == null) {
			mapOfCandidates = new HashMap<Integer, Candidate>();
			for (Candidate cand : candidates) {
				mapOfCandidates.put(cand.getId(), cand);
			}
		}
		return mapOfCandidates.get(id);
	}

	/**
	 * Liefert alle vorhandenen Kandidatennummern aus der Hashmap. Sollte die
	 * Hashmap noch nicht gefüllt sein, so wird diese beim ersten Aufruf
	 * gefüllt, um später schneller drauf zugreifen zu können.
	 * 
	 * @return ArrayList<Integer> : die Keys der Hashmap, sind die
	 *         KandidatenIds.
	 */
	public ArrayList<Integer> getCandidateIds() {
		if (mapOfCandidates == null) {
			mapOfCandidates = new HashMap<Integer, Candidate>();
			for (Candidate cand : candidates) {
				mapOfCandidates.put(cand.getId(), cand);
			}
		}
		ArrayList<Integer> arrayList = new ArrayList<Integer>(
				mapOfCandidates.keySet());
		Collections.sort(arrayList);
		return arrayList;
	}

}
