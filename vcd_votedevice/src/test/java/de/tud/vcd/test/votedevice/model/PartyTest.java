package de.tud.vcd.test.votedevice.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import de.tud.vcd.common.CandidateImportInterface;
import de.tud.vcd.common.XMLCandidate;
import de.tud.vcd.votedevice.municipalElection.model.Candidate;
import de.tud.vcd.votedevice.municipalElection.model.Party;
import de.tud.vcd.votedevice.municipalElection.model.exceptions.CandidateAutoDistributionNotAllowedException;
import de.tud.vcd.votedevice.municipalElection.model.exceptions.CandidateNotFoundException;
import de.tud.vcd.votedevice.municipalElection.model.exceptions.CannotAddCandidateFalsePartyException;

public class PartyTest {

	CandidateImportInterface[] c;
	
	
	
	Party p;
		
	@Before
	public void setUp() throws Exception {
		//Eine Partei einrichten:
		CandidateImportInterface[] c= new XMLCandidate[10];
		
		c[0] = new XMLCandidate(001, "Name0", "Vorname0", "BCD");
		c[8]= new XMLCandidate(111, "Name1", "Vorname1", "BCD");
		c[2] = new XMLCandidate(222, "Name2", "Vorname2", "BCD");
		c[5] = new XMLCandidate(333, "Name3", "Vorname3", "BCD");
		c[4] = new XMLCandidate(444, "Name4", "Vorname4", "BCD");
		c[3] = new XMLCandidate(555, "Name5", "Vorname5", "BCD");
		c[6] = new XMLCandidate(666, "Name6", "Vorname6", "BCD");
		c[7] = new XMLCandidate(777, "Name7", "Vorname7", "BCD");
		c[1] =new XMLCandidate(888, "Name8", "Vorname8", "BCD");
		c[9] = new XMLCandidate(999, "Name9", "Vorname9", "BCD");
		
		
		
		p=new Party(543, "BCD", 3,71);
		for (CandidateImportInterface cand: c){
			p.addCandidate(cand);
		}
		
		
		
	}

	

	@Test
	public void testGetId() {
		assertEquals(543,p.getId());
	}

	@Test
	public void testGetName() {
		assertEquals("BCD",p.getName());
	}

	@Test
	public void testIsVotedtestSetVoted() {
		assertEquals(false,p.isVoted());
		p.setVoted(true);
		assertEquals(true,p.isVoted());
		p.setVoted(false);
		assertEquals(false,p.isVoted());
	}

	

	@Test
	public void testGetCandidate() throws CandidateNotFoundException {
		String name;
		name= p.getCandidate(333).getName();
		assertEquals("Name3",name);
		name= p.getCandidate(888).getName();
		assertEquals("Name8",name);
	}
	
	@Test(expected=CandidateNotFoundException.class)
	public void testGetCandidate_NotFound() throws CandidateNotFoundException {
		String name;
		name= p.getCandidate(987).getName();
		assertEquals("Name3",name);
	}

	@Test
	public void testGetCandidates() {
		ArrayList<Candidate> alc=p.getCandidates();
		assertEquals(10, alc.size());
		assertEquals("Name2",alc.get(2).getName());
	}
	
	@Test
	public void testOrderOfCandidates() throws CannotAddCandidateFalsePartyException {
		CandidateImportInterface c10=new XMLCandidate(876, "Name876", "Vorname876", "BCD");
		CandidateImportInterface c11=new XMLCandidate(345, "Name345", "Vorname345", "BCD");
		CandidateImportInterface c12=new XMLCandidate(003, "Name003", "Vorname003", "BCD");
		
		p.addCandidate(c10);
		p.addCandidate(c11);
		p.addCandidate(c12);
		
		ArrayList<Candidate> alc=p.getCandidates();
		assertEquals(13, alc.size());
		for (int i=1;i<alc.size();i++){
			assertEquals("False order: "+alc.get(i-1).getId()+" < "+alc.get(i).getId()+"is not correct",true,(alc.get(i-1).getId()<alc.get(i).getId()) );
		}
			assertEquals(001,alc.get(0).getId());
	}

	@Test
	public void testAddCandidate() throws CandidateNotFoundException, CannotAddCandidateFalsePartyException {
		CandidateImportInterface c10=new XMLCandidate(432, "NameNeu", "VornameNeu", "BCD");
		assertEquals(10,p.getCandidates().size());
		p.addCandidate(c10);
		assertEquals(11,p.getCandidates().size());
		assertEquals(432,p.getCandidate(432).getId());
	}
	
	@Test(expected=CannotAddCandidateFalsePartyException.class)
	public void testAddCandidate_falseParty() throws CandidateNotFoundException, CannotAddCandidateFalsePartyException {
		CandidateImportInterface cError = new XMLCandidate(123, "NameFalse", "VornameFalse", "ABC");
		
		assertEquals(10,p.getCandidates().size());
		p.addCandidate(cError);
		assertEquals(10,p.getCandidates().size());
	}
	
	@Test
	public void testAddCandidateWithErrorWithoutChangesAtModel() throws CandidateNotFoundException {
		CandidateImportInterface cError = new XMLCandidate(123, "NameFalse", "VornameFalse", "ABC");
		
		assertEquals(10,p.getCandidates().size());
		try {
			p.addCandidate(cError);
		} catch (CannotAddCandidateFalsePartyException e) {
			//do nothing, ignore command
			//e.printStackTrace();
		}
		assertEquals(10,p.getCandidates().size());
	}

	@Test
	public void testCountVotes() throws CandidateNotFoundException, CandidateAutoDistributionNotAllowedException {
		p.getCandidate(111).setVotes(3);
		p.getCandidate(333).setCrossedOut();
		p.getCandidate(555).setVotes(0);
		p.getCandidate(666).setVotes(1);
		
		assertEquals(4, p.countVotes());
		
		p.distributeVotes(5);
		assertEquals(4, p.countVotes());
		
		p.setVoted(true);
		p.distributeVotes(5);
		assertEquals(9, p.countVotes());
		
		p.setVoted(false);
		assertEquals(4, p.countVotes());
	}

	@Test
	public void testCountDistributedVotes() throws CandidateNotFoundException, CandidateAutoDistributionNotAllowedException {
		p.getCandidate(111).setVotes(3);
		p.getCandidate(333).setCrossedOut();
		p.getCandidate(555).setVotes(0);
		p.getCandidate(666).setVotes(1);
		
		assertEquals(4, p.countVotes());
		p.setVoted(true);
		
		p.distributeVotes(5);
		
		assertEquals(9, p.countVotes());
		assertEquals(5, p.countDistributedVotes());
	}

	@Test
	public void testCountManualVotes() throws CandidateNotFoundException, CandidateAutoDistributionNotAllowedException {
		p.getCandidate(111).setVotes(3);
		p.getCandidate(333).setCrossedOut();
		p.getCandidate(555).setVotes(0);
		p.getCandidate(666).setVotes(1);
		
		assertEquals(4, p.countManualVotes());
		
		p.distributeVotes(5);
		
		assertEquals(4, p.countManualVotes());
		
		p.getCandidate(999).setVotes(2);
		
		assertEquals(6, p.countManualVotes());
	}

	@Test
	public void testDistributeVotes() throws CandidateNotFoundException, CandidateAutoDistributionNotAllowedException {
		p.getCandidate(111).setVotes(3);
		p.getCandidate(333).setCrossedOut();
		p.getCandidate(555).setVotes(0);
		p.getCandidate(666).setVotes(1);
		
		p.setVoted(true);
		
		assertEquals(4, p.countVotes());
		assertEquals(0, p.countDistributedVotes());
		
		p.distributeVotes(5);
		assertEquals(9, p.countVotes());
		assertEquals(5, p.countDistributedVotes());
		
		p.distributeVotes(3);
		assertEquals(7, p.countVotes());
		assertEquals(3, p.countDistributedVotes());
		
		p.distributeVotes(9);
		assertEquals(13, p.countVotes());
		assertEquals(9, p.countDistributedVotes());
		
		p.distributeVotes(20);
		assertEquals(22, p.countVotes());
		assertEquals(18, p.countDistributedVotes());
		
	}

}
