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
package de.tud.vcd.eVotingTallyAssistance.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import de.tud.vcd.common.BallotCardDesign;
import de.tud.vcd.common.CandidateImportInterface;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.CandidateNotFoundException;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.CandidateNotKnownException;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.DoubleCandidateIdException;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.PartyNotExistsException;
import de.tud.vcd.eVotingTallyAssistance.model.RegelChecker.Validity;


/**
 * Stimmzettel: Beinhaltet alle Daten die zu einem gescannten Stimmzettel
 * gehören. Also der Id mit der er später gespeichert wird, den Stimmen, dem
 * Gültigkeitsflag, ob der Wahlzettel ungültig sein soll und dem Regelchecker
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 */
@Root(name = "Wahlzettel")
public class Wahlzettel {
	@Attribute
	private int id; // die eindeutige ID des Wahlzettels in der Urne, zum
					// Wiederfinden und ändern
	@Attribute
	private boolean valid; // markiert direkt ungültig abgegebene Stimmen
	@ElementList
	private ArrayList<Stimme> stimmen;// die enthaltenen Stimmen für die
										// jeweilen Kandidaten
	private RegelChecker rc;// der Regelchecker, der für diesen Zettel
							// verantwortlich ist.
	@Attribute
	private int commited;

	@Attribute
	private int party;
	
	/**
	 * Der normale Konstruktor, um die Klasse zu erzeugen mit einer id und einem
	 * RegelChecker, der den Wahlzettel überprüft.
	 * 
	 * @param id
	 * @param rc
	 */
	public Wahlzettel(int id, RegelChecker rc) {
		this.id = id;
		this.rc = rc;
		stimmen = new ArrayList<Stimme>();
		valid = false;
		commited = 0;
		party=-1;

	}

	/**
	 * @return the party
	 */
	public int getParty() {
		return party;
	}

	/**
	 * @throws Exception 
	 * @param party the party to set
	 * @throws  
	 */
	public void setParty(int party) throws Exception  {
		String[] parties=BallotCardDesign.getInstance().getParties();
		if (!(party >=0 && party<=parties.length)){
			throw new PartyNotExistsException("Partei mit der Listennummer: "+party+" existiert nicht.");
		}
		this.party = party;
	}

	/**
	 * Konstruktor nur für das Laden der XML Dateien. Da müssen alle Angaben
	 * gemacht werden, die in der XML gespeichert sind.
	 * 
	 * @param id
	 * @param valid
	 * @param stimmen
	 * @param commited
	 */
	public Wahlzettel(@Attribute(name = "id") int id,
			@Attribute(name = "valid") boolean valid,
			@ElementList(name = "stimmen") ArrayList<Stimme> stimmen,
			@Attribute(name = "commited") int commited,
			@Attribute(name = "party") int party) {
		this.id = id;
		this.rc = new RegelChecker();
		this.stimmen = stimmen;
		this.valid = valid;
		this.commited = commited;
		this.party=party;
		//sortieren:
		Collections.sort(stimmen);

	}

	public void setValid(boolean b) {
		valid = b;
	}

	public boolean getValidFlag() {
		return valid;
	}
	
	
	public boolean reduceVotes(){
		int MAXSTIMMEN=71;
		int MAXVOTES=3;
		
		HashSet<Integer> parties=new HashSet<Integer>();
		int gesamtStimmen = 0;
		for (Stimme st : this.getStimmen()) {
			// Prüfen, ob die Anzahl an Stimmen für diesen Kandidaten das
			// Maximum nicht überschreitet
			gesamtStimmen += st.getAngekreuzteStimmen();
			parties.add(st.getId()/100);
		}
		//System.out.println("Anzahl Stimmen gesamt: "+gesamtStimmen);
		//System.out.println("Maximale Stimmen erlaubt: "+ MAXSTIMMEN);
		int zuReduzieren=gesamtStimmen-MAXSTIMMEN;
		
		//Prüfen, ob der Status korrigierbar wäre:
		boolean zuruecksetzen=false;
		if (!(gesamtStimmen>MAXSTIMMEN && parties.size()<=1)){
			//Abbrechen, da es nichts zu korrigieren gibt
			//return false;
			//Da mehrere Parteien alles zurücksetzen
			zuReduzieren=0;
			zuruecksetzen=true;
		}
		
		//System.out.println("Zureduzierende Stimmen: "+zuReduzieren);
		
		//Jetzt reduzieren, da es möglich ist:
		int round=1;
		while (zuruecksetzen || zuReduzieren>0 && round<=MAXVOTES){//schwaches abbruchkriterium setzen, falls nicht genügend reduziert werden kann
			zuruecksetzen=false;
			int anzKandidaten=stimmen.size();
			for (int j=round;j<=MAXVOTES;j++){
				for (int i=anzKandidaten-1;i>=0;i--){
					if (zuReduzieren>0 && (stimmen.get(i).getAngekreuzteStimmen()==j)){
							zuReduzieren--;
							//System.out.println("REduzieren: " + c.getId());
							stimmen.get(i).setReduzierteStimmen(round);
						
					}else{
						//in der ersten Runde alle reduzierungen zurücknehmen, wenn nicht oben schon reduziert wurde
						if (j==1 && round==1){
							stimmen.get(i).setReduzierteStimmen(0);
						}
					}
				}
			}
			round++;//Maximal so oft durchlaufen wie Kreuze pro Person möglich sind.
		}
		
		
		return false;
	}
	
	
	public int countVotes(){
		int votes=0;
		for (Stimme st : stimmen){
			votes+=st.getAngekreuzteStimmen();
		}
		
		return votes;
	}

	/**
	 * prüft, ob der Stimmzettel nach der übergebenen Regel überhaupt gültig
	 * ist. Zudem wird das Valid-Flag ausgewertet, welches bei einem ungültigen
	 * Stimmzettel gesetzt wird.
	 * 
	 * @return Boolean Gibt an, ob der Status auf Gültig gesetzt ist und der
	 *         RegelChecker keine Einwände hat.
	 */
	public Validity isValid() {
		if (!valid){
			return Validity.INVALID;
		}
		Validity regel = rc.checkWahlzettel(this);
		return regel;
	}

	/**
	 * @return the commited
	 */
	public int getCommited() {
		return commited;
	}

	/**
	 * @param commited
	 *            the commited to set
	 */
	public void setCommited() {
		this.commited++;
	}

	public int getId() {
		return id;
	}

	public void addStimme(Stimme stimme) throws DoubleCandidateIdException,
			CandidateNotKnownException {
		// Prüfen, ob Stimme schon in Liste vorhanden:
		if (getStimmeByCandId(stimme.getId()) != null)
			throw new DoubleCandidateIdException();
		// Prüfen, ob Kandidatennummer überhaupt im Verzeichnis ist:
		try {
			if (isCandidateInVotingList(stimme.getId())) {
				stimmen.add(stimme);
			} else {
				throw new CandidateNotKnownException();
			}
		} catch (CandidateNotKnownException e) {
			throw new CandidateNotKnownException();
		} catch (Exception e) {
			throw new CandidateNotKnownException(
					"Kandidatendatei konnte nicht gelesen werden.");
		}
		// sortieren:
		Collections.sort(stimmen);
		reduceVotes();
	}

	/**
	 * Löscht einen Kandidaten vom Stimmzettel anhand der Id des Kandidaten.
	 * 
	 * @param candidate_id
	 */
	public void removeStimme(int candidate_id) {
		for (Stimme st : stimmen) {
			if (st.getId() == candidate_id) {
				stimmen.remove(st);
				break;
			}
		}
		// sortieren:
		Collections.sort(stimmen);
		reduceVotes();
	}
	
	

	public ArrayList<Stimme> getStimmen() {
		return stimmen;
	}

	/**
	 * Liefert die Stimme an der Position zurück.
	 * 
	 * @param id
	 * @return
	 */
	public Stimme getStimme(int id) {
		return stimmen.get(id);
	}

	/**
	 * Sucht nach der Stimme aber anhand der Stimmen-Id und nicht anhand der
	 * Position im Array.
	 * 
	 * @param id
	 *            int:KandidatenId
	 * @return Stimme oder NULL, falls sie nicht gefunden wurde.
	 */
	public Stimme getStimmeByCandId(int id) {
		for (Stimme st : stimmen) {
			if (st.getId() == id) {
				return st;
			}
		}
		return null;
	}

	
	public HashMap<Integer, Stimme> getStimmenIds(){
		HashMap<Integer, Stimme> hash=new HashMap<Integer, Stimme>();
		for (Stimme st : stimmen) {
			hash.put(st.getId(), st);
		}
		return hash;
	}
	
	/**
	 * Ändert die Id eines Kandidaten, falls diese falsch erkannt wurde.
	 * 
	 * @param oldId
	 *            :alte Id des Kandidaten
	 * @param newId
	 *            :neue Id des Kandidaten
	 * @throws CandidateNotFoundException
	 *             ,DoubleCandidateIdException, CandidateNotKnownException
	 */
	public void changeStimme(int oldId, int newId)
			throws CandidateNotFoundException, DoubleCandidateIdException,
			CandidateNotKnownException {
		// sucht nach der alten Stimme
		Stimme oldStimme = null;
		oldStimme = getStimmeByCandId(oldId);
		if (oldStimme == null)
			throw new CandidateNotFoundException();

		// prüfen, ob die Id schon vorhanden ist:
		Stimme newStimme = getStimmeByCandId(newId);
		if (newStimme != null)
			throw new DoubleCandidateIdException();

		// prüfen, ob die neue Id überhaupt gültig ist.
		try {
			if (!isCandidateInVotingList(newId)) {
				throw new CandidateNotKnownException();
			}
		} catch (CandidateNotKnownException e) {
			throw new CandidateNotKnownException();
		} catch (Exception e) {
			throw new CandidateNotKnownException(
					"Kandidatendatei konnte nicht gelesen werden.");
		}
		// neue Id setzen:
		oldStimme.setId(newId);
		// Liste wieder sortieren
		Collections.sort(stimmen);
		reduceVotes();
	}

	/**
	 * Erstellt eine tiefe echte Kopie des Wahlzettels, um zum Beispiel beim
	 * Edit den Original Wahlzettel noch behalten zu können und nur auf der
	 * Kopie zu arbeiten.
	 * 
	 * @return Wahlzettel, tiefe Kopie des Originals
	 */
	public Wahlzettel copyWahlzettel() {
		Wahlzettel cp = new Wahlzettel(this.id, this.rc);
		cp.commited = this.commited;
		// cp.stimmen=(ArrayList<Stimme>) this.stimmen.clone();
		for (Stimme st : this.stimmen) {
			cp.stimmen.add(st.clone());
		}
		cp.valid = this.valid;
		return cp;
	}

	/**
	 * Prüft, ob der Kandidat mit der übergebenen Id überhaupt in der Liste der
	 * möglichen Kandidaten ist.
	 * 
	 * @param id
	 *            KandidatenId
	 * @return Boolean: Vorhanden?
	 * @throws Exception
	 */
	private boolean isCandidateInVotingList(int id) throws Exception {
		CandidateImportInterface c = null;
		c = BallotCardDesign.getInstance().getCandidate(id);
		if (c == null)
			throw new CandidateNotKnownException();

		return true;
	}

	public boolean isPartyVoted() {
		if (party==-1){
			return false;
		}else{
			return true;
		}
	}

}
