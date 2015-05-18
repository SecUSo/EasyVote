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
package de.tud.vcd.eVotingTallyAssistance.model;

import java.util.HashMap;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import de.tud.vcd.eVotingTallyAssistance.common.exceptions.ErgebnisException;


/**
 * Stellt das Ergebnis der Wahlzettel dar. Ist im Prinzip eine redunante
 * Darstellung, da das Auswerten der Wahlzettel das gleiche ergebnis liefern
 * muss. Jedoch beschleunigt dies die Anzeige auf den GUIs, da nicht durch alle
 * Wahlzettel gegangen werden muss, um die Stimmanzahl festzustellen.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 */
@Root(name = "ergebnis")
public class Wahlergebnis {
	private int lastId;
	@Element
	private int anzahlWahlzettel;
	@Element
	private String wahlleiter;
	@Element
	private int anzahlUngueltigeWahlzettel;
	@Path("wertung")
	@ElementMap(entry = "candidate", key = "id", attribute = true, inline = true)
	private HashMap<Integer, Integer> ergebnis;
	private boolean onlyView = false;

	/**
	 * Konstruktor zum normalen Erzeugen. Es wird der Wahlleiter mit ins
	 * Ergebnis übergeben als Verantwortlicher.
	 * 
	 * @param wahlleiter
	 *            String Name des Wahlleiters
	 */
	public Wahlergebnis(String wahlleiter) {
		this.wahlleiter = wahlleiter;
		ergebnis = new HashMap<Integer, Integer>();
	}

	/**
	 * Konstruktor nur für das Laden der XML Dateien. Da müssen alle Angaben
	 * gemacht werden, die in der XML gespeichert sind.
	 * 
	 * @param wahlleiter
	 * @param anzahlWahlzettel
	 * @param anzahlUngueltigeWahlzettel
	 * @param ergebnis
	 */
	public Wahlergebnis(
			@Element(name = "wahlleiter") String wahlleiter,
			@Element(name = "anzahlWahlzettel") int anzahlWahlzettel,
			@Element(name = "anzahlUngueltigeWahlzettel") int anzahlUngueltigeWahlzettel,
			@ElementMap(entry = "candidate", key = "id", attribute = true, inline = true) HashMap<Integer, Integer> ergebnis) {
		this.wahlleiter = wahlleiter;
		this.anzahlWahlzettel = anzahlWahlzettel;
		this.anzahlUngueltigeWahlzettel = anzahlUngueltigeWahlzettel;
		this.ergebnis = ergebnis;
		this.onlyView = true;
		// ergebnis= new HashMap<Integer,Integer>();
	}

	/**
	 * @return the wahlleiter
	 */
	public String getWahlleiter() {
		return wahlleiter;
	}

	/**
	 * @param wahlleiter
	 *            the wahlleiter to set
	 */
	public void setWahlleiter(String wahlleiter) {
		this.wahlleiter = wahlleiter;
	}

	public void setLastId(int lastId) {
		this.lastId = lastId;
	}

	public int getLastId() {
		return this.lastId;
	}

	/**
	 * @return the anzahlWahlzettel
	 */
	public int getAnzahlWahlzettel() {
		return anzahlWahlzettel;
	}

	/**
	 * @return is onlyView? keine Farbwechsel machen.
	 */
	public boolean isOnlyView() {
		return onlyView;
	}

	/**
	 * @return the anzahlUngueltigeWahlzettel
	 */
	public int getAnzahlUngueltigeWahlzettel() {
		return anzahlUngueltigeWahlzettel;
	}

	/**
	 * Erhöht die Anzahl der Wahlzettel um eins
	 */
	public void addAnzahlWahlzettel() {
		this.anzahlWahlzettel++;
	}

	/**
	 * Erhöht die Anzahl der ungültigen Wahlzettel um eins
	 */
	public void addAnzahlUngueltigeWahlzettel() {
		this.anzahlUngueltigeWahlzettel++;
	}

	/**
	 * Verringert die Anzahl der Wahlzettel
	 * 
	 * @throws ErgebnisException
	 */
	public void subAnzahlWahlzettel() throws ErgebnisException {
		this.anzahlWahlzettel--;
		if (this.anzahlWahlzettel > 0) {
			this.anzahlWahlzettel--;
		} else {
			this.anzahlWahlzettel = 0;
			throw new ErgebnisException(
					"Anzahl der Stimmzettel kann nicht kleiner null sein.");
		}
	}

	/**
	 * Verringert die Anzahl der ungültigen Wahlzettel
	 * 
	 * @throws ErgebnisException
	 */
	public void subAnzahlUngueltigeWahlzettel() throws ErgebnisException {
		if (anzahlUngueltigeWahlzettel > 0) {
			this.anzahlUngueltigeWahlzettel--;
		} else {
			this.anzahlUngueltigeWahlzettel = 0;
			throw new ErgebnisException(
					"Anzahl ungültiger Stimmzettel kann nicht kleiner null sein.");
		}

	}

	/**
	 * fügt dem Kandidate den Wert von value hinzu.
	 * 
	 * @param index
	 *            int KandidatenId
	 * @param value
	 *            int Wert der Kreuze
	 */
	public void add(int index, int value) {
		int bisherigerStand = 0;
		if (ergebnis.containsKey(index)) {
			bisherigerStand = ergebnis.get(index);
		}
		ergebnis.put(index, value + bisherigerStand);
	}

	/**
	 * subtrahiert dem Kandidate den Wert von value.
	 * 
	 * @param index
	 *            int KandidatenId
	 * @param value
	 *            int Wert der Kreuze
	 */
	public void sub(int index, int value) throws ErgebnisException {
		int bisherigerStand = 0;
		if (ergebnis.containsKey(index)) {
			bisherigerStand = ergebnis.get(index);
		}
		if (bisherigerStand >= value) {
			ergebnis.put(index, bisherigerStand - value);
		} else {
			ergebnis.put(index, 0);
			throw new ErgebnisException(
					"Stimmenanzahl eines Kandidaten kann nie unter 0 gehen.");
		}

	}

	/**
	 * liefert das Ergebnis als Map zurück, so dass es weiterverarbeitet werden
	 * kann in der GUI.
	 * 
	 * @return HashMap<Integer, Integer> über alle Ergebnisse
	 */
	public HashMap<Integer, Integer> getErgebnis() {
		return ergebnis;
	}

	/**
	 * Das Ergebnis für einen speziellen Kandidaten abfragen.
	 * 
	 * @param index
	 *            int KandidatenId
	 * @return int Anzahl der Stimmen für diesen Kandidaten
	 */
	public int getErgebnisOfCandidate(int index) {
		if (ergebnis.containsKey(index))
			return ergebnis.get(index);
		else
			return 0;
	}

}
