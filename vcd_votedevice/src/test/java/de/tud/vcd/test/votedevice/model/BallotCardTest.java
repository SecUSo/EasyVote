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
package de.tud.vcd.test.votedevice.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.tud.vcd.common.BallotCardDesign;
import de.tud.vcd.votedevice.model.ElectionRulesInterface;
import de.tud.vcd.votedevice.municipalElection.model.BallotCard;
import de.tud.vcd.votedevice.municipalElection.model.Party;
import de.tud.vcd.votedevice.municipalElection.model.exceptions.CandidateNotFoundException;
import de.tud.vcd.votedevice.municipalElection.model.exceptions.PartyNotFoundException;

public class BallotCardTest {
	BallotCardDesign bcd;
	BallotCard bc;
	@Before
	public void setUp()    {
		
		try {
			InputStream filename=getClass().getClassLoader().getResource("BallotCardTest_wahlzettel.xml").openStream();
			bcd=BallotCardDesign.getInstance(filename);
			bc= new BallotCard(bcd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}

	@Test
	public void testBallotCard() throws CandidateNotFoundException, PartyNotFoundException  {
		BallotCard ballotcard;
		try {
			ballotcard = new BallotCard(bcd);
			assertEquals("1.2.3", ballotcard.getElectionId());
			assertEquals(8,ballotcard.getPartyList().size());
			assertEquals("Wilhelm Tamas",ballotcard.getParty("JKL").getCandidate(515).getName());
			assertEquals(null,ballotcard.getParty("JKL").getCandidate(515).getPrename());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Test
	public void testResetBallotCard() throws CandidateNotFoundException, PartyNotFoundException {
		bc.setCandidateVote("JKL", 501, 3);
		
		bc.setPartyVoted("STU", true);
		bc.setManualVoteInvalid(true);
		bc.resetBallotCard();
		
		assertEquals(false, bc.isVoteManualInvalid());
		assertEquals(0,bc.countCandidatesVotes());
		assertEquals(0,bc.countVotedParties());
	}

	@Test
	public void testIsValid() throws PartyNotFoundException, CandidateNotFoundException {
		assertEquals(true, bc.isValid());
		bc.setManualVoteInvalid(true);
		assertEquals(false, bc.isValid());
		bc.setManualVoteInvalid(false);
		assertEquals(true, bc.isValid());
		
		//vote several parties
		bc.setPartyVoted("CBA", true);
		assertEquals(true, bc.isValid());
		bc.setPartyVoted("GHI", true);
		assertEquals(false, bc.isValid());
		bc.setPartyVoted("CBA", false);
		assertEquals(true, bc.isValid());
		
		//vote several candidates
		for(int i=501;i<514;i++){
			bc.setCandidateVote("JKL", i, 3);
		}
		assertEquals(true, bc.isValid());
		for(int i=514;i<518;i++){
			bc.setCandidateVote("JKL", i, 3);
		}
		assertEquals(false, bc.isValid());
		bc.resetBallotCard();
		assertEquals(true, bc.isValid());
	}
// TEST ANPASSEN UND WIDER LAUFEN LASSEN
//	@Test
//	public void testGetFails() throws CandidateNotFoundException, PartyNotFoundException {
//		ArrayList<ElectionRulesInterface.RejectReasons> f=new ArrayList<ElectionRulesInterface.RejectReasons>();
//		
//		bc.setManualVoteInvalid(true);
//		f.add(ElectionRulesInterface.RejectReasons.MANUALLY_INVALID);
//		assertEquals(f, bc.getFails());
//		
//		f.add(ElectionRulesInterface.RejectReasons.TO_MANY_PARTIES);
//		
//		// vote several parties
//		bc.setPartyVoted("CBA", true);
//		bc.setPartyVoted("GHI", true);
//		assertEquals(f, bc.getFails());
//		
//		// vote several candidates
//		for (int i = 501; i < 518; i++) {
//			bc.setCandidateVote("JKL", i, 3);
//		}
//		f.add(ElectionRulesInterface.RejectReasons.TO_MANY_CANDIDATES);
//		assertEquals(f, bc.getFails());
//		
//		
//		
//		bc.resetBallotCard();
//		f.clear();
//		assertEquals(f, bc.getFails());
//	}

	@Test
	public void testCountVotedParties() throws PartyNotFoundException {
		assertEquals(0,bc.countVotedParties());
		bc.setPartyVoted("CBA", true);
		bc.setPartyVoted("GHI", true);
		assertEquals(2,bc.countVotedParties());
		bc.setPartyVoted("ABC", true);
		assertEquals(3,bc.countVotedParties());
		bc.setPartyVoted("STU", true);
		assertEquals(4,bc.countVotedParties());
		
		bc.resetBallotCard();
		assertEquals(0,bc.countVotedParties());
	}

	@Test
	public void testCountCandidatesVotes() throws CandidateNotFoundException, PartyNotFoundException {
		// vote several candidates
		bc.setCandidateVote("JKL", 501, 3);
		assertEquals(3,bc.countCandidatesVotes());
		
		for (int i = 603; i < 606; i++) {
			bc.setCandidateVote("MNO", i, 2);
		}
		assertEquals(9,bc.countCandidatesVotes());
		
		bc.setPartyVoted("STU", true);
		assertEquals(36,bc.countCandidatesVotes());
		bc.setPartyVoted("STU", false);
		assertEquals(9,bc.countCandidatesVotes());
		bc.setPartyVoted("STU", true);
		bc.setCandidateCrossed("STU", 802);
		assertEquals(33,bc.countCandidatesVotes());
		
		bc.setPartyVoted("STU", false);
		assertEquals(9,bc.countCandidatesVotes());
		
		
	}

	@Test
	public void testCountCandidatesDistributedVotes() throws CandidateNotFoundException, PartyNotFoundException {
		bc.setCandidateVote("JKL", 501, 3);
		assertEquals(0,bc.countCandidatesDistributedVotes());
		
		for (int i = 603; i < 606; i++) {
			bc.setCandidateVote("MNO", i, 2);
		}
		assertEquals(0,bc.countCandidatesDistributedVotes());
		
		bc.setPartyVoted("STU", true);
		assertEquals(27,bc.countCandidatesDistributedVotes());
		bc.setPartyVoted("STU", false);
		assertEquals(0,bc.countCandidatesDistributedVotes());
		bc.setPartyVoted("STU", true);
		bc.setCandidateCrossed("STU", 802);
		assertEquals(24,bc.countCandidatesDistributedVotes());
		
		bc.setPartyVoted("STU", false);
		assertEquals(0,bc.countCandidatesDistributedVotes());
		
	}

	@Test
	public void testCountCandidatesManualVotes() throws CandidateNotFoundException, PartyNotFoundException {
		bc.setCandidateVote("JKL", 501, 3);
		assertEquals(3,bc.countCandidatesManualVotes());
		
		for (int i = 603; i < 606; i++) {
			bc.setCandidateVote("MNO", i, 2);
		}
		assertEquals(9,bc.countCandidatesManualVotes());
		
		bc.setPartyVoted("STU", true);
		assertEquals(9,bc.countCandidatesManualVotes());
		bc.setPartyVoted("STU", false);
		assertEquals(9,bc.countCandidatesManualVotes());
		bc.setPartyVoted("STU", true);
		bc.setCandidateCrossed("STU", 802);
		assertEquals(9,bc.countCandidatesManualVotes());
		
		bc.setPartyVoted("STU", false);
		assertEquals(9,bc.countCandidatesManualVotes());
		bc.setCandidateVote("JKL", 501, 0);
		assertEquals(6,bc.countCandidatesManualVotes());
	}

	@Test
	public void testGetPartyInt() throws PartyNotFoundException {
		assertEquals("MNO",bc.getParty(0).getName());
		assertEquals("ABC",bc.getParty(1).getName());
	}

	@Test
	public void testGetPartyString() throws PartyNotFoundException {
		assertEquals("GHI",bc.getParty("GHI").getName());
		assertEquals("CBA",bc.getParty("CBA").getName());
	}

	@Test
	public void testGetPartyList() {
		ArrayList<Party> pl= bc.getPartyList();
		assertEquals("GHI",pl.get(4).getName());
		assertEquals(8, pl.size());
	}

	
	@Test
	public void testIsVoteManualInvalid_SetManualVoteInvalid() {
		assertEquals(false, bc.isVoteManualInvalid());
		bc.setManualVoteInvalid(true);
		assertEquals(true, bc.isVoteManualInvalid());
		bc.setManualVoteInvalid(false);
		assertEquals(false, bc.isVoteManualInvalid());
		
	}

	@Test
	public void testGetElectionId() {
		assertEquals("1.2.3", bc.getElectionId());
	}

	@Test
	public void testGetElectionName() {
		assertEquals("Meine Testwahl", bc.getElectionName());
	}

	@Ignore
	public void testGetState() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetAutoDistribution() throws CandidateNotFoundException, PartyNotFoundException {
		bc.setCandidateVote("JKL", 501, 3);
		assertEquals(3,bc.countCandidatesVotes());
		
		for (int i = 603; i < 606; i++) {
			bc.setCandidateVote("MNO", i, 2);
		}
		assertEquals(9,bc.countCandidatesVotes());
		
		bc.setPartyVoted("STU", true);
		assertEquals(36,bc.countCandidatesVotes());
		bc.setPartyVoted("STU", false);
		assertEquals(9,bc.countCandidatesVotes());
		bc.setPartyVoted("STU", true);
		bc.setCandidateCrossed("STU", 802);
		assertEquals(33,bc.countCandidatesVotes());
		
//		bc.setAutoDistribution("STU",802);
//		assertEquals(36,bc.countCandidatesVotes());
//		
//		bc.setAutoDistribution("MNO", 604);
//		assertEquals(34,bc.countCandidatesVotes());
		
		
//		bc.setPartyVoted("STU", false);
//		assertEquals(7,bc.countCandidatesVotes());
	}

	

}
