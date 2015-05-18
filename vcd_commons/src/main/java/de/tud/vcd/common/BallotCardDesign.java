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
/**
 * 
 */
package de.tud.vcd.common;

/**
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
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

import de.tud.vcd.common.exceptions.BallotCardDesignDeseralizerException;
import de.tud.vcd.common.exceptions.BallotCardDesignKeyNotFoundException;
import de.tud.vcd.common.exceptions.DesignKeyNotInXMLException;
import de.tud.vcd.common.exceptions.XMLCandidateNotFoundException;




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
public class BallotCardDesign {

	private static String FILENAME="wahlzettel.xml";
	/**
	 * erlaubte Werte für die Designangaben:
	 */
	public enum DesignKeys {
		//ANZAHLPARTEIENGLEICHZEITIG, MANUELLESUNGUELTIG , QRCODELESEN, QR_X, QR_Y, QR_W, QR_H, STARTHOEHE, INFOBOXSTARTHOEHE, ABSTANDRAND, VSPACE, HSPACE, LINEHEIGHT, NAMEWIDTH, NRWIDTH, KANDIDATENPROSPALTE, VOTESPROKANDIDAT, MAXSTIMMEN, PAGEWIDTH, PAGEHEIGHT
		PAGEWIDTH, 
		PAGEHEIGHT, 
		MARGIN_TOP,
		MARGIN_BOTTOM,
		MARGIN_LEFT,
		MARGIN_RIGHT,
		COLUMN_WIDTH,
		BEGIN_VOTES_COLUMN,
		HEIGHT_INFOBOX,
		WIDTH_CROSSED_CANDIDATES,
		PRINT_QR_CODE,
		VOTESPROKANDIDAT,
		ANZAHLPARTEIENGLEICHZEITIG,
		MANUELLESUNGUELTIG,
		MAXSTIMMEN,
		WIDTH_PARTYBOX,
		MAXPARTIES,
		FONTSIZE_CROSSED,
		FONTSIZE_CROSSED_MIN,
		FONTSIZE_CROSSED_HEADLINE,
		MARGIN_CROSSED,
		FONTSIZE_HEADLINE,
		FONTSIZE_HEADLINE_SUB
		
	}

	@Element
	private String election_id;

	@Element
	private String election_name;

	@ElementArray
	private String[] party; 
	
	@Path("design")
	@ElementMap(entry = "property", key = "key", attribute = true, inline = true)
	private HashMap<DesignKeys, Integer> design;

	@Path("candidates")
	@ElementList(entry = "candidate", inline = true)
	private ArrayList<XMLCandidate> candidates;

	private HashMap<Integer, XMLCandidate> mapOfCandidates = null;

	private static BallotCardDesign instance = null;

	/**
	 * liefert die Instanz des Singleton Pattern. So wird das Objekt nur einmal
	 * angelegt und verfügt über das Wissen.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static synchronized BallotCardDesign getInstance(InputStream filename) throws Exception {
		if (instance == null) {
			instance = loadXML();
		}
		return instance;
	}
	
	
	/**
	 * liefert die Instanz des Singleton Pattern. So wird das Objekt nur einmal
	 * angelegt und verfügt über das Wissen.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static synchronized BallotCardDesign getInstance() throws Exception  {
		if (instance == null) {
			instance = loadXML();
		}
		return instance;
	}

	private BallotCardDesign() {

	}

	private static String getJarExecutionDirectory()
	  {
	    File propertiesFile = new File("");
	 
	    if (!propertiesFile.exists())
	    {
	      try
	      {
	        CodeSource codeSource = BallotCardDesign.class.getProtectionDomain().getCodeSource();
	        File jarFile = new File(codeSource.getLocation().toURI().getPath());
	        String jarDir = jarFile.getParentFile().getPath();
	        propertiesFile = new File(jarDir + System.getProperty("file.separator") );
	        return propertiesFile.getAbsolutePath()+System.getProperty("file.separator");
	      }
	      catch (Exception e)
	      {
//	    	  //DEBUG:
//	    	  JOptionPane
//				.showConfirmDialog(
//						null,
//						e.getMessage()+e.getStackTrace(),
//						"Fehler:", JOptionPane.DEFAULT_OPTION,
//						JOptionPane.ERROR_MESSAGE);
	      }
	    }
	 
	    return System.getProperty("file.separator");
	  }
	
	private static BallotCardDesign loadXML() throws Exception{
	       String filename = getJarExecutionDirectory()+FILENAME;
     	   System.out.println("WZD: "+filename);
				//DEBUG:
//				JOptionPane
//				.showConfirmDialog(
//						null,
//						"WZD: "+filename,
//						"Fehler:", JOptionPane.DEFAULT_OPTION,
//						JOptionPane.ERROR_MESSAGE);
		Serializer deserializer = new Persister();
				
		File source = new File(filename);
		//System.out.println("exist"+source.exists());
		BallotCardDesign wzd;
		try {
			wzd = deserializer.read(BallotCardDesign.class, source);
		} catch (IllegalArgumentException e){
			String msg= e.getMessage().substring(e.getMessage().lastIndexOf(".")+1);
			throw new BallotCardDesignKeyNotFoundException("Wert "+msg+" darf nicht in der wahlzettel.xml stehen.");
		} catch (FileNotFoundException e) {
		
			throw new BallotCardDesignDeseralizerException(
					"Das System kann die angegebene Datei nicht finden (" + filename
					+ ").");
			
		} catch (Exception e) {	
			e.printStackTrace();
			
			throw new BallotCardDesignDeseralizerException(
					"Das Lesen der Wahlzettel-XML Datei (" + filename
							+ ") ist fehlgeschlagen.");
		}
		return wzd;
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
//	private static BallotCardDesign loadXML(InputStream stream) throws BallotCardDesignKeyNotFoundException {
//
//		//String filename = ConfigHandler.getInstance().getConfigValue(
//		//		ConfigVars.WAHLZETTELDESIGNDATEI);
//
//		Serializer deserializer = new Persister();
//		//File source = new File(filename);
//		BallotCardDesign wzd;
//		try {
//			wzd = deserializer.read(BallotCardDesign.class, stream);
//		} catch (Exception e) {
//			throw new BallotCardDesignKeyNotFoundException(
//					"Das Lesen der Wahlzettel-XML Datei: (" + stream.toString()
//							+ ") ist fehlgeschlagen.");
//		}
//		return wzd;
//		// System.out.println(example.election_name);
//
//	}

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
	 * @throws DesignKeyNotInXMLException 
	 */
	public int getDesignValue(DesignKeys key) throws DesignKeyNotInXMLException {
		int value;
		try {
			value = design.get(key);
		} catch (Exception e) {
			// Wenn der Wert nicht vorhanden ist, dann einfach auf 0 setzen und
			// warnen
			throw new DesignKeyNotInXMLException("DesignKey "+key+" fehlt in der XML Datei. Einfügen und Programm neu starten.");
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
	 * @throws XMLCandidateNotFoundException 
	 */
	public CandidateImportInterface getCandidate(int id) throws XMLCandidateNotFoundException {
		if (mapOfCandidates == null) {
			mapOfCandidates = new HashMap<Integer, XMLCandidate>();
			for (XMLCandidate cand : candidates) {
				mapOfCandidates.put(cand.getId(), cand);
			}
		}
		CandidateImportInterface c= mapOfCandidates.get(id);
		if (c==null){
			throw new XMLCandidateNotFoundException();
		};
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
			mapOfCandidates = new HashMap<Integer, XMLCandidate>();
			for (XMLCandidate cand : candidates) {
				mapOfCandidates.put(cand.getId(), cand);
			}
		}
		ArrayList<Integer> arrayList = new ArrayList<Integer>(
				mapOfCandidates.keySet());
		Collections.sort(arrayList);
		return arrayList;
	}
	
	/**
	 * Liefert die Kandidaten als Arraylist zurück.
	 * @return
	 */
	public ArrayList<CandidateImportInterface> getCandidates() {
		ArrayList<CandidateImportInterface> cii= new ArrayList<CandidateImportInterface>();
		for (XMLCandidate cand : candidates) {
			cii.add(cand);
		}
		
		return cii;
		
	}
	
	/**
	 * @return the party
	 */
	public String[] getParties() {
		return party;
	}
	
	public int getMaxFelder() throws DesignKeyNotInXMLException{
		return getDesignValue(DesignKeys.MAXSTIMMEN)+getDesignValue(DesignKeys.MAXPARTIES)+1;
	}
	
	public int getMaxFelderEachSide() throws DesignKeyNotInXMLException{
		return (int)Math.floor((double)(getDesignValue(DesignKeys.MAXSTIMMEN)+getDesignValue(DesignKeys.MAXPARTIES)+1)/2);
	}

}