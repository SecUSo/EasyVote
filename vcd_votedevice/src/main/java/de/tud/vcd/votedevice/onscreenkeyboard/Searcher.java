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
package de.tud.vcd.votedevice.onscreenkeyboard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;

import de.tud.vcd.votedevice.municipalElection.model.Candidate;
import de.tud.vcd.votedevice.municipalElection.model.Party;

/**
 * Implementiert die Suche in den Klassen des Modells. Das Modell hat kein Wissen was in ihm gesucht wird und wie gesucht wird. 
 * Dies wird über diese Klasse geregelt. Damit kann auch eine andere Suche implementiert werden, ohne das Modell zu verändern.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class Searcher {
	
	VCDSearchable _o;	//Bezug auf eigentliches Object
	HashSet<VCDSearchable> objArr;
	 HashSet<Character> nextChars;
	
	/**
	 * Erzeugt den Sucher. Als Parameter wird dabei ein durchsuchbares Objekt angegeben.
	 * @param o
	 */
	public Searcher(VCDSearchable o){
		objArr=new HashSet<VCDSearchable>();
		nextChars= new  HashSet<Character>();
		_o= o;
	}
	
	/**
	 * Erzeugt einen Sucher
	 */
	public Searcher(){
		objArr=new HashSet<VCDSearchable>();
		nextChars= new  HashSet<Character>();
	}
	
	/**
	 * Sucht in einem String nach dem Vorkommen von einem Pattern. 
	 * @param pattern
	 * @param string
	 */
	private void searchInString(String pattern, String string){
		int strAtPos=-2;
		//System.out.print("Suche in '"+string+"' nach '"+pattern+"'");
		while(strAtPos!=-1 && strAtPos<string.length()){
			strAtPos=string.indexOf(pattern, strAtPos+1);
			
			if (strAtPos!=-1){
				//wurde was gefunden
				//Object zum Ergebnis hinzufügen
				objArr.add(_o);
				//nächste zeichen nur, wenn String lang genug ist
				if ((strAtPos+pattern.length())<string.length()){
					nextChars.add(string.charAt(strAtPos+pattern.length()));
				}
			}
		}
	}
	
	/**
	 * Fragt das übergebene durchsuchbare Objekt an, ob der String enthalten ist. Dabei wird je nach Objekt
	 * verschiedene Felder des Modells abgefragt.
	 * 
	 * @param o
	 * @param str
	 */
	public void searchFor(VCDSearchable o, String str){
		//objArr.clear();
		//nextChars.clear();
		_o=o;
		str= str.toLowerCase();
		
		//unterscheide zwischen Partei und Kandidat
		if (_o instanceof Candidate){
			//Sowohl ID, Vorname als auch Nachname durchsuchen
			String forename=((Candidate)_o).getPrename().toLowerCase();
			searchInString(str, forename);
			
			String name=((Candidate)_o).getName().toLowerCase();
			searchInString(str, name);
			
			String id=String.valueOf(((Candidate)_o).getId());
			searchInString(str, id);
			
			String party=((Candidate)_o).getParty().toLowerCase();
			searchInString(str, party);
			
			
		}else if (_o instanceof Party){
			String name=((Party)_o).getName().toLowerCase();
			searchInString(str, name);
		}else{
			System.out.println("Suche nicht durchgeführt!!!");
			//return false;
		}
		
		//return false;
	}
	
	/**
	 * Liefert die zur verfügung stehenden Buchstaben zurück, die noch ein Ergebnis liefern. Damit wird die intelligente vorausschauende Eingabe 
	 * ermöglicht.
	 * @return
	 */
	public  HashSet<Character> nextSearchedCharacters(){
		return nextChars;
	}
	
	
	/**
	 * Liefert die gefundenen Objekte als sorrtiertes Array zurück
	 * 
	 * @return VCDSearchable[]
	 */
	public VCDSearchable[] getSearchedObjects() {
		VCDSearchable[] s = {};
		// Hier die Einträge sortieren
		TreeMap<Integer, VCDSearchable> ts = new TreeMap<Integer, VCDSearchable>();

		for (VCDSearchable obj : objArr) {
			if (obj instanceof Candidate) {
				Candidate c = (Candidate) obj;
				ts.put(c.getId(), c);
			} else if (obj instanceof Party) {
				Party p = (Party) obj;
				ts.put(p.getId(), p);
			}
		}

		ArrayList<VCDSearchable> temp = new ArrayList<VCDSearchable>();
		for (Integer entry : ts.keySet()) {
			temp.add(ts.get(entry));

		}

		return temp.toArray(s);
		// return objArr.toArray(s);
	}

}
