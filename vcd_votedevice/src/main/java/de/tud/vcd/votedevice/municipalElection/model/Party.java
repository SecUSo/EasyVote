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
package de.tud.vcd.votedevice.municipalElection.model;

import java.util.ArrayList;
import java.util.Collections;

import de.tud.vcd.common.CandidateImportInterface;
import de.tud.vcd.votedevice.municipalElection.model.exceptions.CandidateAutoDistributionNotAllowedException;
import de.tud.vcd.votedevice.municipalElection.model.exceptions.CandidateNotFoundException;
import de.tud.vcd.votedevice.municipalElection.model.exceptions.CannotAddCandidateFalsePartyException;
import de.tud.vcd.votedevice.onscreenkeyboard.Searcher;
import de.tud.vcd.votedevice.onscreenkeyboard.VCDSearchable;

/**
 * Ist eine Partei auf dem Wahlzettel. In ihr sind die jeweiligen Kandidaten enthalten.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class Party implements Comparable<Party> , VCDSearchable {

	private int id;//interne id
	private String name;//angezeicgter Parteiname
	private boolean voted;//ob gewählt oder nicht
	private ArrayList<Candidate> candidates;
	private int maxVotes;
	private int maxTotalVotes;
	
	private boolean sorted=false;
	
	/**
	 * Erzeugt eine Partei mit Id, name und technischen Daten.
	 * @param id
	 * @param name
	 * @param maxVotes
	 * @param maxTotalVotes
	 */
	public Party(int id, String name, int maxVotes, int maxTotalVotes) {
		this.id = id;//Benötigt für die natürliche Ordnung der Parteien, da diese nicht alphabetisch sind.
		this.name = name;
		this.voted = false;
		candidates=new ArrayList<Candidate>();
		this.maxVotes=maxVotes;
		this.maxTotalVotes=maxTotalVotes;
		sorted=false;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the voted
	 */
	public boolean isVoted() {
		return voted;
	}

	/**
	 * @param voted the voted to set
	 */
	public void setVoted(boolean voted) {
		//Automatische Verteilung rausnehmen, wenn Partei nicht mehr gewählt ist:
		if (!voted){
			this.distributeVotes(0);
		}
		this.voted = voted;
		}

	/**
	 * Hilfsfunktion für die Performance. Kandidaten werden unsortiert in die Liste eingepflegt. 
	 * Aber erst sortiert, wenn darauf zugegriefen werden soll und noch nicht sortiert war.
	 */
	private void sortCandidates(){
		if (this.sorted==false)
			Collections.sort(candidates);
		
		this.sorted=true;
	}
	
	/**
	 * Liefert einen Kandidaten anhand seiner Id zurück. Ist er in der Partei nicht vorhanden, wird ein Fehler geworfen
	 * @param id
	 * @return
	 * @throws CandidateNotFoundException
	 */
	public Candidate getCandidate(int id) throws CandidateNotFoundException{
		sortCandidates();
				
		int pos=java.util.Collections.binarySearch(candidates, new Candidate(id,"","",""));
		if (pos<0){
			throw new CandidateNotFoundException();
		}
		return candidates.get(pos);
	}
	
	/**
	 * Liefert alle Kandidaten als ArrayList zurück
	 * @return
	 */
	public ArrayList<Candidate> getCandidates(){
		sortCandidates();
		return candidates;
	}
	
	/**
	 * Fügt einen Kandiaten in die Liste hinzu
	 * @param cii
	 * @throws CannotAddCandidateFalsePartyException
	 */
	public void addCandidate(CandidateImportInterface cii) throws CannotAddCandidateFalsePartyException{
		if (!cii.getParty().equals(this.name)){
			throw new CannotAddCandidateFalsePartyException();
		}
		Candidate c = new Candidate(cii);
		candidates.add(c);
		this.sorted=false;
		
	}
	
	/**
	 * Zählt die Stimmen innerhalb aller Kandidaten 
	 * @return
	 */
	public int countVotes(){
		int result=0;
		for (Candidate c: candidates){
			result+=c.getCountedVotes();
		}
		return result;
	}
	
	/**
	 * Zählt die Stimmen, die verteilt wurden über die Listenstimme
	 * @return
	 */
	public int countDistributedVotes(){
		//Wenn Partei nicht gewählt wurde, dann erst gar nicht nachgucken
		int result=0;
		for (Candidate c: candidates){
				result+=c.getCountedVotes()-c.getVotes();
		}
		return result;
		
	}
	
	/**
	 * Zählt die manuell vergebenen Stimmen in der Partei
	 * @return
	 */
	public int countManualVotes(){
		int result=0;
		for (Candidate c: candidates){
			//if (!c.isAutoDistribution()){
				result+=c.getVotes();
			//}
		}
		return result;
	}
	
	/**
	 * enthält die Anzahl der Stimmen die gedruckt werden, hier wird auch abgebildet, wenn Stimmen reduziert wurden. 
	 * Die Reduzierung wird jeweils neu berechnet, wenn der Druck oder die Anzeige angestoßen wird.
	 * @return
	 */
	public int countVotesToPrint(){
		//enthält die Anzahl der Stimmen die gedruckt werden, hier wird auch abgebildet, wenn Stimmen reduziert wurden. 
		//Die Reduzierung wird jeweils neu berechnet, wenn der Druck oder die Anzeige angestoßen wird.		
		int result=0;
		for (Candidate c: candidates){
				result+=c.getVotesToPrint();
		}
		return result;
	}
	
	
	public void updateReducedVotes(){
		for(Candidate c: candidates){
			c.setReducedVotes(0);
		}
	}
	
	/**
	 * Reduziert die Stimmen, nahc der Heilungsregel. 
	 */
	public void reduceVotes(){
		
		
		int reduceVote=countManualVotes()-maxTotalVotes;
		
		//In die richtige Reihenfolge zum Reduzieren bringen
		Candidate cc= new Candidate(0, "", "", "---");
		Collections.sort(candidates,cc.getReduceComperator());
		int round=1;
		while (reduceVote>0 && round<=maxVotes){//schwaches abbruchkriterium setzen, falls nicht genügend reduziert werden kann
			
			for (Candidate c : candidates) {
				if (reduceVote>0 && (c.getVotes()>=round)){
					reduceVote--;
					//System.out.println("REduzieren: " + c.getId());
					c.setReducedVotes(round);
				}else{
					if (round==1){
						c.setReducedVotes(0);
					}
				}
			}
			round++;
		}
		//von hinten nach vorne reduzieren
		
		//Zurück sortieren:
		sorted=false;
		sortCandidates();
	}
	
	
	/**
	 * Simulierte Stimmvergabe für einen Kandidtaen. Wieviel Stimmen er bei Listenkreuz bekäme.
	 * @param cand
	 * @param availableVotes
	 * @return
	 */
	public int simulateDistributionForCandidate(Candidate cand, int availableVotes){
		
		
		ArrayList<Candidate> cl=this.getCandidates();
		int candPos=cl.indexOf(cand);
		int countDistCandidates=0;
		for(Candidate c:cl){
			if (c.getId()!=cand.getId() && c.isAutoDistribution()){
				countDistCandidates++;
			}
		}
		//increase countDistCandidates by 1, because it is itself autodistributed, too
		countDistCandidates++;
		int voteForecast=0;
		while(availableVotes>countDistCandidates){
			voteForecast++;
			availableVotes-=countDistCandidates;
		}
		if (candPos<availableVotes){
			voteForecast++;
		}
		voteForecast=Math.min(voteForecast, maxVotes);
		return voteForecast;
	}
	
	/**
	 * Verteilt die Stimmen innerhalb der Parteo
	 * @param toDistribute
	 */
	public void distributeVotes(int toDistribute){
		
		
		//number of candidates:
		int countCandidates=candidates.size();
		//boolean nothingToDo=false;
		
		//reset all distributions 
		for (Candidate c: candidates){
			if (c.isAutoDistribution()){
				try {
					c.setAutoDistributedVotes(0);
				} catch (CandidateAutoDistributionNotAllowedException e) {
					//nothing to do, should not happened, because is Auto Distributed
				}
			}
		}
		//only distribute, if party is voted
		if (this.voted==false)return ;
		
		//Nun verteilen
		for (int i=1;i<=maxVotes;i++){//maximal so oft wie stimmen haben kann
			if (toDistribute<=0)break;
			for(int ci=0;ci<countCandidates;ci++){
				if (toDistribute<=0)break;
				Candidate c= candidates.get(ci);
				if(c.isAutoDistribution() && c.getCountedVotes()<maxVotes){
					try {
						c.incAutoDistributedVotes();
						toDistribute--;
					} catch (CandidateAutoDistributionNotAllowedException e) {
						//nothing to do, should not happened, because is Auto Distributed
					}
				}
				
			}
			
			
			
		}
	}

	
	/**
	 *Löscht den Inhalt der Stimmvergabe in der Partei  
	 */
	public void reset(){
		this.setVoted(false);
		for(Candidate c: candidates){
			c.reset();
		}
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Party o) {
		Integer thisint= new Integer(this.getId());
		Integer oint= new Integer(o.getId());
	    return thisint.compareTo(oint);
	}

	
	/* (non-Javadoc)
	 * @see de.tud.vcd.votedevice.onscreenkeyboard.VCDSearchable#search(de.tud.vcd.votedevice.onscreenkeyboard.Searcher, java.lang.String)
	 */
	public void search(Searcher s, String str) {
		//In jedem Kandidaten suchen
		//s.searchFor(this, str);
		for (Candidate c: this.getCandidates()){
			s.searchFor(c, str);
		}

	}
	
	//Juri
		public void removeCandidate(int id){
			if(!candidates.isEmpty()){
				candidates.remove(id);
			}
		}
}
