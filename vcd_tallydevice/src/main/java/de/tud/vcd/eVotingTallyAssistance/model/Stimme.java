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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

/**
 * Implementiert das Verhalten von genau einer Stimme die für einen Kandidaten
 * bestimmt ist. Die Id ist dabei der Kandidatenname. In Wertung werden nun die
 * Daten gespeichert. Also für jede Position genau die angekreuzten
 * Markierungen.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 */
@Root(name = "Kandidat")
public class Stimme implements Cloneable, Comparable<Stimme>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Element
	private int id;
	@Path("wertung")
	@ElementMap(entry = "stimme", key = "id", attribute = true, inline = true)
	private HashMap<Integer, Integer> wertung;
	@Element
	private int reduzierteKreuze;
	/**
	 * Eine Stimme muss immer einen Kandidaten (Id) zugehören, daher muss diese
	 * dem Konstruktor übergeben werden.
	 * 
	 * @param id
	 */
	
	//Juri:
	private int manualvotes;
	
	public Stimme(int id) {
		super();
		this.id = id;
		wertung = new HashMap<Integer, Integer>(3);
		reduzierteKreuze=0;
		this.manualvotes = 0;
	}

	/**
	 * Konstruktor nur für das Laden der XML Dateien. Da müssen alle Angaben
	 * gemacht werden, die in der XML gespeichert sind.
	 * 
	 * @param id
	 * @param wertung
	 */
	public Stimme(
			@Element(name = "id") int id,
			@ElementMap(entry = "stimme", key = "id", attribute = true, inline = true) HashMap<Integer, Integer> wertung,
			@Element(name="reduzierteKreuze") int reduzierteKreuze) {
		super();
		this.id = id;
		wertung = new HashMap<Integer, Integer>(3);
		this.wertung = wertung;
		this.reduzierteKreuze=reduzierteKreuze;

	}

	/**
	 * Klont die Stimme als echtes Objekt und nicht nur als Referenz. Wird zum
	 * klonen des Wahlzettels benötigt um eine echte tiefe Kopie zu erstellen.
	 */
	@SuppressWarnings("unchecked")
	public Stimme clone() {
		Stimme st = new Stimme(id);
		st.wertung = (HashMap<Integer, Integer>) wertung.clone();
		return st;
	}

	/**
	 * Ändert den Status der Stimme an der Position index auf den Wert val. Es
	 * wäre damit auch denkbar, Zahlen zuzulassen.
	 * 
	 * @param index
	 *            : int Position der Stimme
	 * @param val
	 *            : int: Wert der Stimme an der Position
	 */
	public void change(int index, int val) {
		wertung.put(index, val);
	}
	
	
	public void setVotes(int val){
		//Werte setzen für die die eine Stimme bekommen
		for (int i=0;i<val;i++){
			wertung.put(i, 1);
		}
		//alle anderen auf 0 setzen, so dass sie keine Stimme bekommen
		for (int i=val;i<wertung.size();i++){
			wertung.put(i, 0);
		}
		
		
	}

	/**
	 * Ändert den Status der Stimme an der Position index auf den Wert val.
	 * Dabei ist für ein Kreuz nur die Unterscheidung zwischen gesetzt und nicht
	 * gesetzt möglich.
	 * 
	 * @param index
	 *            : int Position der Stimme
	 * @param val
	 *            : boolean: Status der Checkbox
	 */
	public void change(int index, boolean val) {
		int value = 0;
		if (val)
			value = 1;

		wertung.put(index, value);
		
	}

	/**
	 * Gibt den Wert der Stimme zurück. Hat dieser Kandidat zum Beispiel zwei
	 * Kreuze erhalten mit dieser Stimme liefert es den Wert 2.
	 * 
	 * @return int: Wert der Stimme
	 */
	public int getValue() {
		int value = 0;
		for (Entry<Integer, Integer> wert : wertung.entrySet()) {
			value += wert.getValue();
		}
		return value-reduzierteKreuze;
	}
	
	public int getReduzierteStimmen(){
		return reduzierteKreuze;
	}
	
	public void setReduzierteStimmen(int reduzierteKreuze){
		this.reduzierteKreuze=reduzierteKreuze;
	}
	
	/**
	 * Gibt den Wert der Stimme zurück. Hat dieser Kandidat zum Beispiel zwei
	 * Kreuze erhalten mit dieser Stimme liefert es den Wert 2.
	 * 
	 * @return int: Wert der Stimme
	 */
	public int getAngekreuzteStimmen() {
		int value = 0;
		for (Entry<Integer, Integer> wert : wertung.entrySet()) {
			value += wert.getValue();
		}
		return value;
	}

	/**
	 * getValueAtPos liefert den Zustand der Stimme an der übergebenen Position.
	 * Dies ist für die korrekte Darstellung der Stimme auf der GUI wichtig, da
	 * hier nicht nur der Wert sondern auch die genaue Position benannt sein
	 * muss.
	 * 
	 * @param pos
	 *            : abzufragende Stelle der Stimme
	 * @return int: Wert der Stimme.
	 */
	public int getValueAtPos(int pos) {
		try {
			int val = wertung.get(pos);
			return val;
		} catch (Exception e) {
			// System.out.println("nicht gefudnen"+pos);
			return 0;
		}
	}

	/**
	 * Liefert die Id der Stimme. Also die Kandidatennummer.
	 * 
	 * @return int: Kandidatennummer
	 */
	public int getId() {
		return id;
	}

	/**
	 * Setzt die Id des Kandidaten. Wird gebraucht, wenn nachträglich ein
	 * Kandidat verändert wird, weil zum Beispiel die Zahl falsch gelesen wurde.
	 * 
	 * @param id
	 *            : int Kandidatennummer
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Um die Stimmen zu ordnen, existiert die Methode compareTo, die
	 * überhcrieben wird, um eine eigene Ordnung zu ermöglichen. Die Intension
	 * ist die Stimmen nach der Id zu ordnen.
	 */
	@Override
	public int compareTo(Stimme st) {
		if (this.getId() < st.getId())
			return -1;
		if (this.getId() > st.getId())
			return 1;
		// Weder noch also gleich=0
		return 0;

	}
	
	//Juri:
	public void setmanualVotes(int v){
		this.manualvotes = v;
	}
	
	public int getmanualVotes(){
		return this.manualvotes;
	}
}
