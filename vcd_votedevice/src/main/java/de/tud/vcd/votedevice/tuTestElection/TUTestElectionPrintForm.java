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
package de.tud.vcd.votedevice.tuTestElection;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Hashtable;

import javax.swing.ImageIcon;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import de.tud.vcd.common.BallotCardDesign;
import de.tud.vcd.common.BallotCardDesign.DesignKeys;
import de.tud.vcd.votedevice.model.IBallotCardImageCreator;
import de.tud.vcd.votedevice.tuTestElection.TUTestElectionModel.VoteState;

public class TUTestElectionPrintForm implements IBallotCardImageCreator{
	Dimension resolution;
	TUTestElectionModel bc;
	
	public enum Align
	{
	  RIGHT, LEFT
	}
	
	//globale Parameter!!!!!! Diese bestimmen schließlich auch den Scanerfolg im Votingmodul
	//int[][] c;
	//----------------------
	//int starthoehe=50;
	//int infoboxstarthoehe=20;
	//int abstandRand=10;
    //int vspace=1;//Abstand zwischen Zeilen
    //int hspace=1;//Abstand zwischen Feldern
    //int lineHeight=5;//Höhe der Zeilen
   // int nameWidth=45;//Breite des Namensfeld
    //int nrWidth=20;//Breite der Kandidatennummer
    //int nrMaxProSpalte=40;//maximale Kandidaten pro Spalte
   // int maxVotesProKandidat=3;
    int seitenhoehe=297;
    int seitenbreite=210;
//    int qr_x;
//    int qr_y;
//    int qr_w;
//    int qr_h;
    int qrcodedrucken=1;
   
    String electionName="Local Election";
    String electionId="0.0.0";
    //------------------
    int erlaubteStimmen=71;
    
    BallotCardDesign wzd;
    //-------------------------
	private int abstandOben;
	private int abstandLinks;
	private String txtTitle;
	private String txtSubTitle;
	private int infoBoxHoehe;
	private int parteiFeldHoehe;
	private int subUeberschriftGroesse;
	private int ueberschriftGroesse;
	
	 
	public TUTestElectionPrintForm(int w, int h, TUTestElectionModel bc) throws Exception {
		resolution =new Dimension(w,h);
		this.bc=bc;
		//Konstruktionsdirektiven (GENERICS)
		//-----------------------------------
		
		//-----------------------------------
		
		
		//Daten aus XML laden;
		InputStream filename=getClass().getClassLoader().getResource("wahlzettelCreator.xml").openStream();
		wzd= BallotCardDesign.getInstance(filename);
		electionName=wzd.getElection_name();
		electionId=wzd.getElection_id();
		
	
		
		qrcodedrucken=wzd.getDesignValue(DesignKeys.PRINT_QR_CODE);
		electionName=wzd.getElection_name();
		electionId=wzd.getElection_id();

		seitenhoehe=wzd.getDesignValue(DesignKeys.PAGEHEIGHT);
	    seitenbreite=wzd.getDesignValue(DesignKeys.PAGEWIDTH);
		
		abstandOben=wzd.getDesignValue(DesignKeys.MARGIN_TOP);//10
		abstandLinks=wzd.getDesignValue(DesignKeys.MARGIN_LEFT);//10
		infoBoxHoehe=wzd.getDesignValue(DesignKeys.HEIGHT_INFOBOX);//20
		parteiFeldHoehe=infoBoxHoehe;
		
		
		subUeberschriftGroesse=wzd.getDesignValue(DesignKeys.FONTSIZE_HEADLINE_SUB);//4;
		ueberschriftGroesse=wzd.getDesignValue(DesignKeys.FONTSIZE_HEADLINE);//8;
		
		txtTitle="Elektronische Abstimmung (Prototyp)";
		txtSubTitle="";
		
		
		
	}
	
	
	

//	private int countVotes(){
//		
//		return bc.countCandidatesVotes();
//	}

	
	void drawStimme(Graphics2D g2d,int x, int y, int w, VoteState vs){
		g2d.drawRect(x, y, w, getYMeasures(25));
		String str= bc.getVoteStateText(vs);
		g2d.setFont(new Font("SansSerif", Font.PLAIN,getYMeasures(8)));
		g2d.drawString(str, x+getYMeasures(5), y+getYMeasures(15));
		
		
		g2d.drawOval(x+w-getYMeasures(20), y+getYMeasures(5), getYMeasures(15), getYMeasures(15));
		if (bc.containsVoteState(vs)){
			Stroke old= g2d.getStroke();
			Stroke stroke = new BasicStroke( 3 );
			g2d.setStroke( stroke );

			g2d.drawLine(x+w-getYMeasures(20), y+getYMeasures(5),x+w-getYMeasures(20)+ getYMeasures(15),  y+getYMeasures(5+15));
			g2d.drawLine(x+w-getYMeasures(20),y+getYMeasures(5)+getYMeasures(15),x+w-getYMeasures(20)+ getYMeasures(15),  y+getYMeasures(5) );
			g2d.setStroke(old);
		}
	}
	
	public ImageIcon createImage(Color bgcolor) throws Exception { 

        //Bild erzeugen und 2D Oberfläche für besseres Rendering erstellen
        BufferedImage bufferedImage = new BufferedImage(resolution.width, resolution.height, BufferedImage.TYPE_INT_RGB); 
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        //Hintergrund einfärben
        g2d.setColor(bgcolor);
        g2d.fillRect(0,0, resolution.width, resolution.height);
        
        //Remove next line only debug;
       // debugDrawHelpLines(g2d);
    	
        
        //Schriftfarbe setzen:
        g2d.setColor(Color.BLACK);
        
        //Überschrift setzen:
       
        
        g2d.setFont(new Font("SansSerif", Font.BOLD,getYMeasures(ueberschriftGroesse)));
    	FontMetrics fm = g2d.getFontMetrics(g2d.getFont());
		int height=fm.getAscent()-fm.getDescent();
    	int startX=getXMeasures(0+abstandLinks);
    	int startY=getYMeasures(0+abstandOben)+height;
    	g2d.drawString(txtTitle,startX, startY);
    	
    	 //Subüberschrift setzen:
        
        
        g2d.setFont(new Font("SansSerif", Font.PLAIN,getYMeasures(subUeberschriftGroesse)));
    	fm = g2d.getFontMetrics(g2d.getFont());
		height=fm.getAscent()-fm.getDescent();
    	startX=getXMeasures(0+abstandLinks);
    	startY=startY+height+fm.getDescent();
    	g2d.drawString(txtSubTitle,startX, startY);
    	
    	/*
    	 * Sind Sie grundsätzlich zufrieden mit Ihrem BA/MA/Promotions-Betreuer bzw. Vorgesetzten?

    A: immer zufrieden
    B: meistens zufrieden
    C: mal zufrieden mal nicht
    D: selten zufrieden
    E: nie zufrieden
    	 */
    	//Frage schreiben:
    	int x=startX+getXMeasures(10);
    	int y=startY+getYMeasures(parteiFeldHoehe-10+5);
    	 g2d.setFont(new Font("SansSerif", Font.PLAIN,getYMeasures(6)));
     	
    	 
    	 
    	 int zeilenHoehe=getYMeasures(6);
    	 int einzug=getXMeasures(10);
    	 writeText(g2d, x, y, "Sind Sie grundsätzlich mit Ihrem...");
    	y=y+zeilenHoehe;
    	writeText(g2d, x+einzug, y, "O-Phasen-Tutor (neue Studenten)");
    	y=y+zeilenHoehe;
    	writeText(g2d, x+einzug, y, "Mentor (nur Inf. Studenten)");
    	y=y+zeilenHoehe;
    	writeText(g2d, x+einzug, y, "BA-Betreuer (Studenten)");
    	y=y+zeilenHoehe;
    	writeText(g2d, x+einzug, y, "MA-Betreuer (Studenten)");
    	y=y+zeilenHoehe;
    	writeText(g2d, x+einzug, y, "PhD-Betreuer (Doktoranden)");
    	y=y+zeilenHoehe;
    	writeText(g2d, x+einzug, y, "Vorgesetzten (Mitarbeiter)");
    	y=y+zeilenHoehe;
    	writeText(g2d, x+einzug, y, "Präsidenten (Professoren)");
    	y=y+zeilenHoehe;
    	writeText(g2d, x, y, "...zufrieden?");
    	y=y+2*zeilenHoehe;
    	
//    	y=startY+getYMeasures(parteiFeldHoehe+5);
//    	writeTextCENTER(g2d, x, y, "Ihrem Betreuer bzw. Vorgesetzten?" );
    	
    	//Gültigkeit der STimme ausgeben:
    	//y=getYMeasures(60);
    	x=(getXMeasures(seitenbreite))/2;
    	g2d.setFont(new Font("SansSerif", Font.BOLD,getYMeasures(8)));
     	String v="";
    	if (bc.isValid()){
     		v="Ihre Stimme ist gültig.";
     	}else{
     		v="Ihre Stimme wird als ungültig gewertet.";
     	}
    	writeTextCENTER(g2d, x, y, v);
    	
    	y=y+2*zeilenHoehe;
        //Nun die ganzen optionen Ausgeben
    	//y=getYMeasures(70);
    	int w= getXMeasures(seitenbreite)/3*2;
    	x=(getXMeasures(seitenbreite)-w)/2;
    		
    	for (VoteState vs : bc.getAllVoteStates()){
    		drawStimme(g2d,x, y,w, vs);
    		y+=getYMeasures(25);
    	}
    	
    	
    	//Jetzt den QR Code drucken
    	int qrcodesize=getYMeasures(30);
    	String p="-";
    	for (VoteState vvs:bc.getVoteStates()){
    		p=p+vvs.toString()+"-";
    	}
    	
    	String qrtext=bc.isValid()+""+p+"WahlID:01782"+"Umfrage zur Zufriedenheit mit Betreuer 17-20.6.2013";
    	BufferedImage bimg = createQrCode(qrtext, qrcodesize);
    	g2d.drawImage(bimg,(getXMeasures(seitenbreite)-qrcodesize)/2,y+getYMeasures(10), null);
    	
    	
//    	
        //Bild nun ausgeben:
        ImageIcon icon = new ImageIcon(bufferedImage);
		return icon;
	}
	
	
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
	
//	private void drawGestricheneStimmen(Graphics2D g2d, ArrayList<Candidate> crossed_cl){
//		//Nur malen, wenn auch wirklich benötigt:
//		if (!(crossed_cl!=null && crossed_cl.size()>0)){
//			return;
//		}
//		//Kasten malen
//		int x= getXMeasures(seitenbreite-spalteGestrichene)/2;
//		int y= getYMeasures(beginnStimmen);
//		int width=getXMeasures(spalteGestrichene);
//		int height=getYMeasures(seitenhoehe-beginnStimmen-abstandUnten);
//		
//		
//		//Überschrift einfügen
//		String text1="In der gewählten Liste";
//		String text2="gestrichene Kandidaten";
//		
//		
//		
//		
//		
//		int marginRandPixelX=getXMeasures(marginRandGestrichene);
//		int marginRandPixelY=getYMeasures(marginRandGestrichene);
//		schriftGestricheneUeberschriftVorschlag=getYMeasures(schriftGestricheneUeberschriftVorschlag);
//		schriftGestricheneVorschlag=getYMeasures(schriftGestricheneVorschlag);
//		schriftGestricheneMin=getYMeasures(schriftGestricheneMin);
//		
//		//die zur verfügung stehende Breite ausnutzen bis zur angegebenen Maximalschriftgröße
//		Font plainFont;;
//		FontMetrics fm;
//		int platzText=0;
//		do{
//			plainFont=new Font("SansSerif", Font.PLAIN,schriftGestricheneUeberschriftVorschlag);
//			fm = g2d.getFontMetrics(plainFont);
//			platzText=Math.max(fm.stringWidth(text1),fm.stringWidth(text2));
//			schriftGestricheneUeberschriftVorschlag--;
//		}while(platzText>(width-marginRandPixelX) && platzText>0);
//		
//		g2d.setFont(plainFont);
//		g2d.drawString(text1, x+marginRandPixelX, y+fm.getAscent()-fm.getDescent()+marginRandPixelY);
//		g2d.drawString(text2, x+marginRandPixelX, y+2*fm.getAscent()-fm.getDescent()+marginRandPixelY);
//		int yLine=y+2*fm.getHeight()+marginRandPixelY-2*fm.getDescent();
//		g2d.drawLine(x, yLine, x+width, yLine);
//		
//		//Zur Verfügung stehenden Platz berechnen und anfangen die Liste auszugeben:
//		int platz=height-yLine+y;
//		double zeilenhoehe=(double)platz/maxKandidaten;
//		//System.out.println("Z: "+zeilenhoehe);
//		
//		
//		
//		//Sicherstellen, dass die Schriftart nicht größer wird als der Vorschlag, um Riesenbuchstaben zu vermeiden bei wenigen Kandidaten
//		if(zeilenhoehe>schriftGestricheneVorschlag){
//			zeilenhoehe=schriftGestricheneVorschlag;
//		}
//		
//		//Wenn Zeilenhöhe kleiner ist als das Minimum, dann umschalten auf zwei spalten und nur Id ausgeben:
//		int hoeheGestrichene=0;
//		if (zeilenhoehe>=schriftGestricheneMin){
//			//einspaltig ausgeben:
//			plainFont=new Font("SansSerif", Font.PLAIN,(int)zeilenhoehe);
//			g2d.setFont(plainFont);
//			fm = g2d.getFontMetrics(plainFont);
//			
//			int position=0;
//			for (Candidate c : crossed_cl){
//				String cText=c.getId()+" "+c.getName()+", "+c.getPrename();
//				//Prüfen, ob Text reinpasst, ansonsten Vorname abkürzen:
//				int txtWidth=fm.stringWidth(cText);
//				if (txtWidth>width-2*marginRandPixelX){
//					cText=c.getId()+" "+c.getName()+", "+c.getPrename().substring(0,1)+".";
//				}
//				//Wenn es immer noch nicht passen solte, dann nachnamen auch noch beschneiden solange bis es passt
//				String name=c.getName();
//				while(fm.stringWidth(cText)>width-2*marginRandPixelX){
//					name=name.substring(0, name.length()-1);
//					cText=c.getId()+" "+name.substring(0, name.length()-1)+"., "+c.getPrename().substring(0,1)+".";
//				}
//				
//				//Nun den Namen auch noch ausgeben:
//				g2d.drawString(cText, x+marginRandPixelX, Math.round(yLine+fm.getAscent()+position*zeilenhoehe));
//				hoeheGestrichene=(int)Math.round(yLine+fm.getAscent()+position*zeilenhoehe+fm.getDescent());
//				position++;
//			}
//		}else{
//			//zweispaltig ausgeben (gucken, ob mit der vorgeschlagenen Größe gearbeitet werden kann:
//			if (zeilenhoehe*2 > schriftGestricheneUeberschriftVorschlag){
//				zeilenhoehe=schriftGestricheneVorschlag;
//			}else{
//				zeilenhoehe=2*zeilenhoehe;
//			}
//			
//			plainFont=new Font("SansSerif", Font.PLAIN,(int)zeilenhoehe);
//			fm = g2d.getFontMetrics(plainFont);
//			g2d.setFont(plainFont);
//			
//			int position=0;
//			int spalte=0;
//			int idsProSpalte=(int)(platz/zeilenhoehe);
//			for (Candidate c : crossed_cl){
//				if (position>=idsProSpalte){
//					spalte=width/2;
//				}
//				String cText=c.getId()+"";
//				g2d.drawString(cText, x+marginRandPixelX+spalte, Math.round(yLine+fm.getAscent()+(position % idsProSpalte)*zeilenhoehe));
//				hoeheGestrichene=(int)Math.round(yLine+fm.getAscent()+(position % idsProSpalte)*zeilenhoehe+fm.getDescent());
//				position++;
//				
//				
//			}
//			
//		}
//		g2d.drawRect(x, y, width, hoeheGestrichene-y);
//		
//	}
	
	
//	private void drawParteibox(Graphics2D g2d, int x, int y, int w, int h) {
//		//g2d.drawString("parteibx", x+5, y+30);
//		int kreuzMarginX=getXMeasures(2);
//		int kreuzMarginY=getYMeasures(2);
//		//erstmal ungültige abhandeln:
//		if (!bc.isValid()){
//			g2d.setFont(new Font("SansSerif", Font.BOLD,18));
//			g2d.drawString("Ungültig", x+5, y+30);
//			
//			
////			ArrayList<RejectReasons> rr= bc.getFails();
////			if (rr.contains(RejectReasons.MANUALLY_INVALID){
////				
////			}
//			
//			
//		}else{
//			//Stimmzettel ist zumindest zeilweise gültig:
//			EnumSet<Validity> set = EnumSet.of(Validity.VALID_ONLY_PARTY, Validity.VALID_PARTY_AND_CANDIDATE);
//			Validity validity=bc.getValidity();
//			if (set.contains(validity  )){
//				//eine Parteistimme ist vorhanden
//				int kreuzGroesseX=getXMeasures(8);
//				int kreuzGroesseY=getYMeasures(8);
//				
//				
//				Stroke stroke=g2d.getStroke();
//				BasicStroke stroke1
//		        = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
//		                          BasicStroke.JOIN_MITER);
//				g2d.setStroke(stroke1);
//				g2d.drawOval(x+kreuzMarginX, y+kreuzMarginY, kreuzGroesseX, kreuzGroesseY);
//				g2d.drawLine(x+kreuzMarginX, y+kreuzMarginY, x+kreuzMarginX+kreuzGroesseX, y+kreuzMarginY+kreuzGroesseY);
//				g2d.drawLine(x+kreuzMarginX, y+kreuzMarginY+kreuzGroesseY, x+kreuzMarginX+kreuzGroesseX, y+kreuzMarginY);
//				
//				g2d.setStroke(stroke);
//				String partyName="";
//				for(Party p: bc.getPartyList()){
//					if (p.isVoted())partyName=p.getName();
//				}
//				g2d.setFont(new Font("SansSerif", Font.PLAIN,getYMeasures(6)));
//				g2d.drawString(partyName, x+2*kreuzMarginX+kreuzGroesseX, y+kreuzMarginY+g2d.getFontMetrics(g2d.getFont()).getAscent());
//				
//				//in der Parteistimme sind gestrichene Kandidaten oder
//				//in der Parteistimme sind zusätzliche Kreuze:
//				
//				if (validity==Validity.VALID_PARTY_AND_CANDIDATE){
//					g2d.setFont(new Font("SansSerif", Font.PLAIN,getYMeasures(4)));
//					//g2d.drawString("Liste wurde verändert.", x+kreuzMarginX, y+kreuzMarginY+kreuzGroesseY+g2d.getFontMetrics(g2d.getFont()).getAscent());
//				}else{
//					g2d.setFont(new Font("SansSerif", Font.PLAIN,getYMeasures(4)));
//					g2d.drawString("Das Listenkreuz entspricht", x, y+kreuzMarginY+kreuzGroesseY+g2d.getFontMetrics(g2d.getFont()).getAscent());
//					g2d.drawString("der untenstehenden Darstellung.", x, y+kreuzMarginY+kreuzGroesseY+g2d.getFontMetrics(g2d.getFont()).getAscent()+g2d.getFontMetrics(g2d.getFont()).getHeight());
//				}
//				
//			}else if(validity==Validity.VALID_REDUCE_PARTIES){
//				 //Mehrfach vergebene Kopfstimmen werden ignoriert, da noch zusätzliche Stimmen da sind
//				g2d.setFont(new Font("SansSerif", Font.BOLD,getYMeasures(4)));
//				FontMetrics fm = g2d.getFontMetrics(g2d.getFont());
//				int hoehe=fm.getAscent();
//				g2d.drawString("Hinweis:", x+kreuzMarginX, y+kreuzMarginY+hoehe);
//				hoehe+=fm.getHeight();
//				
//				g2d.setFont(new Font("SansSerif", Font.PLAIN,getYMeasures(4)));
//				fm = g2d.getFontMetrics(g2d.getFont());
//				g2d.drawString("Mehrfach vergebene Kopf-", x+kreuzMarginX, y+kreuzMarginY+hoehe);
//				hoehe+=fm.getHeight();
//				g2d.drawString("stimmen werden ignoriert.", x+kreuzMarginX, y+kreuzMarginY+hoehe);
//			}else if(validity==Validity.VALID_NO_PARTY){
//				g2d.setFont(new Font("SansSerif", Font.PLAIN,getYMeasures(4)));
//				g2d.drawString("keine Liste gewählt", x+kreuzMarginX, y+kreuzMarginY+g2d.getFontMetrics(g2d.getFont()).getAscent());
//			}
//		
//			
//		}
//		
//		
//	}
	
	
	
	
	
	
	
	
	
	
	
	
	

	
    private void writeTextCENTER(Graphics2D g2d,int x, int y, String text){
    	//g2d.setFont(new Font("SansSerif", Font.PLAIN,12));
    	
    	FontMetrics fm = g2d.getFontMetrics(g2d.getFont());
		int width=fm.stringWidth(text);
    	int startX=x-(width)/2;
    	
    	g2d.drawString(text,startX, y);
    }
    private void writeText(Graphics2D g2d,int x, int y, String text){
    	//g2d.setFont(new Font("SansSerif", Font.PLAIN,12));
    	
    	//FontMetrics fm = g2d.getFontMetrics(g2d.getFont());
		//int width=fm.stringWidth(text);
    	int startX=x;//-(width)/2;
    	
    	g2d.drawString(text,startX, y);
    }
//    	
//    	
//    	
////    	BaseFont bf = BaseFont.createFont();
////    	cb.beginText();
////        cb.setFontAndSize(bf, 12);
////    	cb.showTextAligned(PdfContentByte.ALIGN_CENTER, text, getXMeasures(x), getYMeasures(y), 0);
////    	cb.endText();
//    }
//    
//    private void writeTextCENTER_HL(Graphics2D g2d,int x, int y, String text) {
//    	g2d.setFont(new Font("SansSerif", Font.BOLD,14));
//    	
//    	FontMetrics fm = g2d.getFontMetrics(g2d.getFont());
//		int width=fm.stringWidth(text);
//    	int startX=getXMeasures(x)-(width)/2;
//    	
//    	g2d.drawString(text,startX, getYMeasures(y));
//    	
////    	BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
////    	cb.beginText();
////        cb.setFontAndSize(bf, 14);
////    	cb.showTextAligned(PdfContentByte.ALIGN_CENTER, text, getXMeasures(x), getYMeasures(y), 0);
////    	cb.endText();
//    }
    
    
//    private void printMeasures(){ 
//        System.out.println("A4-Ma\u00DFe: " + PageSize.A4.getWidth() + "pt x " 
//                + PageSize.A4.getHeight() + "pt - " 
//                + (PageSize.A4.getWidth() * 0.3527) + "mm x " 
//                + (PageSize.A4.getHeight() * 0.3527) + "mm"); 
//    } 
//    
   
    
    private int getXMeasures(int x){
    	return (int)((x*resolution.width)/210);
    }
    
    private int getYMeasures(int y){
    	return (int)((y*resolution.height)/297);
    }
    
   
}
