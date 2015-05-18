package de.tud.vcd.common;

import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.tud.vcd.common.BallotCardDesign.DesignKeys;
import de.tud.vcd.common.exceptions.DesignKeyNotInXMLException;
import de.tud.vcd.common.exceptions.XMLCandidateNotFoundException;

@FixMethodOrder(MethodSorters.JVM)
public class BallotCardDesignTest {

	@Ignore //(expected = BallotCardDesignKeyNotFoundException.class)
	public void testGetInstanceString_CorruptedXML() throws Exception {
		InputStream filename=getClass().getClassLoader().getResource("wahlzettelCorruptedXML.xml").openStream();
		
		BallotCardDesign bcd= BallotCardDesign.getInstance(filename);
		Assert.assertNotEquals(bcd, null);
		
		
	}
	
	@Before  //testet implizit, ob der XML Import funktioniert.
	public void getInstanceString() throws Exception {
		//prepare Ressource
		
		InputStream filename=getClass().getClassLoader().getResource("wahlzettel.xml").openStream();
		//InputStream filename=getClass().getClassLoader().getResourceAsStream("wahlzettel.xml");	
		
				//Create and test
		BallotCardDesign.getInstance(filename);
		//Assert.assertNotEquals(bcd, null);
		//BallotCardDesign.getInstance().
		
	}
	
	

	
	@Ignore
	public void testGetInstance() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetElection_id() throws Exception {
		BallotCardDesign bcd= BallotCardDesign.getInstance();
		String assumption="54321";
		Assert.assertTrue("Election id is >"+bcd.getElection_id()+"<, but should be >"+assumption+"<",bcd.getElection_id().equals(assumption));
	}

	@Test
	public void testGetElection_name() throws Exception {
		BallotCardDesign bcd= BallotCardDesign.getInstance();
		String assumption="Meine Testwahl";
		Assert.assertTrue("Election name is >"+bcd.getElection_name()+"<, but should be >"+assumption+"<",bcd.getElection_name().equals(assumption));
	}

	@Test
	public void testGetDesignValue() throws Exception {
		DesignKeys dk= DesignKeys.MAXSTIMMEN;
		int assumption=71;
		int readedValue= BallotCardDesign.getInstance().getDesignValue(dk);
		Assert.assertTrue("Value of designKey "+dk.toString()+" is >"+readedValue+"<, but should be >"+assumption+"<",readedValue==assumption);
	
		dk= DesignKeys.VOTESPROKANDIDAT;
		assumption=3;
		readedValue= BallotCardDesign.getInstance().getDesignValue(dk);
		Assert.assertTrue("Value of designKey "+dk.toString()+" is >"+readedValue+"<, but should be >"+assumption+"<",readedValue==assumption);
	
	}
	
//	@Test(expected = DesignKeyNotInXMLException.class)
//	public void testGetDesignValue_KeyNotExists() throws Exception {
//		DesignKeys dk;//= DesignKeys.QR_X;
//		int assumption=123;
//		int readedValue;//= BallotCardDesign.getInstance().getDesignValue(dk);
////		Assert.assertTrue("Value of designKey "+dk.toString()+" is >"+readedValue+"<, but should be >"+assumption+"<",readedValue==assumption);
////	
//		dk= DesignKeys.VOTESPROKANDIDAT;
//		assumption=3;
//		readedValue= BallotCardDesign.getInstance().getDesignValue(dk);
//		Assert.assertTrue("Value of designKey "+dk.toString()+" is >"+readedValue+"<, but should be >"+assumption+"<",readedValue==assumption);
//	
//	}

//	@Test
//	public void testGetCandidateIds() throws Exception {
//		BallotCardDesign bcd= BallotCardDesign.getInstance();
//		ArrayList<Integer> al= bcd.getCandidateIds();
//		
//		int[] results= new int[]{99,108,165,501,659,743,808,809};
//		ArrayList<Integer> r= new ArrayList<Integer>();
//		for (int i: results){
//			r.add(i);
//		}
//		boolean testresult= al.containsAll(r);
//		Assert.assertTrue(testresult);
//		
//	}

//	@Test
//	public void testGetCandidate() throws Exception {
//		BallotCardDesign bcd= BallotCardDesign.getInstance();
//		int candidateId=743;
//		CandidateImportInterface c= bcd.getCandidate(candidateId);
//		Assert.assertEquals("Not the right name.","Höllebrand", c.getName());
//		Assert.assertEquals("Not the right prename.","Isabell", c.getPrename());
//		Assert.assertEquals("Not the right party.","PQR", c.getParty());
//		Assert.assertEquals("Not the right id.",743, c.getId());
//		
//		//744" name="Christoph Fassl" partei="PQR" />
//		candidateId=744;
//		 c= bcd.getCandidate(candidateId);
//		Assert.assertEquals("Not the right name.","Christoph Fassl", c.getName());
//		Assert.assertEquals("Not the right prename.",null, c.getPrename());
//		Assert.assertEquals("Not the right party.","PQR", c.getParty());
//		Assert.assertEquals("Not the right id.",744, c.getId());
//	}
	
	@Test (expected = XMLCandidateNotFoundException.class)
	public void testGetCandidate_NotFound() throws Exception {
		BallotCardDesign bcd= BallotCardDesign.getInstance();
		int candidateId=9999;
		bcd.getCandidate(candidateId);
	}

}
