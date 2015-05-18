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
 * geh�ren. Also der Id mit der er sp�ter gespeichert wird, den Stimmen, dem
 * G�ltigkeitsflag, ob der Wahlzettel ung�ltig sein soll und dem Regelchecker
 * 
 * @author Roman J�ris <roman.joeris@googlemail.com>
 * 
 */
@Root(name = "Wahlzettel")
public class Wahlzettel {
	@Attribute
	private int id; // die eindeutige ID des Wahlzettels in der Urne, zum
					// Wiederfinden und �ndern
	@Attribute
	private boolean valid; // markiert direkt ung�ltig abgegebene Stimmen
	@ElementList
	private ArrayList<Stimme> stimmen;// die enthaltenen Stimmen f�r die
										// jeweilen Kandidaten
	private RegelChecker rc;// der Regelchecker, der f�r diesen Zettel
							// verantwortlich ist.
	@Attribute
	private int commited;

	@Attribute
	private int party;
	
	/**
	 * Der normale Konstruktor, um die Klasse zu erzeugen mit einer id und einem
	 * RegelChecker, der den Wahlzettel �berpr�ft.
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
	 * Konstruktor nur f�r das Laden der XML Dateien. Da m�ssen alle Angaben
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
			// Pr�fen, ob die Anzahl an Stimmen f�r diesen Kandidaten das
			// Maximum nicht �berschreitet
			gesamtStimmen += st.getAngekreuzteStimmen();
			parties.add(st.getId()/100);
		}
		//System.out.println("Anzahl Stimmen gesamt: "+gesamtStimmen);
		//System.out.println("Maximale Stimmen erlaubt: "+ MAXSTIMMEN);
		int zuReduzieren=gesamtStimmen-MAXSTIMMEN;
		
		//Pr�fen, ob der Status korrigierbar w�re:
		boolean zuruecksetzen=false;
		if (!(gesamtStimmen>MAXSTIMMEN && parties.size()<=1)){
			//Abbrechen, da es nichts zu korrigieren gibt
			//return false;
			//Da mehrere Parteien alles zur�cksetzen
			zuReduzieren=0;
			zuruecksetzen=true;
		}
		
		//System.out.println("Zureduzierende Stimmen: "+zuReduzieren);
		
		//Jetzt reduzieren, da es m�glich ist:
		int round=1;
		while (zuruecksetzen || zuReduzieren>0 && round<=MAXVOTES){//schwaches abbruchkriterium setzen, falls nicht gen�gend reduziert werden kann
			zuruecksetzen=false;
			int anzKandidaten=stimmen.size();
			for (int j=round;j<=MAXVOTES;j++){
				for (int i=anzKandidaten-1;i>=0;i--){
					if (zuReduzieren>0 && (stimmen.get(i).getAngekreuzteStimmen()==j)){
							zuReduzieren--;
							//System.out.println("REduzieren: " + c.getId());
							stimmen.get(i).setReduzierteStimmen(round);
						
					}else{
						//in der ersten Runde alle reduzierungen zur�cknehmen, wenn nicht oben schon reduziert wurde
						if (j==1 && round==1){
							stimmen.get(i).setReduzierteStimmen(0);
						}
					}
				}
			}
			round++;//Maximal so oft durchlaufen wie Kreuze pro Person m�glich sind.
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
	 * pr�ft, ob der Stimmzettel nach der �bergebenen Regel �berhaupt g�ltig
	 * ist. Zudem wird das Valid-Flag ausgewertet, welches bei einem ung�ltigen
	 * Stimmzettel gesetzt wird.
	 * 
	 * @return Boolean Gibt an, ob der Status auf G�ltig gesetzt ist und der
	 *         RegelChecker keine Einw�nde hat.
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
		// Pr�fen, ob Stimme schon in Liste vorhanden:
		if (getStimmeByCandId(stimme.getId()) != null)
			throw new DoubleCandidateIdException();
		// Pr�fen, ob Kandidatennummer �berhaupt im Verzeichnis ist:
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
	 * L�scht einen Kandidaten vom Stimmzettel anhand der Id des Kandidaten.
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
	 * Liefert die Stimme an der Position zur�ck.
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
	 * �ndert die Id eines Kandidaten, falls diese falsch erkannt wurde.
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

		// pr�fen, ob die Id schon vorhanden ist:
		Stimme newStimme = getStimmeByCandId(newId);
		if (newStimme != null)
			throw new DoubleCandidateIdException();

		// pr�fen, ob die neue Id �berhaupt g�ltig ist.
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
	 * Edit den Original Wahlzettel noch behalten zu k�nnen und nur auf der
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
	 * Pr�ft, ob der Kandidat mit der �bergebenen Id �berhaupt in der Liste der
	 * m�glichen Kandidaten ist.
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
