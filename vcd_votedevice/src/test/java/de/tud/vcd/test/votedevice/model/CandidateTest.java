package de.tud.vcd.test.votedevice.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.tud.vcd.common.CandidateImportInterface;
import de.tud.vcd.common.XMLCandidate;
import de.tud.vcd.votedevice.municipalElection.model.Candidate;
import de.tud.vcd.votedevice.municipalElection.model.exceptions.CandidateAutoDistributionNotAllowedException;

public class CandidateTest {
	Candidate c1;
	Candidate c2;
	Candidate c3;
	
	CandidateImportInterface cii;
	
	@Before
	public void setUp() throws Exception {
		c1 = new Candidate(123, "Nachname", "Vorname", "ParteiABC");
		c2 = new Candidate(456, "Meyer", "Hugo", "SPD");
		
		cii = new XMLCandidate(789, "Testname", "Testvorname", "Testpartei");
		
		c3 = new Candidate(cii);
	}

	@Test
	public void testCandidate_IntStringStringString() {
		assertEquals("Nachname", c1.getName());
		assertEquals("Vorname", c1.getPrename());
		assertEquals("ParteiABC", c1.getParty());
		assertEquals(123, c1.getId());
		assertEquals(0, c1.getVotes());
		assertEquals(true, c1.isAutoDistribution());
		assertEquals(false, c1.isCrossedOut());
	}

	@Test
	public void testCandidateCandidateImportInterface() {
		assertEquals(cii.getName(), c3.getName());
		assertEquals(cii.getPrename(), c3.getPrename());
		assertEquals(cii.getParty(), c3.getParty());
		assertEquals(cii.getId(), c3.getId());
		assertEquals(0, c3.getVotes());
		assertEquals(true, c3.isAutoDistribution());
		assertEquals(false, c3.isCrossedOut());
	}

	@Test
	public void testSetAutoDistributedVotes() throws CandidateAutoDistributionNotAllowedException {
		c2.setVotes(3);
		c2.setCrossedOut();
		c2.setAutoDistribution();
		c2.setAutoDistributedVotes(2);
		assertEquals(2, c2.getVotes());
	}
	
	@Test(expected=CandidateAutoDistributionNotAllowedException.class)
	public void testSetAutoDistributedVotes_NotAllowed() throws CandidateAutoDistributionNotAllowedException {
		c2.setVotes(3);
		c2.setAutoDistributedVotes(3);
	}

	@Test
	public void testGetId() {
		assertEquals(456, c2.getId());
	}

	@Test
	public void testGetName() {
		assertEquals("Meyer", c2.getName());
	}

	@Test
	public void testGetPrename() {
		assertEquals("Hugo", c2.getPrename());
	}

	@Test
	public void testGetParty() {
		assertEquals("SPD", c2.getParty());
	}

	@Test
	public void testGetVotes() {
		assertEquals(0, c2.getVotes());
		c2.setVotes(3);
		assertEquals(3, c2.getVotes());
		c2.setCrossedOut();
		assertEquals(0, c2.getVotes());
	}

	@Test
	public void testSetVotes() {
		assertEquals(0, c2.getVotes());
		c2.setVotes(3);
		assertEquals(3, c2.getVotes());
		c2.setVotes(1);
		assertEquals(1, c2.getVotes());
		c2.setVotes(2);
		assertEquals(2, c2.getVotes());
		c2.setVotes(0);
		assertEquals(0, c2.getVotes());
	}

	@Test
	public void testIsCrossedOut() {
		assertEquals(false, c2.isCrossedOut());
		c2.setCrossedOut();
		assertEquals(true, c2.isCrossedOut());
		c2.setVotes(0);
		assertEquals(false, c2.isCrossedOut());
	}

	@Test
	public void testSetCrossedOut() {
		assertEquals(false, c2.isCrossedOut());
		c2.setCrossedOut();
		assertEquals(true, c2.isCrossedOut());
		c2.setVotes(0);
		assertEquals(false, c2.isCrossedOut());
	}

	@Test
	public void testIsAutoDistribution() throws CandidateAutoDistributionNotAllowedException {
		assertEquals(true, c2.isAutoDistribution());
		c2.setVotes(3);
		assertEquals(false, c2.isAutoDistribution());
		c2.reset();
		assertEquals(true, c2.isAutoDistribution());
		c2.setCrossedOut();
		assertEquals(false, c2.isAutoDistribution());
		c2.reset();
		c2.setAutoDistributedVotes(3);
		assertEquals(true, c2.isAutoDistribution());
		assertEquals(3, c2.getVotes());
	}

	@Test
	public void testSetAutoDistribution() {
		c2.setCrossedOut();
		assertEquals(false, c2.isAutoDistribution());
		assertEquals(true, c2.isCrossedOut());
		c2.setAutoDistribution();
		assertEquals(true, c2.isAutoDistribution());
		assertEquals(false, c2.isCrossedOut());
		
	}

	@Test
	public void testReset() {
		assertEquals("Nachname", c1.getName());
		assertEquals("Vorname", c1.getPrename());
		assertEquals("ParteiABC", c1.getParty());
		assertEquals(123, c1.getId());
		assertEquals(0, c1.getVotes());
		assertEquals(true, c1.isAutoDistribution());
		assertEquals(false, c1.isCrossedOut());
		
		c1.setVotes(5);
		assertEquals(5, c1.getVotes());
		assertEquals(false, c1.isAutoDistribution());
		assertEquals(false, c1.isCrossedOut());
		
		c1.reset();
		
		assertEquals("Nachname", c1.getName());
		assertEquals("Vorname", c1.getPrename());
		assertEquals("ParteiABC", c1.getParty());
		assertEquals(123, c1.getId());
		assertEquals(0, c1.getVotes());
		assertEquals(true, c1.isAutoDistribution());
		assertEquals(false, c1.isCrossedOut());
		
		
	}

	@Test
	public void testComparable(){
		assertEquals(-1, c1.compareTo(c2));
		assertEquals(-1, c2.compareTo(c3));
		assertEquals(-1, c1.compareTo(c3));
		
		assertEquals(0, c1.compareTo(c1));
		assertEquals(0, c2.compareTo(c2));
		assertEquals(0, c3.compareTo(c3));
		
		assertEquals(1, c2.compareTo(c1));
		assertEquals(1, c3.compareTo(c2));
		assertEquals(1, c3.compareTo(c1));
	}
}
