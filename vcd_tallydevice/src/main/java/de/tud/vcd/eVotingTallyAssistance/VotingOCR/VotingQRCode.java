/**
 * 
 */
package de.tud.vcd.eVotingTallyAssistance.VotingOCR;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import de.tud.vcd.common.BallotCardDesign;
import de.tud.vcd.common.BallotCardDesign.DesignKeys;
import de.tud.vcd.common.CandidateImportInterface;
import de.tud.vcd.eVotingTallyAssistance.common.Candidate;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.CandidateNotKnownException;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.DoubleCandidateIdException;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.ElectionIdIsNotEqualException;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.PartyNotExistsException;
import de.tud.vcd.eVotingTallyAssistance.gui.resultGui.CandidateShow;
import de.tud.vcd.eVotingTallyAssistance.model.RegelChecker;
import de.tud.vcd.eVotingTallyAssistance.model.Stimme;
import de.tud.vcd.eVotingTallyAssistance.model.Wahlzettel;
import de.tud.vcd.votedevice.municipalElection.model.exceptions.CandidateAutoDistributionNotAllowedException;


/**
 * Kümmert sich um das Erzeugen und Lesen von QR Codes. Durch diese Klassen entsteht zwar eine höhere 
 * Abhängigkeit der Klassen untereinander, aber sind vom Aufgabenschwerpunkt besser getrennt.
 * Die ganze Klasse ist statisch, da in ihr kein Wissen liegt, sondern nur Methoden zur Verfügung stellt
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class VotingQRCode {

	
	/**
	 * 
	 */
	public VotingQRCode() {
	}
	
	/**
	 * Erzeugt aus einem übergeben QR Code einen Wahlzettel, um mit diesem arbeiten zu können.
	 * 
	 * @param qrcodeImage : BufferedImage mit dem QR Code
	 * @param id Integer id des Wahlzettels
	 * @param rc der RegelChecker
	 * @return Wahlzettel
	 * 
	 * @throws Exception
	 */
	public static Wahlzettel decodeQRtoWahlzettel(BufferedImage qrcodeImage, int id,RegelChecker rc) throws Exception {
		String qrString= readQRCode(qrcodeImage);
		Wahlzettel wz= decodeStringToWahlzettel(id, rc, qrString);
		return wz;
	}
	
	
	/**
	 * Erzeugt aus einem Wahlzettel einen QR Code der die Stimmen und die Gültigkeit enthält.
	 * 
	 * Die Kodierung des interen Strings setzt als erstes Zeichen ein + oder - für einen gültigen oder ungültigen Wahlzettel. Darauffolgend 
	 * kommt jeweils die KandidatenId + dem Wert der Stimme für diesen Kandidaten + einem Punkt, um den Kandidaten 
	 * abzuschließen. Dies macht das Parsen einfacher vor allem, wenn beim Erkennen des QR Codes Fehler drin sein sollen.
	 * Der String wird am Ende auch mit einem Punkt terminiert.
	 * 
	 * Beispiel: +3971.6723. heißt gültiger Wahlzettel und der Kandidat 397 hat 1 Stimme und 672 hat 3 Stimmen.
	 * 
	 * @param wz Wahlzettel mit den Stimmen der übermittelt werden soll
	 * @param qrCodeSize Größe des QR Codes. Ist aber dennoch dynamisch
	 * @return BufferedImage mit dem QR Code.
	 * @throws Exception WriterException vom Encoding oder ein Lesefehler vom Wahlzetteldesign
	 */
	public static BufferedImage encodeWahlzettelToQR(Wahlzettel wz, int qrCodeSize) throws Exception{
		String text=wahlzettelToString(wz);
		return createQrCode(text, qrCodeSize);
		
	}
	

	/**
	 * Erzeugt aus dem übergebenen String zu parsen und daraus einen Wahlzettel zu erzeugen. Doppelte Kandidaten oder nicht
	 * vorhandene Kandidaten werden ignoriert, um möglichst viel erfassen zu können.
	 * 
	 * @param id Integer id des Wahlzettels
	 * @param rc der RegelChecker
	 * @param qrString der übergebene String mit den Daten die einen Wahlzettel ergeben sollen
	 * @return Wahlzettel
	 * 
	 * @throws Exception
	 */
	public static Wahlzettel decodeStringToWahlzettel(int id,RegelChecker rc,  String qrString)throws Exception {
		//System.out.println(qrString);
		BallotCardDesign wzd=BallotCardDesign.getInstance();
		int anzStimmenProKandidat=wzd.getDesignValue(DesignKeys.VOTESPROKANDIDAT);
		
		Wahlzettel wz=new Wahlzettel(id, rc);
		
		//Id des Wahlbezirks auswerten
		String idStr=qrString.substring(0, 5);
		qrString=qrString.substring(5);
		if (!wzd.getElection_id().equals(idStr)){
			throw new ElectionIdIsNotEqualException("<html>Der Wahlzettel wird zurückgewiesen!<br>Die Wahlbezirks Id dieses Stimmzettels ist "+idStr+" und entspricht nicht der eingetragenen Id "+wzd.getElection_id()+".</html>");
		}
		
		//Valid auslesen und setzen
		wz.setValid(qrString.substring(0, 1).equals("1"));
		
		//parteifeld folgt als nächstes als zweistellige Zahl
		int partyInt=Integer.parseInt(qrString.substring(1, 3));
		wz.setParty(partyInt);
		
		
		
		//Reststring
		String rest=qrString.substring(3);
		
		//Juri
		String crossed;
		//int pcrossed = rest.indexOf("_");
		int pcrossed = rest.indexOf(")");
		System.out.println("position:" + pcrossed);
		if(pcrossed == -1){
		    crossed = "";
		}else{
		    crossed = rest.substring(pcrossed+1);
		    rest = rest.substring(0, pcrossed);
		}
		System.out.println("crossed:" + crossed);
		System.out.println("barcode:" + rest);
		
		//Juri
		String mvoted = rest;
		rest = generateWZ(partyInt-1, rest, crossed);
		
		int candidateEncodeLength=4;
		int voteEncodeLength=1;
		
		while (rest.length() >0){
			String lesen=rest.substring(0, candidateEncodeLength+voteEncodeLength);
			rest=rest.substring(candidateEncodeLength+voteEncodeLength);
			//System.out.println("Interpret: "+lesen);
			
			String strStimmId= lesen.substring(0, candidateEncodeLength);
			Stimme st= new Stimme(Integer.valueOf(strStimmId));
			String strValue= lesen.substring(candidateEncodeLength ,candidateEncodeLength+voteEncodeLength);
			st.setVotes(Integer.valueOf(strValue));
			//System.out.println("Stimme:"+st.getId()+": "+st.getValue());
			try {
				wz.addStimme(st);
			} catch (DoubleCandidateIdException | CandidateNotKnownException e) {
				//Wird einfach ignoriert, kann später manuell nachgetragen werden.
				//System.out.println("Fehler");
			}
		}
		
		//Juri: adding the information to each vote, what has been cast manually
	
		while (mvoted.length() > 0){
			String lesen = mvoted.substring(0, 5);
			mvoted = mvoted.substring(5);
			
			String ID = lesen.substring(0,4);
			String V = lesen.substring(4);
			int idInt = Integer.parseInt(ID);
			int voteInt = Integer.parseInt(V);
			System.out.println("ID: " +ID + " V: "+ V );		
			Stimme st = wz.getStimmeByCandId(idInt);
			st.setmanualVotes(voteInt);
			System.out.println(st.getmanualVotes());
		}
		
//		int punktPos=rest.indexOf(".");
//		while (punktPos>0){
//			String lesen=rest.substring(0, punktPos);
//			rest=rest.substring(punktPos+1);
//			punktPos=rest.indexOf(".");
//			//System.out.println(lesen);
//			
//			//Daraus Stimme erzeugen:
//			//Die Id auslesen (ist alles bis auf die letzte Stelle):
//			String strStimmId= lesen.substring(0, lesen.length()-1);
//			Stimme st= new Stimme(Integer.valueOf(strStimmId));
//			String strValue= lesen.substring(lesen.length()-1);
//			//Als Bitmaske auslesen und setzen
//			int value=Integer.valueOf(strValue);
//			for (int i=0;i<anzStimmenProKandidat;i++){
//				int pos=anzStimmenProKandidat-i-1;
//				st.change(i,  (((value >> pos) & 1 ) == 1) );
//			}	
//			
//			try {
//				wz.addStimme(st);
//			} catch (DoubleCandidateIdException | CandidateNotKnownException e) {
//				//Wird einfach ignoriert, kann später manuell nachgetragen werden.
//			}
//			
//		}
		//System.out.println("Gesammt Stimmen: "+wz.getStimmen());
		return wz;
	}
	
	/**
	 * Versucht aus dem übergeben BufferedImage ein QR Code zu finden und übersetzt dieses in einen String.
	 * 
	 * @param qrcodeImage : BufferedImage 
	 * @return String mit dem Inhalt des QRCodes
	 * @throws Exception 
	 */
	private static String readQRCode(BufferedImage qrcodeImage) throws Exception{
		//Die Parameter anlegen
		Hashtable<DecodeHintType, Object> hintMap = new Hashtable<DecodeHintType, Object>();
        hintMap.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        
		//Bild zu BinaryBitmap verwandeln
		BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(qrcodeImage);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

		//QR Leser initialisieren...
		QRCodeReader reader = new QRCodeReader();
		Result result;
		//...und lesen:
		result = reader.decode(bitmap,hintMap);
		
		return result.getText();
	}
	
	/**
	 * Erzeugt aus einem übergebenen String einen QR Code mit der übergebenen Größe. Wobei diese sich dynamisch verhält
	 * was jedoch bereits aus der ZXing Bibliothek so kommt. Die Fehlerkorrektur beträgt 25%, dass heißt bis zu 
	 * 25% der Daten können wieder hergestellt werden, wenn sie nicht korrekt gelesen werden. Dies ist notwendig
	 * da Drucken und Scannen verlustbehaftet ist.
	 * 
	 * @param content
	 * @param qrCodeSize
	 * @return
	 * @throws WriterException
	 */
	private static BufferedImage createQrCode(String content, int qrCodeSize) throws WriterException        {
        
            // Create the ByteMatrix for the QR-Code that encodes the given String.
        	System.out.println("Länge: "+content.length());
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
 
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix byteMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize, hintMap);
 
            // Make the BufferedImage that are to hold the QRCode
            int matrixWidth = byteMatrix.getWidth();
 
            BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
            image.createGraphics();
 
            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, matrixWidth, matrixWidth);
 
            // Paint and save the image using the ByteMatrix
 
            graphics.setColor(Color.BLACK);
 
            for (int i = 0; i < matrixWidth; i++)
            {
                for (int j = 0; j < matrixWidth; j++)
                {
                    if (byteMatrix.get(i, j) == true)
                    {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }
            return image;
            //ImageIO.write(image, imageFormat, outputStream);
   
    }
	
	/**
	 * Berechnet aus dem Wahlzettel einen String, der alle benötigten Informationen besitzt, um den Stimmzettel zu
	 * kodieren. Aus Platzgründen wird dabei aber nicht die Position jeder einzelnen Stimme gespeichert, sondern nur
	 * die Anzahl an Kreuze pro Kandidat. Dies ist aber eine Sache, die die Wahlmaschine abfangen könnte.
	 * Jedoch ist es schwer so viele Daten mit einer großen Fehlerkorrektur in den QR Code zu bekommen.
	 * 
	 * Die Kodierung setzt als erstes Zeichen ein + oder - für einen gültigen oder ungültigen Wahlzettel. Darauffolgend 
	 * kommt jeweils die KandidatenId + dem Wert der Stimme für diesen Kandidaten + einem Punkt, um den Kandidaten 
	 * abzuschließen. Dies macht das Parsen einfacher vor allem, wenn beim Erkennen des QR Codes Fehler drin sein sollen.
	 * Der String wird am Ende auch mit einem Punkt terminiert.
	 * 
	 * Beispiel: +3971.6723. heißt gültiger Wahlzettel und der Kandidat 397 hat 1 Stimme und 672 hat 3 Stimmen.
	 * 
	 * @param wz Wahlzettel mit dem Wahlzettel der codiert werden soll.
	 * @return String mit der Kodierung
	 * @throws Exception Fehler beim Lesen der Wahlzetteldatei
	 */
	private static String wahlzettelToString(Wahlzettel wz) throws Exception {
		BallotCardDesign wzd=BallotCardDesign.getInstance();
		int anzVotesProKandidat=wzd.getDesignValue(DesignKeys.VOTESPROKANDIDAT);
		ArrayList<Stimme> st= wz.getStimmen();
    	java.util.Collections.sort( st );
		
    	String result="";
    	
    	//Erstmal validBit setzen:
//    	if (wz.isValid()){
//    		result="+";
//    	}else{
//    		result="-";
//    	}
    	
		//Stimmen in den String schreiben:
		for (int i = 0; i < st.size(); i++) {
			//Id in String umwandeln udn mit führenden Nullen versorgen
			String strNr = String.valueOf(st.get(i).getId());
			//BitShifting, um möglichst viel in wenig Platz zu speichern
			int value=0;
			for (int k=0;k<anzVotesProKandidat;k++){
				value= (value<<1);
				int wert= st.get(i).getValueAtPos(k);
				value=value+wert;
			}
			String strValue=""+value;
			//Nun alle Stimmen dranhängen, aber nur den Wert
			result=result+strNr+strValue+".";
		}
    	System.out.println(result);
    	
    	return result;
    }
	
	
	//Juri
	private static String generateWZ(int PartyID, String mselected, String crossed) throws Exception{
		String QR = new String();
		String ms = new String();
		
		String[] parties= BallotCardDesign.getInstance().getParties();
		ArrayList<Integer> partySelectedCandidates = new ArrayList<Integer>();
		
		if ((PartyID >=0 && PartyID<=parties.length)){
			
			partySelectedCandidates = getCandidatesSelectedParty(PartyID, crossed);
			
		}
		
		//this makes sure that the candidate list is not empty
		
		if((PartyID >=0 && PartyID<=parties.length)){
			int manuallyCasted;
			manuallyCasted = getManuallyCastedVotes(mselected);
		
			int [] votestodistribute = VotesToDistribute(partySelectedCandidates, mselected, manuallyCasted);
		
			QR = distributeVotes(partySelectedCandidates, votestodistribute);
	    }
		
		ms = getManuallySelectedNotfromParty(PartyID+1, mselected);
		
		System.out.println(QR+ms);
		return QR+ms;
	}
	
	
	private static String getManuallySelectedNotfromParty(int Pid, String listofMsel){
		String listCand = new String();
		
		while(listofMsel.length() > 0){
			
			String ID = listofMsel.substring(0, 2);
			int candidate_id = Integer.parseInt(ID);
			
			if(candidate_id != Pid){
				listCand += listofMsel.substring(0,5);
			}
			listofMsel = listofMsel.substring(5);
		}
		return listCand;
	}
	
	//This methods returns the number of manually cast votes, such that it is possible to calculate the no. of votes that need
	//to be automatically distributed
	private static int getManuallyCastedVotes(String mselected){
		int mcastedvotes = 0;
		
		while(mselected.length()>0){
		  //String lesen = mselected.substring(0, 5);
		  mcastedvotes += Integer.parseInt(mselected.substring(4,5));
		  mselected = mselected.substring(5);
		}
		return mcastedvotes;
	}
	
	
	//Method return the list of candidates of the selected party that might potentially get a vote by automatic distribution
	//this means that crossed out candidates of the party are not on this list anymore
	private static ArrayList<Integer> getCandidatesSelectedParty(int PartyID, String crossed) throws Exception{
		String[] parties= BallotCardDesign.getInstance().getParties();
		String pname = parties[PartyID];
		
		ArrayList<Integer> crossedList = new ArrayList<Integer>();
		ArrayList<Integer> partyCandidates = new ArrayList<Integer>();
		
		
		//This loop gets the ids of the crossed out candidates, and adds them to crossedList
		while (crossed.length() > 0){
			String lesen=crossed.substring(0, 4);
			
			String first = crossed.substring(0,1);
			int fst = Integer.parseInt(first);
			if(fst == 0){
				lesen = lesen.substring(1,4);
			}
			
			crossedList.add(Integer.parseInt(lesen));
			crossed=crossed.substring(4);
		}
		
		//This part gets all the candidates of the selected party. Furthermore it stores their IDs into the partyCandidates list.
		ArrayList<Integer> canList;
	    ArrayList<CandidateImportInterface> candidates = new ArrayList<CandidateImportInterface>();
			try {
		   canList = BallotCardDesign.getInstance().getCandidateIds();
		  for (int i : canList) {
			 if(BallotCardDesign.getInstance().getCandidate(i).getParty().equals(pname)){
			  candidates.add(BallotCardDesign.getInstance().getCandidate(i));
			  partyCandidates.add(BallotCardDesign.getInstance().getCandidate(i).getId());
			 } 
		  }
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		//This removes all crossed out candidates of the selected party, such that the returing list contains only those candidates
		//of the selected party that might potentially get a vote
		partyCandidates.removeAll(crossedList);

		return partyCandidates;
	}
	
	//This method delivers an array with the corresponding votes for the candidates of the selected party. In order that the
	//automatic distribution is done correctly, in the first part of this method the manual cast votes to the candidates of this
	//party are added.
	private static int [] VotesToDistribute(ArrayList<Integer> candidates, String mselected, int mcastedvotes)throws Exception{
		int VOTESPROKANDIDAT = 0;
		int MAXSTIMMEN = 0;
		int votesToDistribute = 0;
		int [] votes = new int[candidates.size()];
		String manselected = mselected;
		
		BallotCardDesign wzd;
		wzd = BallotCardDesign.getInstance();
		VOTESPROKANDIDAT = wzd.getDesignValue(DesignKeys.VOTESPROKANDIDAT);
		MAXSTIMMEN = wzd.getDesignValue(DesignKeys.MAXSTIMMEN);
		votesToDistribute = MAXSTIMMEN - mcastedvotes;
		
		
		//already adding the manual votes for those candidates that are in the selected party, such that the automatic distribution 
		//of votes for the party is correct.
		while(manselected.length()>0){
			String ID = manselected.substring(0, 4);
			String Vote = manselected.substring(4,5);
			int id = Integer.parseInt(ID);
			int vote = Integer.parseInt(Vote);
			
			for(int i = 0; i<candidates.size();i++){
				if(candidates.get(i).equals(id)){
					votes[i] = vote;
				}
			}
			manselected = manselected.substring(5); 
		}
		
		boolean getout = false;
		//adding the votes that should be automatic distributed to the candidates of the party
		while(votesToDistribute > 0 && !getout){
		 for(int i=0; i<candidates.size();i++){
			 if(votesToDistribute > 0 && votes[i] < VOTESPROKANDIDAT){
				votes[i]++;
				votesToDistribute--;
			}
		  }
		  
		 //This make sure that in case all candidates of the party have already three votes, and still votes are left to be 
		 //distributed it should get out of the loop and ignore those votes
		 for(int j=0;j < votes.length;j++){
		    if(votes[j] < VOTESPROKANDIDAT){
		    	   getout = false;
		    }else{
		    	getout = true;
		    }      
		 }
		 
		 
		
		}
		
		//System.out.println("" + VOTESPROKANDIDAT);
		return votes;
	}
	
	//This method concatenates each candidate with his votes and returns the required string back, which is expected to be read
	// by scanning the QR-Code.
	private static String distributeVotes(ArrayList<Integer> candidates, int [] votes){
		String qrCode = new String();
				
		for(int i=0; i<candidates.size();i++){
			String id = candidates.get(i).toString();
			String vString = ""+votes[i];
			if(id.length() < 4){
				id = "0"+id;
			}
		    
			//Only those candidates of the party that have got at least one vote.
			if(votes[i] != 0){
			qrCode += id+vString;
			}
		}
		
		return qrCode;
	}
	
	
//	public static void main(String [] args){
//		RegelChecker rc = new RegelChecker();
//		String qr = "543211040801309012";
//		int i = 1;		
		
//		try {
//			decodeStringToWahlzettel(i, rc, qr);
//			generateWZ(7,"0801309012","");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//		e.printStackTrace();
//		}
//	}	

}