package de.tud.vcd.votedevice.votecastingmanipulation;

import de.tud.vcd.votedevice.municipalElection.model.BallotCard;
import de.tud.vcd.votedevice.municipalElection.model.Candidate;
import de.tud.vcd.votedevice.municipalElection.model.Party;
import de.tud.vcd.votedevice.municipalElection.model.VotingQRCodeNew;
import de.tud.vcd.votedevice.municipalElection.model.BallotCard.Validity;
import de.tud.vcd.votedevice.municipalElection.model.exceptions.CandidateAutoDistributionNotAllowedException;
import de.tud.vcd.votedevice.municipalElection.model.exceptions.CandidateNotFoundException;
import de.tud.vcd.votedevice.municipalElection.model.exceptions.PartyNotFoundException;

import java.util.Random;
import java.util.ArrayList;

public class PrintoutManipulation {

	private String string;
	private BallotCard manipulatedBC;
	public String error_type = "";
	private boolean status = false;
	
	public PrintoutManipulation(BallotCard bc) throws Exception{
		VotingQRCodeNew wz_string = new VotingQRCodeNew();
		string = wz_string.encodeWahlzettelToString(bc);
		manipulateBC(bc);
		bc.setError(error_type);
		//System.out.println("error type: " + error_type);
	}
	
	public PrintoutManipulation(){
		
	}
	
	private BallotCard manipulateBC(BallotCard bc) throws PartyNotFoundException, CandidateNotFoundException, CandidateAutoDistributionNotAllowedException{
		int pID = -1;
		int plen = -1;
		int rnd = -1;
		int rnd_01 = -1;
		
		if(bc.isValid()){
			String mstring = string.substring(6,string.indexOf("_"));
			//Wenn crossed nicht leer, dann wird die variable crossed mit den rest gefüllt
			//dies führt später dazu, dass entweder geadded oder exchanged wird
			String crossed;
			if(string.endsWith("_")){
				crossed = "";
			}else{
				crossed = string.substring(string.indexOf("_"));
			}
			
			int lstring = mstring.length();
			
			if(bc.countVotedParties() == 1){
			pID = getPartyID(bc.getPartyList(), bc);
			plen = bc.getParty(pID).getCandidates().size();
			}
		//	System.out.println("Party ID & length & stringleng: " + pID + " " + plen + " " +lstring);
			
			if(lstring == 2 && bc.countVotedParties() == 1 && crossed.isEmpty()){
				if(!status){
					exchange_partylist(bc,pID);
					status = true;
					System.out.println("Fehler 1");
				}
				//remove_candidateFromList(bc, plen, pID);
			}
			
			if(lstring == 2 && bc.countVotedParties() == 1 && !crossed.isEmpty()){
				if(!status){
				exchange_crossedcandidate(bc,plen, pID);
				status = true;
				System.out.println("Fehler 2");
				}
				//addOrexchange_candidateFromList(bc,plen,pID);
			}
			
			String mvoted = mstring.substring(2);
			
			//wenn es direkte Stimmen gibt
			if(lstring >= 3){// && (bc.countVotedParties() == 0 || bc.countVotedParties() > 1)){
			     rnd = new Random().nextInt(3);  
				 rnd_01 = new Random().nextInt(2);
			     //Diese IF-Abfrage vermeidet, dass der Stimmzettel ungültig wird, falls er geheilt wurde, d.h. in diesem 
			     //Fall kann man höchstens Stimmen tauschen aber keine Kandidaten, ansonsten ist die Manipulation zu
			     //offensichtlich
			     if(bc.getValidity() == Validity.VALID_REDUCE_CANDIDATES){
			    	 if(!status){
			    		 exchange_votes(bc,mvoted);
			    		 status = true;
			    		 System.out.println("Fehler 4");
			    	 }
			     }
			     
			     switch(rnd){
			     		case 0: 
			     			if(!status){
			     			exchange_candidate(bc,mvoted);
			     			status = true;
			     			System.out.println("Fehler 3");
			     			}
				    	 break;
				    	 case 1: 
				    		 if(!status){
					    	 exchange_votes(bc,mvoted);
					    	 status = true;
					    	 System.out.println("Fehler 4");
					    	 }
				    	break;
				    	 case 2:
				    		 if(!status && !crossed.isEmpty()){
				    		 exchange_crossedcandidate(bc,plen, pID);
						     status = true;
						     System.out.println("Fehler 2");
						     }
			     }
			     
			   if(!status && crossed.isEmpty() && rnd == 2){ 
				   switch(rnd_01){
				   case 0: 
		     			if(!status){
		     			exchange_candidate(bc,mvoted);
		     			status = true;
		     			System.out.println("Fehler 3");
		     			}
			    	 break;
			    	 case 1: 
			    		 if(!status){
				    	 exchange_votes(bc,mvoted);
				    	 status = true;
				    	 System.out.println("Fehler 4");
				    	 }
			    	break;
			     
				   }
			   }
			     
			}
			
			mstring = "";
		}
		//string = string + error_type;
		return manipulatedBC;
	}
	
	private void exchange_partylist(BallotCard b, int pid) throws PartyNotFoundException{
		int new_pID = new Random().nextInt(b.getPartyList().size()) + 1;
		//System.out.println("partei alt: " + pid + "grösse: " + b.getPartyList().size() + "partei neu: " +new_pID);
		//Falls die zufällig gewählte partei, dieselbe ist, die ersetzt werden soll dann
		// weiter zufällig eine andere Partei aussuchen.
		while(new_pID == pid){
			new_pID = new Random().nextInt(b.getPartyList().size());
		}
		
		b.setPartyVoted(b.getParty(pid).getName(), false);
		b.setPartyVoted(b.getParty(new_pID).getName(), true);
		b.getParty(new_pID).distributeVotes(71);
		error_type = "*1";
	}
	
	private void exchange_crossedcandidate(BallotCard b, int len, int pid)throws PartyNotFoundException, CandidateAutoDistributionNotAllowedException{
		int candNo = new Random().nextInt(len);
		int crossedCandidate = -1;
		int i = 0;
		ArrayList<Integer> crossedlist = new ArrayList<Integer>();
		//wenn der zufällig gewählte Kandidat gestriechen ist, dann einen anderen Kandidaten
		//zufällig auswählen
		while(b.getParty(pid).getCandidates().get(candNo).isCrossedOut()){
			candNo = new Random().nextInt(len);
		}
		
		//Erzeuge eine Liste mit den gestriechenen Kandidaten
		for (Candidate c: b.getParty(pid).getCandidates()){  
			   if(c.isCrossedOut()){
				   crossedlist.add(i);
				  //crossedCandidate = i;
			   }
			   i++;
		}
		
		//Wähle zufällig eins aus der Liste zum Hinzufügen
		crossedCandidate = crossedlist.get(new Random().nextInt(crossedlist.size()));
		
		//System.out.println(" " + candNo + " " + crossedCandidate);
		//ersetze die Kandidaten
		b.getParty(pid).getCandidates().get(candNo).setCrossedOut();
		b.getParty(pid).getCandidates().get(crossedCandidate).reset();
		b.getParty(pid).distributeVotes(71);
		error_type = "*2";
	}
	
	
	private void exchange_candidate(BallotCard b, String manualvotes) throws PartyNotFoundException{
		 int len = manualvotes.length() / 5;
		 int pos = new Random().nextInt(len);
		 String candNo = manualvotes.substring(pos*5, pos*5+4);
		 int pID = -1;
		 int candno = -1;
		 
		 //finde die Partei ID
		 if(Integer.parseInt(candNo.substring(0,1)) == 0){
			 pID =  Integer.parseInt(candNo.substring(1,2));
		 }else{
			 pID = 10;
		 }
		 
		// System.out.println("mvotes: " + manualvotes + "candNo:" + candNo + "pid: " + pID);
		 
		 int ex_party = new Random().nextInt(10) + 1;
	     int ex_cand = new Random().nextInt(b.getParty(ex_party).getCandidates().size());
		 
	    // System.out.println("exp: "+ ex_party + "ex_cand: " + ex_cand);
		 candno = (10 * Integer.parseInt(candNo.substring(2,3))) + Integer.parseInt(candNo.substring(3)) - 1;
			 
		 //System.out.println("party: " + pID + "Kand: " + candno + "exparty: " + ex_party + "exkand: " + ex_cand);
		 //solange der zufällig gewählte Kandidat Stimmen hat suche einen anderen Kandidaten
		 while(b.getParty(ex_party).getCandidates().get(ex_cand).getVotes() > 0){
			   ex_party = new Random().nextInt(10);
			   ex_cand = new Random().nextInt(b.getParty(ex_party).getCandidates().size());
		 }
			
		//System.out.println("exparty: " + ex_party + "exkand: " + ex_cand);
			 
		 int votes = b.getParty(pID).getCandidates().get(candno).getVotes();
		 int reducedvotes = b.getParty(pID).getCandidates().get(candno).getReducedVotes();
			 
		 //System.out.println("votes: " + votes);
		// b.getParty(pID).removeCandidate(candno);
		 b.getParty(pID).getCandidates().get(candno).reset();
		 b.getParty(ex_party).getCandidates().get(ex_cand).setVotes(votes);
		 b.getParty(ex_party).getCandidates().get(ex_cand).setReducedVotes(reducedvotes);
		 
		 error_type = "*3";
	}
	
	private void exchange_votes(BallotCard b, String manualvotes) throws PartyNotFoundException{
		 int len = manualvotes.length() / 5;
		 int p3 = -1;
		 int p2 = -1;
//		 int p2b = -1;
		 int p1 = -1;
		 int c3 = -1;
		 int c2 = -1;
//		 int c2b = -1;
		 int c1 = -1;
		 String cand3 = "";
		 String cand2 = "";
//		 String cand2b = "";
		 String cand1 = "";
		 int pos3 = -1;
		 int pos2 = -1;
//		 int pos2b = -1;
		 int pos1 = -1;
		 int i = 0;
		 String current = "";
		 int rnd_p = -1;
		 int rnd_q = -1;
		//Teile die Kandidaten nach deren Stimmen
		 ArrayList<String> cand_3 = new ArrayList<String>();
		 ArrayList<String> cand_2 = new ArrayList<String>();
		 ArrayList<String> cand_1 = new ArrayList<String>();
		 boolean exchange = false;
		 
		//wenn die Person nur einen einzigen Kandidaten gewählt hat, und die zufallszahl führt dazu, dass man dem gewählten 
		 //Kandidaten eine Stimme nimmt, und einem anderen gewählten zuordnet, den es aber nicht gibt, dann bleibt nichts 
		 //anderes übrig, als die austauschmethode aufzurufen.
		 if(len == 1){
			 if(!status){
				 exchange_candidate(b,manualvotes);
				 System.out.println("exchange candidate stelle 1 by exchange vote");
			 }
			 status = true;
			 
		 }
		 while(i<len){
			current = manualvotes.substring(i*5, i*5+5);
			
			//System.out.println("current: " + current);
			if(Integer.parseInt(current.substring(4,5)) == 1){
				cand_1.add(current);
			}
			if(Integer.parseInt(current.substring(4,5)) == 2){
				cand_2.add(current);
			}
			if(Integer.parseInt(current.substring(4,5)) == 3){
				cand_3.add(current);
			}
			 i++;
		 }
		 
		 //Alle gewählte Kandidaten scheinen gleiche Anzahl Stimmen bekommen zu haben, d.h. entweder 3 oder 1
		 //daher kann man hier nur austauschen, wenn man zufällig in dieser Methode gelangt ist
		 if((cand_2.isEmpty() && cand_1.isEmpty()) || (cand_3.isEmpty() && cand_2.isEmpty())){
			 if(!status){
			 exchange_candidate(b,manualvotes);
			 System.out.println("exchange candidate stelle 2 by exchange vote");
			 }
			 status = true;
		 }
	
	if(!status){
		 //wenn gruppe2 leer, dann tausche zw gruppe1 und 3, weil mind. zwei gruppen voll sind
		 if(cand_2.isEmpty()){
			 pos3 = new Random().nextInt(cand_3.size());
			 pos1 = new Random().nextInt(cand_1.size());
			 
			 cand3 = cand_3.get(pos3);
			 cand1 = cand_1.get(pos1);
			 
			 p3 = get_pid(cand3);
			 p1 = get_pid(cand1);
			 
			 c3 = get_candid(cand3);
			 c1 = get_candid(cand1);
			//System.out.println("pos: " + pos3 + pos1 + "cand: " + cand3 + cand1 + "party: " + p3 + p1 + "candid: " + c3 + c1 + "votes: " + v3 + v1);
			 exchange(b, p3, p1, c3, c1);
			 
		//	 b.getParty(p3).getCandidates().get(c3).reset();
		//	 b.getParty(p3).getCandidates().get(c3).reset();
		//	 b.getParty(p3).getCandidates().get(c3).setVotes(1);
		//	 b.getParty(p1).getCandidates().get(c1).setVotes(3);
			 exchange = true;
		 }else{
			 //Wenn gruppe2 nicht leer, aber die 1 und die 3 sind leer, dann geht nur Kandidaten tauschen, sonst müsste man Stimmen
			 //weglassen
			 if(cand_1.isEmpty() && cand_3.isEmpty()){
			//	 pos2 = new Random().nextInt(cand_2.size());
			//	 pos2b = new Random().nextInt(cand_2.size());
				 //solange dies der selbe zufällig ist suche einen anderen
			//	 while(pos2b == pos2){
			//		 pos2 = new Random().nextInt(cand_2.size());
			//	 }
				 
			//	 cand2 = cand_2.get(pos2);
			//	 cand2b = cand_2.get(pos2b);
			//	 p2 = get_pid(cand2);
			//	 p2b = get_pid(cand2b);
			//	 c2 = get_candid(cand2);
			//	 c2b = get_candid(cand2b);
				 
			//	 exchange(b, p2, p2b, c2, c2b);
	//			 b.getParty(p2).getCandidates().get(c2).reset();
	//			 b.getParty(p2b).getCandidates().get(c2b).reset();
	//			 b.getParty(p2).getCandidates().get(c2).setVotes(3);
	//			 b.getParty(p2b).getCandidates().get(c2b).setVotes(1);
			   
				 exchange_candidate(b,manualvotes);
				 System.out.println("exchange candidate stelle 3 by exchange vote");
				 status = true;
				 exchange = true;
			 }
			 
		 }
		 
		 //suche zw gruppen zum austauschen zufällig (hier könnte gruppe1 oder 3 leer sein)
		 rnd_p = new Random().nextInt(3);
		 rnd_q = new Random().nextInt(3);
		 
		//wenn die zufalls gruppen gleich sind, und es handelt sich um gruppen die nur eine 
		 //oder drei stimmen haben, müssen weitere zufallszahlen gezogen werden, damit ein tausch
		 //auch möglich ist. D.h. diese Austausche sind möglich: 0/2;1/2;1/1;
		 while(rnd_p == rnd_q && (rnd_p == 0 || rnd_p == 2)){
			  rnd_p = new Random().nextInt(3);
			  rnd_q = new Random().nextInt(3);
		 }
		System.out.println("rnds " + rnd_p + rnd_q);
	if(!exchange){
		 //wenn der tausch zw gruppe1 und 3 stattfinden soll. hier soll man überprüfen ob 1 der 
		 //gruppen leer ist und daher die volle mit der zwei tauschen soll
		 if((rnd_p == 0 || rnd_p == 2) && (rnd_q == 0 || rnd_q == 2)){
			
			 //wenn einer der beiden leer ist, dann muss die volle mit der gruppe2 tauschen
			 if((cand_1.isEmpty() || cand_3.isEmpty())){
				 
				 if(cand_1.isEmpty()){
					 pos3 = new Random().nextInt(cand_3.size());
					 pos2 = new Random().nextInt(cand_2.size());
					 
					 cand3 = cand_3.get(pos3);
					 cand2 = cand_2.get(pos2);
					 
					 p3 = get_pid(cand3);
					 p2 = get_pid(cand2);
					 
					 c3 = get_candid(cand3);
					 c2 = get_candid(cand2);
					 
					 exchange(b, p3, p2, c3, c2);
					 exchange = true;
			//		 b.getParty(p2).getCandidates().get(c2).reset();
			//		 b.getParty(p3).getCandidates().get(c3).reset();
			//		 b.getParty(p2).getCandidates().get(c2).setVotes(3);
			//		 b.getParty(p3).getCandidates().get(c3).setVotes(2);
				 }else{
					 pos1 = new Random().nextInt(cand_1.size());
					 pos2 = new Random().nextInt(cand_2.size());
					 
					 cand1 = cand_1.get(pos1);
					 cand2 = cand_2.get(pos2);
					 
					 p1 = get_pid(cand1);
					 p2 = get_pid(cand2);
					 
					 c1 = get_candid(cand1);
					 c2 = get_candid(cand2);
					 
					 exchange(b, p2, p1, c2, c1);
					 exchange = true;
			//		 b.getParty(p1).getCandidates().get(c1).reset();
			//		 b.getParty(p2).getCandidates().get(c2).reset();
			//		 b.getParty(p1).getCandidates().get(c1).setVotes(2);
			//		 b.getParty(p2).getCandidates().get(c2).setVotes(1);
					 
				 }
				 
			 }else{
				 //beide gruppen sind voll, also kann dazwischen getauscht werden
				 pos3 = new Random().nextInt(cand_3.size());
				 pos1 = new Random().nextInt(cand_1.size());
				 
				 cand3 = cand_3.get(pos3);
				 cand1 = cand_1.get(pos1);
				 
				 p3 = get_pid(cand3);
				 p1 = get_pid(cand1);
				 
				 c3 = get_candid(cand3);
				 c1 = get_candid(cand1);
				//System.out.println("pos: " + pos3 + pos1 + "cand: " + cand3 + cand1 + "party: " + p3 + p1 + "candid: " + c3 + c1 + "votes: " + v3 + v1);
				 exchange(b, p3, p1, c3, c1);
				 exchange = true;
			//	 b.getParty(p3).getCandidates().get(c3).reset();
			//	 b.getParty(p3).getCandidates().get(c3).reset();
			//	 b.getParty(p3).getCandidates().get(c3).setVotes(1);
			//	 b.getParty(p1).getCandidates().get(c1).setVotes(3);
			 }
		 }
		 
		 //wenn der tausch zw gruppe2 und 3 stattfinden soll. hier soll man überprüfen ob 
		 //gruppe3 leer ist, wenn ja tausche mit gruppe 1
		 if((rnd_p == 1 || rnd_p == 2) && (rnd_q == 1 || rnd_q == 2)){
			 
			 
			 //wenn die gruppe3 leer ist dann tausche zw 1 und 2
				 if(cand_3.isEmpty()){
					 pos1 = new Random().nextInt(cand_1.size());
					 pos2 = new Random().nextInt(cand_2.size());
					 
					 cand1 = cand_1.get(pos1);
					 cand2 = cand_2.get(pos2);
					 
					 p1 = get_pid(cand1);
					 p2 = get_pid(cand2);
					 
					 c1 = get_candid(cand1);
					 c2 = get_candid(cand2);
					 
					 exchange(b, p2, p1, c2, c1);
				//	 b.getParty(p1).getCandidates().get(c1).reset();
				//	 b.getParty(p2).getCandidates().get(c2).reset();
				//	 b.getParty(p1).getCandidates().get(c1).setVotes(2);
				//	 b.getParty(p2).getCandidates().get(c2).setVotes(1);
					 exchange = true;
				 }else{
					 pos3 = new Random().nextInt(cand_3.size());
					 pos2 = new Random().nextInt(cand_2.size());
					 
					 cand3 = cand_3.get(pos3);
					 cand2 = cand_2.get(pos2);
					 
					 p3 = get_pid(cand3);
					 p2 = get_pid(cand2);
					 
					 c3 = get_candid(cand3);
					 c2 = get_candid(cand2);
					 
					 exchange(b, p3, p2, c3, c2);
			//		 b.getParty(p2).getCandidates().get(c2).reset();
			//		 b.getParty(p3).getCandidates().get(c3).reset();
			//		 b.getParty(p2).getCandidates().get(c2).setVotes(3);
			//		 b.getParty(p3).getCandidates().get(c3).setVotes(2);
					 exchange = true;
				 }
		 }
		 
		//wenn der tausch zw gruppe2 und 3 stattfinden soll. hier soll man überprüfen ob 
		 //gruppe3 leer ist, wenn ja tausche mit gruppe 1
		 if((rnd_p == 0 || rnd_p == 1) && (rnd_q == 0 || rnd_q == 1)){
			 
			 //wenn die gruppe1 leer ist dann tausche zw 3 und 2
				 if(cand_1.isEmpty()){
					 pos3 = new Random().nextInt(cand_3.size());
					 pos2 = new Random().nextInt(cand_2.size());
					 
					 cand3 = cand_3.get(pos3);
					 cand2 = cand_2.get(pos2);
					 
					 p3 = get_pid(cand3);
					 p2 = get_pid(cand2);
					 
					 c3 = get_candid(cand3);
					 c2 = get_candid(cand2);
					 
					 exchange(b, p3, p2, c3, c2);
					 exchange = true;
			//		 b.getParty(p2).getCandidates().get(c2).reset();
			//		 b.getParty(p3).getCandidates().get(c3).reset();
			//		 b.getParty(p2).getCandidates().get(c2).setVotes(3);
			//		 b.getParty(p3).getCandidates().get(c3).setVotes(2);
				 }else{
					 pos1 = new Random().nextInt(cand_1.size());
					 pos2 = new Random().nextInt(cand_2.size());
					 
					 cand1 = cand_1.get(pos1);
					 cand2 = cand_2.get(pos2);
					 
					 p1 = get_pid(cand1);
					 p2 = get_pid(cand2);
					 
					 c1 = get_candid(cand1);
					 c2 = get_candid(cand2);
					 
					 exchange(b, p2, p1, c2, c1);
			//		 b.getParty(p1).getCandidates().get(c1).reset();
			//		 b.getParty(p2).getCandidates().get(c2).reset();
			//		 b.getParty(p1).getCandidates().get(c1).setVotes(2);
			//		 b.getParty(p2).getCandidates().get(c2).setVotes(1);
					 exchange = true;
				 }
		 }
		 
		//wenn der tausch zw gruppe2 stattfinden soll. hier soll man überprüfen ob 
		 //gruppe2 mind. zwei kandidaten hat, ansonsten wähle 1 oder 3 zufällig
		 if(rnd_p == 1 && rnd_q == 1){
			 
			 //es gibt min. 2 kandidaten in gruppe 2 also kann der kandidatentausch stattfinden
			 if(cand_2.size() > 1){
			//	 pos2 = new Random().nextInt(cand_2.size());
			//	 pos2b = new Random().nextInt(cand_2.size());
				 //solange dies der selbe zufällig ist suche einen anderen
			//	 while(pos2b == pos2){
			//		 pos2 = new Random().nextInt(cand_2.size());
			//	 }
				 
			//	 cand2 = cand_2.get(pos2);
			//	 cand2b = cand_2.get(pos2b);
			//	 p2 = get_pid(cand2);
			//	 p2b = get_pid(cand2b);
			//	 c2 = get_candid(cand2);
			//	 c2b = get_candid(cand2b);
				 
			//	 exchange(b, p2, p2b, c2, c2b);

				 exchange_candidate(b,manualvotes);
				 System.out.println("exchange candidate stelle 4 by exchange vote");
				 status = true;
				 exchange = true;
			//	 b.getParty(p2).getCandidates().get(c2).reset();
			//	 b.getParty(p2b).getCandidates().get(c2b).reset();
			//	 b.getParty(p2).getCandidates().get(c2).setVotes(3);
			//	 b.getParty(p2b).getCandidates().get(c2b).setVotes(1);
			 
			 }else{
			 
				 int z = new Random().nextInt(2);
				 
				 switch(z){
				 //wenn die gruppe1 leer ist dann tausche zw 3 und 2
				 case 0: if(cand_1.isEmpty()){
					 pos3 = new Random().nextInt(cand_3.size());
					 pos2 = new Random().nextInt(cand_2.size());
					 
					 cand3 = cand_3.get(pos3);
					 cand2 = cand_2.get(pos2);
					 
					 p3 = get_pid(cand3);
					 p2 = get_pid(cand2);
					 
					 c3 = get_candid(cand3);
					 c2 = get_candid(cand2);
					 
					 exchange(b, p3, p2, c3, c2);
					 exchange = true;
				//	 b.getParty(p2).getCandidates().get(c2).reset();
				//	 b.getParty(p3).getCandidates().get(c3).reset();
				//	 b.getParty(p2).getCandidates().get(c2).setVotes(3);
				//	 b.getParty(p3).getCandidates().get(c3).setVotes(2);
				 }else{
					 pos1 = new Random().nextInt(cand_1.size());
					 pos2 = new Random().nextInt(cand_2.size());
					 
					 cand1 = cand_1.get(pos1);
					 cand2 = cand_2.get(pos2);
					 
					 p1 = get_pid(cand1);
					 p2 = get_pid(cand2);
					 
					 c1 = get_candid(cand1);
					 c2 = get_candid(cand2);
					 
					 exchange(b, p2, p1, c2, c1);
					 exchange = true;
			//		 b.getParty(p1).getCandidates().get(c1).reset();
			//		 b.getParty(p2).getCandidates().get(c2).reset();
			//		 b.getParty(p1).getCandidates().get(c1).setVotes(2);
			//		 b.getParty(p2).getCandidates().get(c2).setVotes(1); 
				 };
				 //wenn die 3 leer ist, dann tausche 1 und 2
				 case 1: if(cand_3.isEmpty()){
					 pos1 = new Random().nextInt(cand_1.size());
					 pos2 = new Random().nextInt(cand_2.size());
					 
					 cand1 = cand_1.get(pos1);
					 cand2 = cand_2.get(pos2);
					 
					 p1 = get_pid(cand1);
					 p2 = get_pid(cand2);
					 
					 c1 = get_candid(cand1);
					 c2 = get_candid(cand2);
					 
					 exchange(b, p2, p1, c2, c1);
					 exchange = true;
					 
				//	 b.getParty(p1).getCandidates().get(c1).reset();
				//	 b.getParty(p2).getCandidates().get(c2).reset();
				//	 b.getParty(p1).getCandidates().get(c1).setVotes(2);
				//	 b.getParty(p2).getCandidates().get(c2).setVotes(1); 
				 }else{
					 pos3 = new Random().nextInt(cand_3.size());
					 pos2 = new Random().nextInt(cand_2.size());
					 
					 cand3 = cand_3.get(pos3);
					 cand2 = cand_2.get(pos2);
					 
					 p3 = get_pid(cand3);
					 p2 = get_pid(cand2);
					 
					 c3 = get_candid(cand3);
					 c2 = get_candid(cand2);
					 
					 exchange(b, p3, p2, c3, c2);
					 exchange = true;
					 
				//	 b.getParty(p2).getCandidates().get(c2).reset();
				//	 b.getParty(p3).getCandidates().get(c3).reset();
				//	 b.getParty(p2).getCandidates().get(c2).setVotes(3);
				//	 b.getParty(p3).getCandidates().get(c3).setVotes(2);
				 };
			  } 
		 }
	  } 
	}
	
	if(!status && exchange == true){
		error_type = "*4";
	}
  }
 }	
	
	private int getPartyID(ArrayList<Party> parties, BallotCard b){
		int id = 0;
		
		for(Party p: parties){
			if(p.isVoted() && b.countVotedParties() == 1){
				id = p.getId();
			}
		}
		return id;
	}
	
	private int get_pid(String s){
		int pid = -1;
		//System.out.println("get_pid:" + s);
		if(Integer.parseInt(s.substring(0,1)) == 0){
			 pid =  Integer.parseInt(s.substring(1,2));
		 }else{
			 pid = 10;
		 }
		 
		 return pid;
	}
	
	private int get_candid(String s){
		int candid = -1;
		candid = (10 * Integer.parseInt(s.substring(2,3))) + Integer.parseInt(s.substring(3,4)) - 1;
		
		return candid;
	}
	
	private void exchange(BallotCard bc, int P, int p, int C, int c) throws PartyNotFoundException{
		
		int votes_C = bc.getParty(P).getCandidates().get(C).getVotes();
		int reduced_votes_C = bc.getParty(P).getCandidates().get(C).getReducedVotes();
				
		int votes_c = bc.getParty(p).getCandidates().get(c).getVotes();
		int reduced_votes_c = bc.getParty(p).getCandidates().get(c).getReducedVotes();
						
		bc.getParty(P).getCandidates().get(C).setVotes(votes_c);
		bc.getParty(P).getCandidates().get(C).setReducedVotes(reduced_votes_c);
		
		bc.getParty(p).getCandidates().get(c).setVotes(votes_C);
		bc.getParty(p).getCandidates().get(c).setReducedVotes(reduced_votes_C);
	}
}