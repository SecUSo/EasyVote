package de.tud.vcd.votedevice.municipalElection.model;

/**
 * 
 */

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import de.tud.vcd.common.BallotCardDesign;

/**
 * Kümmert sich um das Erzeugen und Lesen von QR Codes. Durch diese Klassen entsteht zwar eine höhere 
 * Abhängigkeit der Klassen untereinander, aber sind vom Aufgabenschwerpunkt besser getrennt.
 * Die ganze Klasse ist statisch, da in ihr kein Wissen liegt, sondern nur Methoden zur Verfügung stellt
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class VotingQRCodeNew {

	/**
	 * 
	 */
	public VotingQRCodeNew() {
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
	public static BufferedImage encodeWahlzettelToQR(BallotCard wz, int qrCodeSize) throws Exception{
		String text=wahlzettelToString(wz);
		return createQrCode(text, qrCodeSize);
		
	}
	
	//Juri: To get access to the privat information returned by the wahlzettelToString method
		public String encodeWahlzettelToString(BallotCard wz) throws Exception{
			String string = wahlzettelToString(wz);
			return string;
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
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
 
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
	private static String wahlzettelToString(BallotCard bc) throws Exception {
		//InputStream filename=Starter.class.getClassLoader().getResource("wahlzettelCreator.xml").openStream();
		//BallotCardDesign wzd= BallotCardDesign.getInstance(filename);
		//int maxVotesProKandidat=wzd.getDesignValue(DesignKeys.VOTESPROKANDIDAT);
		
		ArrayList<Candidate> votedCandidates= new ArrayList<Candidate>();
    	
		//Juri: This list should be filled in with the crossed out Candidates
		ArrayList<Candidate> crossedCandidates = new ArrayList<Candidate>();
		
		ArrayList<Party> parties=new ArrayList<Party>();
		
    	ArrayList<Party> pl=bc.getPartyList();
    	for (Party p: pl){
    		if (p.isVoted()){
    			parties.add(p);
    		}
    		ArrayList<Candidate> cl=bc.getParty(p.getName()).getCandidates();
    		for(Candidate c:cl){
    			if (c.getCountedVotes()>0){
    				votedCandidates.add(c);
    			}
    			
    			//Juri: This if condition makes sure that crossed out candidates, of selected party are included in the list
    			if(p.isVoted() && c.isCrossedOut()){
    				crossedCandidates.add(c);
    			}
    		}
    	}
    	
    	
    	
//        int anzCand=votedCandidates.size();
//        int anzVotes=maxVotesProKandidat;
		
		
    	java.util.Collections.sort( votedCandidates );
		
    	String result="";
    	//Die Id als 5stelligen STring aus Zahlen voranstellen (gucken, dass es wirklich nur zahlen sind damit QR nicht überlastet wird):
    	String isStr=BallotCardDesign.getInstance().getElection_id();
    	int idInt;
    	try{
    		idInt= Integer.parseInt(isStr);
    		
    	}catch(NumberFormatException e){
    		idInt=0;
    	}
    	//aus Id 5 stelligen String machen
    	String idString=""+idInt;
    	if (idString.length()>5){
			idString=idString.substring(0, 5);
		}
    
    	int laenge=idString.length();
    	for (int i=0;i<(5-laenge);i++){
    		idString="0"+idString;
    	}
    	result+=idString;
    	//Erstmal validBit setzen:
    	if (bc.isValid()){
    		result+="1";
    	}else{
    		result+="0";
    	}
    	
    	//Partei einfügen falls nur eine, sonst 00;
    	if (parties.size()==1){
    		int pid=parties.get(0).getId();
    		String pidStr=pid+"";
    		if (pidStr.length()==1){
    			pidStr="0"+pidStr;
    		}
    		result+=pidStr;
    	}else{
    		result+="00";
    	}
    	
		//Stimmen in den String schreiben:
		for (int i = 0; i < votedCandidates.size(); i++) {
			
		 //Juri: This if condition make sure that only manually slected candidates and their votes are encoded into the QR-Code
		 if(votedCandidates.get(i).getVotes() != 0 && votedCandidates.get(i).getVotesToPrint() > 0){	
			//Id in String umwandeln udn mit führenden Nullen versorgen
			
			String strNr = String.valueOf(votedCandidates.get(i).getId());
			 
			while(strNr.length()<4){
				strNr="0"+strNr;
			};
			
			
			//int votes= votedCandidates.get(i).getVotesToPrint(); This line was the original one. The one below gets the manually cast votes
			int votes = votedCandidates.get(i).getVotesToPrint(); //getVotes();
			String strValue=""+votes;
			
			//Nun alle Stimmen dranhängen, aber nur den Wert
			result=result+strNr+strValue;
		 }
		}
		
		//Juri: This inserst the sign into the string, which allows to differentiate between the selected and crossed out candidates
		result += "_";
		
		//Juri: This loop inserst the crossed out candidates into the QR-Code.
		for(int i=0; i<crossedCandidates.size();i++){
			//Id in String umwandeln udn mit führenden Nullen versorgen
			String strNr = String.valueOf(crossedCandidates.get(i).getId());
		 
			while(strNr.length()<4){
				strNr="0"+strNr;
			};
			result = result+strNr;
		}
		//Juri
		result = result + bc.getError();
    	System.out.println(result);
		return result;
		//return "54321100010720108201092011020116201172011820119201202012120129201492015330157301583015920161201691021520216202173021830249302503026120267302683026931011310133_";
    	//return "5432110101083_0169";
    	//return "5432110303011_0347";
    	//return "54321101010220112201202012920133201402_0115";
    //return "54321102020110203102522_";	
	}
	


}
