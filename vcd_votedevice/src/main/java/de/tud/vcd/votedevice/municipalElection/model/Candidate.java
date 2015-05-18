package de.tud.vcd.votedevice.municipalElection.model;

import java.util.Comparator;

import de.tud.vcd.common.CandidateImportInterface;
import de.tud.vcd.votedevice.municipalElection.model.exceptions.CandidateAutoDistributionNotAllowedException;
import de.tud.vcd.votedevice.onscreenkeyboard.Searcher;
import de.tud.vcd.votedevice.onscreenkeyboard.VCDSearchable;

/**
 * Repräsentiert einen Kandidaten im Modell
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class Candidate implements Comparable<Candidate>, VCDSearchable {

	private int id;
	

	private String name;
	private String prename;
	private String party;
	private int votes;//Stimmen die der Kandidate manuell bekommen hat
	private int countedVotes;//Stimmen die der Kandidat effektiv hat
	private int reducedVotes;//Stimmen die reduziert wurden, um auf die Maximalanzahl zu kommen.
	private boolean crossedOut;
	private boolean autoDistribution;
	
	/**
	 * Kandidat wird erzeugt mit id, namen und zugehörigerPartei
	 * @param id
	 * @param name
	 * @param prename
	 * @param party
	 */
	public Candidate(int id, String name, String prename, String party) {
		this.id = id;
		this.name = name;
		this.prename = prename;
		this.party = party;
		this.votes = 0;
		this.crossedOut=false;
		this.autoDistribution=true;
	}
	
	
	/**
	 * Gibt die STimmen an, die gedruckt werden sollen. Also die vergebenen minus die reduzierten, wenn dies über zu viele Stimmen
	 * notwendig wurde.
	 * @return
	 */
	public int getVotesToPrint(){
		return countedVotes-reducedVotes;
	}
	
	/**
	 * Liefert die Anzahl der reduzierten Stimmen zurück
	 * @return the reducedVotes
	 */
	public int getReducedVotes() {
		return reducedVotes;
	}


	/**
	 * Setzt die Anzahl an reduzierten Stimmen
	 * @param reducedVotes the reducedVotes to set
	 */
	public void setReducedVotes(int reducedVotes) {
		this.reducedVotes = reducedVotes;
	}


	/* (non-Javadoc)
	 * @see de.tud.vcd.votedevice.model.VCDSearchable#search(de.tud.vcd.votedevice.onscreenkeyboard.Searcher, java.lang.String)
	 */
	public void search(Searcher s, String str){
		s.searchFor(this, str);
	}
	
	
	/**
	 * Erzeugt einen Kandidaten über ein Import INterface
	 * @param importcandidate
	 */
	public Candidate(CandidateImportInterface importcandidate){
		this.id=importcandidate.getId();
		this.name=importcandidate.getName();
		this.prename=importcandidate.getPrename();
		this.party=importcandidate.getParty();
		this.votes=0;
		this.countedVotes=0;
		this.crossedOut=false;
		this.autoDistribution=true;
	}
	
	
	/**
	 * Id des Kandidaten
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Name des Kandidaten
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Vorname des Kandiaten
	 * @return the prename
	 */
	public String getPrename() {
		return prename;
	}

	/**
	 * Partei des Kandidaten
	 * @return the party
	 */
	public String getParty() {
		return party;
	}

	/**
	 * Liefert alle Stimmen des Kandidaten
	 * @return the votes
	 */
	public int getVotes() {
		return votes;
	}
	
	/**
	 *Liefert die zu zählenden Stimmen des Kandidaten
	 * @return the counted votes
	 */
	public int getCountedVotes() {
		return countedVotes;
	}

	/**
	 * Setzt die Anzahl an Stimmen
	 * @param votes: the votes to set
	 */
	public void setVotes(int votes) {
		this.votes = votes;
		this.countedVotes=votes;
		this.autoDistribution=true;
		this.crossedOut = false;
	}
	
	
	/**
	 * Setzt die Stimmen, die über die Listenverteilung erfolgen
	 * @param votes
	 * @throws CandidateAutoDistributionNotAllowedException
	 */
	public void setAutoDistributedVotes(int votes) throws CandidateAutoDistributionNotAllowedException{
		if (this.autoDistribution){
			this.countedVotes = this.votes+votes;
			this.crossedOut = false;
		}else{
			throw new CandidateAutoDistributionNotAllowedException();
		}
	}
	
	/**
	 * erhöht die über die Liste vergebenen Stimmen um eins
	 * @throws CandidateAutoDistributionNotAllowedException
	 */
	public void incAutoDistributedVotes() throws CandidateAutoDistributionNotAllowedException{
		if (this.autoDistribution){
			this.countedVotes++;
			this.crossedOut = false;
		}else{
			throw new CandidateAutoDistributionNotAllowedException();
		}
	}

	/**
	 * Ist er durchgestrichen?
	 * @return the crossedOut
	 */
	public boolean isCrossedOut() {
		return crossedOut;
	}


	/**
	 * Streicht den Kandidaten durch
	 * @param crossedOut the crossedOut to set
	 */
	public void setCrossedOut() {
		this.votes=0;
		this.countedVotes=0;
		this.autoDistribution=false;
		this.crossedOut = true;
	}


	/**
	 * @return the autoDistribution
	 */
	public boolean isAutoDistribution() {
		return autoDistribution;
	}


	/**
	 * Setzt die Automatissche Verteilung
	 * @param autoDistribution the autoDistribution to set
	 */
	public void setAutoDistribution() {
		//this.votes=0;
		this.autoDistribution = true;
		this.crossedOut=false;
	}

	
	/**
	 * Löscht die bisher vorhandene Stimmen
	 */
	public void reset(){
		this.autoDistribution=true;
		this.crossedOut=false;
		this.votes=0;
		this.countedVotes=0;
		this.reducedVotes=0;
	}


	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Candidate b) {
			Integer thisint= new Integer(this.getId());
			Integer bint= new Integer(b.getId());
		    return thisint.compareTo(bint);
	}
	
	/**
	 * @author Roman Jöris <roman.joeris@googlemail.com>
	 *
	 */
	public class ReduceComparator implements Comparator<Candidate>{
		 
	    public int compare(Candidate ca, Candidate cb) {
	    	Integer a_id= new Integer(ca.getId());
			Integer b_id= new Integer(cb.getId());
			
			Integer a_votes= new Integer(ca.getVotes());
			Integer b_votes= new Integer(cb.getVotes());
			
			//Nach Anzahl Stimmen vergleichen
			int state= (a_votes<b_votes ? -1 : (a_votes.intValue()==b_votes.intValue() ? 0 : 1 ));
			//Nur wenn Stimmen gleich sind noch nach ID reverse sortieren:
			if (state==0){
				state= (a_id>b_id ? -1 : (a_id==b_id ? 0 : 1));
			}
			
	    	
	    	return state;
	    }
	}
	
	/**
	 * Liefert einen Vergleicher
	 * @return
	 */
	public ReduceComparator getReduceComperator(){
		return new ReduceComparator();
	}

}
