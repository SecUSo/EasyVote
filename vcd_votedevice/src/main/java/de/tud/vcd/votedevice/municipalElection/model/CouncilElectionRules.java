package de.tud.vcd.votedevice.municipalElection.model;

import java.util.HashSet;
import java.util.Set;

import de.tud.vcd.votedevice.model.ElectionRulesInterface;
import de.tud.vcd.votedevice.municipalElection.model.BallotCard.Validity;



/**
 * Prüft die Einhaltung der Wahlregeln und gibt dem Wahlzettel seinen Zustand
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class CouncilElectionRules implements ElectionRulesInterface {
	
	
	
	private int votesPerCandidate;
	private int maxVotesPerBallotCard;
	private int maxVotedParties;
	
	
	/**
	 * Erzeugt den Prüfer
	 * @param votesPerCandidate
	 * @param maxVotesPerBallotCard
	 * @param maxVotedParties
	 * @param existsManualInvalid (Nicht mehr verwendet)
	 */
	public CouncilElectionRules(int votesPerCandidate, int maxVotesPerBallotCard, int maxVotedParties, boolean existsManualInvalid) {
		this.votesPerCandidate = votesPerCandidate;
		this.maxVotesPerBallotCard = maxVotesPerBallotCard;
		this.maxVotedParties = maxVotedParties;
		
	}
	
	
	
	
	
	/* (non-Javadoc)
	 * @see de.tud.vcd.votedevice.model.ElectionRulesInterface#getValidity(de.tud.vcd.votedevice.municipalElection.model.BallotCard)
	 */
	public Validity getValidity(BallotCard bc){
		
		
		//is the ballot manually invalid?
		if (bc.isVoteManualInvalid())return Validity.INVALID_MANUAL;
		
		int countVotesParties = 0;
		int countCandidateVotes = 0;
		int countManualVotes = 0;
		int countMaxVotesPerCandidate = 0;
		int crossedCandidateInVotedParty=0;
		Set<Party> partiesWithVotedCandidates = new HashSet<Party>(); 
		 
		

		// look for each party and count the votes and so on
		for (Party p : bc.getPartyList()) {
			if (p.isVoted()) {
				countVotesParties++;
			}
			// add the sum of votes for this party
			countCandidateVotes += p.countVotes();
			countManualVotes+=p.countManualVotes();

			// check, if there are more votes than allowed per candidate (search
			// for max of all)
			for (Candidate c : p.getCandidates()) {
				countMaxVotesPerCandidate=Math.max(c.getCountedVotes(),  countMaxVotesPerCandidate);
				if (c.getVotes()>0){
					partiesWithVotedCandidates.add(p); 
				}
				if (p.isVoted() && c.isCrossedOut()){
					crossedCandidateInVotedParty++;
				}
				
			}

		}
		//VALID, ,,, , VALID_REDUCE_PARTIES, VALID_REDUCE_CANDIDATES 
		//now check the results:
		if (countVotesParties > maxVotedParties && countManualVotes==0) {
			return Validity.INVALID_ONLY_PARTIES;
		}
		if (countCandidateVotes > maxVotesPerBallotCard && partiesWithVotedCandidates.size()>1) {
			return Validity.INVALID_TOOMUCHCANDIDATES;
		}
		if (countMaxVotesPerCandidate > votesPerCandidate) {
			return Validity.INVALID_TOO_MUCH_VOTES_EACH_CANDIDATE;
		}
		if (countCandidateVotes==0 && !bc.isVoteManualInvalid()){
			return Validity.INVALID_EMPTY;
		}
		if (countManualVotes>maxVotesPerBallotCard && partiesWithVotedCandidates.size()==1){
			return Validity.VALID_REDUCE_CANDIDATES;
		}
		if (countVotesParties>maxVotedParties && countManualVotes>0 && countManualVotes<=maxVotesPerBallotCard){
			return Validity.VALID_REDUCE_PARTIES;
		}
		
		if (countVotesParties==1 && countManualVotes==0 && crossedCandidateInVotedParty==0){
			return Validity.VALID_ONLY_PARTY;
		}
		if (countVotesParties==0 && countManualVotes>0 ){
			return Validity.VALID_NO_PARTY;
		}

		//If nothing has yet interrupted the validity-check the ballotcard is valid
		return Validity.VALID_PARTY_AND_CANDIDATE;
	}
}
