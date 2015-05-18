package de.tud.vcd.votedevice.municipalElection.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;

import de.tud.vcd.common.BallotCardDesign;
import de.tud.vcd.common.BallotCardDesign.DesignKeys;
import de.tud.vcd.common.CandidateImportInterface;
import de.tud.vcd.votedevice.model.ElectionRulesInterface;
import de.tud.vcd.votedevice.model.IBallotCardImageCreator;
import de.tud.vcd.votedevice.multielection.VCDModel;
import de.tud.vcd.votedevice.municipalElection.model.exceptions.CandidateNotFoundException;
import de.tud.vcd.votedevice.municipalElection.model.exceptions.CannotAddCandidateFalsePartyException;
import de.tud.vcd.votedevice.municipalElection.model.exceptions.PartyNotFoundException;
import de.tud.vcd.votedevice.onscreenkeyboard.Searcher;
import de.tud.vcd.votedevice.onscreenkeyboard.VCDSearchable;

/**
 * zentrales Modell für die Kommunalwahl. Ist einer der Hauptklassen in diesem Projekt. Enthält die komplette Wahllogik
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class BallotCard extends Observable implements VCDModel, VCDSearchable {

	/**
	 * Zeigt an, ob die Stimmabgabe grade begonnen wurde oder  nicht.
	 *
	 */
	public enum State {
		  INIT, VOTING 
	}
	
	/**
	 * Zustände der Gültigkeit wie ein Wahlzettel sein kann
	 *
	 */
	public enum Validity {
		  VALID_ONLY_PARTY,VALID_NO_PARTY,VALID_PARTY_AND_CANDIDATE, INVALID_MANUAL,INVALID_TOOMUCHCANDIDATES,INVALID_ONLY_PARTIES, INVALID_EMPTY, VALID_REDUCE_PARTIES, VALID_REDUCE_CANDIDATES ,INVALID_TOO_MUCH_VOTES_EACH_CANDIDATE
	}
	

	//Juri
		public String errortype ="";
		public String getError(){
			return errortype;
		}
		public void setError(String e){
			errortype = e;
		}
		
		//prüfe ob gerade/ungerade damit man die Manipulation hinzufügt.
		public int count = 1;
	
	private State state;
	private ArrayList<Party> partylist;
	private boolean voteManualInvalid;
	private String electionId;
	private String electionName;
	
	private ElectionRulesInterface rules;
	private int maxVotes;
	
	private String selectedParty="";
	private int selectedCandidate=-1;
	
	/**
	 * Erzeugt ein Modell anhand der übergebenen Konfigurationsdatei
	 * 
	 * @param bcd
	 * @throws Exception
	 */
	public BallotCard(BallotCardDesign bcd) throws Exception{
		maxVotes=bcd.getDesignValue(DesignKeys.MAXSTIMMEN);
		int maxVotesPerCandidat=bcd.getDesignValue(DesignKeys.VOTESPROKANDIDAT);
		int maxVotedParties=bcd.getDesignValue(DesignKeys.ANZAHLPARTEIENGLEICHZEITIG);
		boolean allowManualInvalid;
		if (bcd.getDesignValue(DesignKeys.MANUELLESUNGUELTIG)==1){
			allowManualInvalid=true;
		}else{
			allowManualInvalid=false;
		}
		
		//initialize variables
		state=State.INIT;
		partylist = new ArrayList<Party>();
		voteManualInvalid=false;
		
		//load rule check
		rules= new CouncilElectionRules(maxVotesPerCandidat, maxVotes, maxVotedParties, allowManualInvalid);
		
		//read general vote casting data:
		electionId=bcd.getElection_id();
		electionName=bcd.getElection_name();
		
		//create all parties and fill them with the candidates:
		ArrayList<CandidateImportInterface> candidates= bcd.getCandidates();
		int nextPartyId=1;
		for (CandidateImportInterface c: candidates ){
			if (!this.existsParty(c.getParty())){
				partylist.add(new Party(nextPartyId,c.getParty(),maxVotesPerCandidat, maxVotes));
				nextPartyId++;
			}
			try {
				this.getParty(c.getParty()).addCandidate(c);
			} catch (CannotAddCandidateFalsePartyException e) {
				//do nothing, this does not happen
			} catch (PartyNotFoundException e) {
				//do nothing, this does not happen
			}
		}
		//Oberfläche informieren
		this.updateObserver();
	}
	
	
	
	/**
	 * Prüft die Gültigkeit des Modells und liefert den Status zurück
	 * @return Validity
	 */
	public Validity getValidity(){
		return rules.getValidity(this);
		
	}
	
	/* (non-Javadoc)
	 * @see de.tud.vcd.votedevice.multielection.VCDModel#resetBallotCard()
	 */
	public void resetBallotCard(){
		
		this.setManualVoteInvalid(false);
		for (Party p: partylist){
			p.reset();
		}
		this.selectedCandidate=-1;
		this.selectedParty="";
		state=State.INIT;
		updateObserver();
	}
	
	/**
	 * Prüft, ob der Stimmzettel gültig ist. Oberflächlichere Angabe als getValidity
	 * @return
	 */
	public boolean isValid(){
		EnumSet<Validity> set = EnumSet.of(Validity.VALID_NO_PARTY,Validity.VALID_ONLY_PARTY, Validity.VALID_PARTY_AND_CANDIDATE, Validity.VALID_REDUCE_CANDIDATES, Validity.VALID_REDUCE_PARTIES);
	
		if (set.contains(rules.getValidity(this))){
	//	if (rules.getFails(this).isEmpty()){
			return true;
		}else{
			return false;
		}
	}
	
	
	/**
	 * Liefert die Anzahl an angewählten Parteien
	 * @return int
	 */
	public int countVotedParties(){
		int count=0;
		for(Party p:partylist){
			if (p.isVoted()){
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Liefert die Anzahl an gewählter Kandidaten
	 * @return int
	 */
	public int countCandidatesVotes(){
		int count=0;
		for(Party p:partylist){
			count+=p.countVotes();
		}
		return count;
	}
	
	/**
	 * Liefert die Anzahl an Kandidatenstimmen die über die Listenstimme verteilt wurden.
	 * @return int
	 */
	public int countCandidatesDistributedVotes(){
		int count=0;
		for(Party p:partylist){
			count+=p.countDistributedVotes();
		}
		return count;
	}

	/**
	 * Liefert die Anzahl an Kandidatenstimmen die manuell gesetzt wurden.
	 * @return int
	 */
	public int countCandidatesManualVotes(){
		int count=0;
		for(Party p:partylist){
			count+=p.countManualVotes();
		}
		return count;
	}
	
	/**
	 * Liefert die Party mit der übergebenen Id zurück.
	 * @param id
	 * @return Party
	 * @throws PartyNotFoundException
	 */
	public Party getParty(int id) throws PartyNotFoundException{
		//sortCandidates();
				
		int pos=java.util.Collections.binarySearch(partylist, new Party(id,"",0,0));
		if (pos<0){
			throw new PartyNotFoundException();
		}
		hasChanged();
		notifyObservers(partylist.get(pos));
		return partylist.get(pos);
	}
	
	/**
	 * Liefert die Party anhand des Parteikürzels zurück
	 * @param name
	 * @return Party
	 * @throws PartyNotFoundException
	 */
	public Party getParty(String name) throws PartyNotFoundException{
		//sortCandidates();
		for (Party p:partylist){
			if (p.getName().equals(name)){
				return p;
			}
		}
		throw new PartyNotFoundException();
	}
	
	/**
	 * Liefert alle Parteien zurück, die eingelesen wurden und momentan vorhanden sind.
	 * @return
	 */
	public ArrayList<Party> getPartyList(){
		return partylist;
	}
	
	/**
	 * Prüft, ob der übergebene Parteiname auch in der Liste existiert
	 * @param name
	 * @return
	 */
	private boolean existsParty(String name){
		boolean exists=false;
		for (Party p: partylist){
			if (p.getName().equals(name))exists=true;
		}
		return exists;
	}
	
	
	/* (non-Javadoc)
	 * @see java.util.Observable#addObserver(java.util.Observer)
	 */
	public void addObserver(Observer o) {
		// Wird aus dem Parentobjekt übernommen, aber so ist es übersichtlicher.
		super.addObserver(o);
	}
	
	
	/**
	 * weist die Observer an sich zu aktualisieren.
	 */
	public void updateObserver() {
		setChanged();
		notifyObservers(this);
		//setChanged();
		//notifyObservers(this.ergebnis);
	}

	/**
	 * Gibt an, ob der Stimmzettel manuell ungültig gesetzt wurde
	 * @return the voteInvalid
	 */
	public boolean isVoteManualInvalid() {
		return voteManualInvalid;
	}

	/**
	 * Liefert die Id der Wahl zurück
	 * @return the electionId
	 */
	public String getElectionId() {
		return electionId;
	}

	/**
	 * Liefert den Wahlnamen zurück
	 * @return the electionName
	 */
	public String getElectionName() {
		return electionName;
	}

	/**
	 * Setzt den Stimmzettel manuell auf ungültig.
	 * @param voteInvalid the voteInvalid to set
	 */
	public void setManualVoteInvalid(boolean voteManualInvalid) {
		state=State.VOTING;
		this.voteManualInvalid = voteManualInvalid;
		doAutoDistributionToAllParties();
		updateObserver();
	}

	/**
	 * @return the state
	 */
	public State getState() {
		return state;
	}


	/**
	 * @param state the state to set
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * Gibt dem Kandidaten in der Partei die entsprechende Anzahl an Stimmen. Partei notwendig, da Kandidaten innerhalb der Parteien angelegt sind.
	 * @param party
	 * @param candidateId
	 * @param votes
	 * @throws CandidateNotFoundException
	 * @throws PartyNotFoundException
	 */
	public void setCandidateVote(String party, int candidateId, int votes) throws CandidateNotFoundException, PartyNotFoundException{
		state=State.VOTING;
		this.getParty(party).getCandidate(candidateId).setVotes(votes);
		doAutoDistributionToAllParties();
		updateObserver();
	}
	
	/**
	 * Streicht den übergebenen Kandidaten
	 * @param party
	 * @param candidateId
	 * @throws CandidateNotFoundException
	 * @throws PartyNotFoundException
	 */
	public void setCandidateCrossed(String party, int candidateId) throws CandidateNotFoundException, PartyNotFoundException{
		state=State.VOTING;
		this.getParty(party).getCandidate(candidateId).setCrossedOut();
		doAutoDistributionToAllParties();
		updateObserver();
	}
	
	/**
	 * Läßt für einen Kandidaten zu, dass an ihn Stimmen verteilt werden können. Erfordert dass die 
	 * Stimmverteilung erneuert wird. Wird Funktionsintern aufgerufen
	 * @param party
	 * @param candidateId
	 * @throws CandidateNotFoundException
	 * @throws PartyNotFoundException
	 */
//	public void setAutoDistribution(String party, int candidateId) throws CandidateNotFoundException, PartyNotFoundException{
//		state=State.VOTING;
//		this.getParty(party).getCandidate(candidateId).setAutoDistribution();
//		doAutoDistributionToAllParties();
//		updateObserver();
//	}
	
	/**
	 * Wählt oder entwählt die übergebene Partei
	 * @param party
	 * @param voted
	 * @throws PartyNotFoundException
	 */
	public void setPartyVoted(String party, boolean voted) throws PartyNotFoundException{
		state=State.VOTING;
		this.getParty(party).setVoted(voted);
		doAutoDistributionToAllParties();
		updateObserver();
	}
	
	/**
	 * Gibt einem vorher definierten Kandidaten die übergeben Anzahl an Stimmen. Dies macht das Handling mit dem Popupmenü leichter
	 * Es ist damit möglich, dass der Kandidate bereits schon vorselektiert wird und nur noch die STimmen bekannt gegeben werden müssen.
	 * @param votes
	 * @throws CandidateNotFoundException
	 * @throws PartyNotFoundException
	 */
	public void setSelectedCandidateVote(int votes) throws CandidateNotFoundException, PartyNotFoundException{
		setCandidateVote(selectedParty, selectedCandidate, votes);
	}
	
	/**
	 * Streicht den vorselektierten Kandidaten
	 * @throws CandidateNotFoundException
	 * @throws PartyNotFoundException
	 */
	public void setSelectedCandidateCrossed() throws CandidateNotFoundException, PartyNotFoundException{
		setCandidateCrossed(selectedParty, selectedCandidate);
	}
	
	/**
	 * Erlaubt die Stimmverteilung für den vorselektierten Kandidaten.
	 * @throws CandidateNotFoundException
	 * @throws PartyNotFoundException
	 */
//	public void setSelectedCandidateAutoDistribution() throws CandidateNotFoundException, PartyNotFoundException{
//		setAutoDistribution(selectedParty,selectedCandidate);
//	}
	
	/**
	 * Wählt die vorselektierte Partei
	 * @param voted
	 * @throws PartyNotFoundException
	 */
	public void setSelectedPartyVoted(boolean voted) throws PartyNotFoundException{
		setPartyVoted(selectedParty, voted);
	}
	
	/**
	 * Verteilt die verfügbaren Stimmen auf die gewählte Partei.
	 */
	private void doAutoDistributionToAllParties(){
		for(Party p: partylist){
			if(p.isVoted()){
				p.distributeVotes(0);
			}
		}
		int availableVotes=Math.max(maxVotes-this.countCandidatesManualVotes(), 0);
		if(voteManualInvalid || countVotedParties()>1){
			availableVotes=0;
		}
		for(Party p: partylist){
			if(p.isVoted()){
				p.distributeVotes(availableVotes);
			}
		}
	}
	
	/**
	 * Erzeugt ein Bild des Wahlzettels mit der übergebenen Auflösung. Dieses Bild kann als Stimmzettel gedruckt werden.
	 * @param resolutionX
	 * @param resolutionY
	 * @return
	 * @throws Exception
	 */
	public ImageIcon createBallotCardReview(int resolutionX, int resolutionY) throws Exception{
		//Stimme reduzieren falls notwendig:
		
		if (getValidity()==Validity.VALID_REDUCE_CANDIDATES){
			for (Party p : partylist) {
				if (p.countManualVotes()>maxVotes) {
					p.reduceVotes(); //Juri hier werden die Stimmen korrekt abgezogen.
				}
			}
		}
		//Review erstellen
	    state=State.VOTING;
		IBallotCardImageCreator bcic = new BallotCardImageCreatorNewDesign(resolutionX, resolutionY, this);
		return bcic.createImage(Color.WHITE);
	}

	/**
	 * Selektiert eine Partei, um mit dieser weiterarbeiten zu können
	 * @param selectedParty the selectedParty to set
	 */
	public void setSelectedParty(String selectedParty) {
		this.selectedParty = selectedParty;
		updateObserver();
	}

	/**
	 * Selektiert einen Kandidaten, um mit diesem weiterarbeiten zu können
	 * @param selectedCandidate the selectedCandidate to set
	 */
	public void setSelectedCandidate(int selectedCandidate) {
		this.selectedCandidate = selectedCandidate;
		//System.out.println("Candidate is: "+this.selectedCandidate);
		updateObserver();
	}
	
	/**
	 * Liefert die voraussichtliche Anzahl an Stimmen die bei einer Verteilung für diesen Kandidaten vorhanden sein würden.
	 * @return int
	 */
	public int getSelectedCandidateAutoDistributionForecast(){
		
		int voteForecast=0;
		try {
			int availableVotes=Math.max(maxVotes-this.countCandidatesManualVotes(), 0);
			if(voteManualInvalid || countVotedParties()>1 || !getParty(getSelectedParty()).isVoted() ){
				availableVotes=0;
			}
			voteForecast=getParty(getSelectedParty()).simulateDistributionForCandidate(getParty(getSelectedParty()).getCandidate(getSelectedCandidate()), availableVotes);
		} catch (PartyNotFoundException e) {
			//e.printStackTrace();
		} catch (CandidateNotFoundException e) {
			//e.printStackTrace();
		}
		
		
		return voteForecast;
	}

	/**
	 * Liefert die selektierte Partei
	 * @return the selectedParty
	 */
	public String getSelectedParty() {
		return selectedParty;
	}

	/**
	 * Liefert den selektierten Kandidaten
	 * @return the selectedCandidate
	 */
	public int getSelectedCandidate() {
		return selectedCandidate;
	}

	/* (non-Javadoc)
	 * @see de.tud.vcd.votedevice.onscreenkeyboard.VCDSearchable#search(de.tud.vcd.votedevice.onscreenkeyboard.Searcher, java.lang.String)
	 */
	public void search(Searcher s, String str) {
		for(Party p: this.getPartyList()){
			p.search(s, str);
			s.searchFor(p, str);
		}
		
	}
	
}
