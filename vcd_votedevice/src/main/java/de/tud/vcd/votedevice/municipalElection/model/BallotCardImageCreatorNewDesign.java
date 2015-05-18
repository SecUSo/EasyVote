package de.tud.vcd.votedevice.municipalElection.model;

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
import java.util.ArrayList;
import java.util.EnumSet;

import javax.swing.ImageIcon;

import de.tud.vcd.common.BallotCardDesign;
import de.tud.vcd.common.BallotCardDesign.DesignKeys;
import de.tud.vcd.votedevice.model.IBallotCardImageCreator;
import de.tud.vcd.votedevice.municipalElection.model.BallotCard.Validity;

/**
 * 
 * Erstellt einen Stimmzettel nach dem neuen Design. Die Wahldaten werden dabei direkt aus dem Modell ausgelesen.
 * Das Aussehen kann �ber die Konfigurationsdatei (wahlzettel.xml) beeinflusst werden.
 * 
 * @author Roman J�ris <roman.joeris@googlemail.com>
 *
 */
public class BallotCardImageCreatorNewDesign implements IBallotCardImageCreator{
	Dimension resolution;
	BallotCard bc;
	
	public enum Align
	{
	  RIGHT, LEFT
	}
	
	
    int seitenhoehe=297;
    int seitenbreite=210;
    int qrcodedrucken=1;
   
    String electionName="Local Election";
    String electionId="0.0.0";
    //------------------
    int erlaubteStimmen=71;
    
    BallotCardDesign wzd;
    //-------------------------
	private int abstandOben;
	private int abstandUnten;
	private int abstandLinks;
	private int maxParteien;
	private int abstandRechts;
	private int maxKandidaten;
	private int parteiFeldBreite;
	private String txtTitle;
	private String txtSubTitle;
	private int infoBoxBreite;
	private int infoBoxHoehe;
	private int parteiFeldHoehe;
	private int qrCodeBreite;
	private int qrCodeHoehe;
	private int beginnStimmen;
	private int spalteKandidatBreite;
	private int spalteGestrichene;
	private int schriftGestricheneVorschlag;
	private int schriftGestricheneMin;
	private int schriftGestricheneUeberschriftVorschlag;
	private int marginRandGestrichene;
	private int subUeberschriftGroesse;
	private int ueberschriftGroesse;
	
	//Juri: This enables to put text over the qr-code, to remind voters what the qr-code is used for
	private String qrCodeTitle;
	private String qrCodeSubTitle1;
	private String qrCodeSubTitle2;
	
	ArrayList<Candidate> crossedCandidates;
    
	public BallotCardImageCreatorNewDesign(int w, int h, BallotCard bc) throws Exception {
		resolution =new Dimension(w,h);
		this.bc=bc;
		
		//Daten aus XML laden;
		InputStream filename=getClass().getClassLoader().getResource("wahlzettelCreator.xml").openStream();
		wzd= BallotCardDesign.getInstance(filename);

		qrcodedrucken=wzd.getDesignValue(DesignKeys.PRINT_QR_CODE);
		electionName=wzd.getElection_name();
		electionId=wzd.getElection_id();

		seitenhoehe=wzd.getDesignValue(DesignKeys.PAGEHEIGHT);
	    seitenbreite=wzd.getDesignValue(DesignKeys.PAGEWIDTH);
		
		abstandOben=wzd.getDesignValue(DesignKeys.MARGIN_TOP);//10
		abstandUnten=wzd.getDesignValue(DesignKeys.MARGIN_BOTTOM);//10
		abstandLinks=wzd.getDesignValue(DesignKeys.MARGIN_LEFT);//10
		abstandRechts=wzd.getDesignValue(DesignKeys.MARGIN_RIGHT);//10
		
		parteiFeldBreite=wzd.getDesignValue(DesignKeys.WIDTH_PARTYBOX);//57
		beginnStimmen=wzd.getDesignValue(DesignKeys.BEGIN_VOTES_COLUMN);//
		infoBoxHoehe=wzd.getDesignValue(DesignKeys.HEIGHT_INFOBOX);//20
		parteiFeldHoehe=infoBoxHoehe;
		qrCodeBreite=infoBoxHoehe;
		qrCodeHoehe=infoBoxHoehe;
		infoBoxBreite=seitenbreite-abstandLinks-abstandRechts-parteiFeldBreite-qrCodeBreite-2;
		
		spalteKandidatBreite=wzd.getDesignValue(DesignKeys.COLUMN_WIDTH);//70
		spalteGestrichene=wzd.getDesignValue(DesignKeys.WIDTH_CROSSED_CANDIDATES);//45
		//kreuzHoehe=0.9;
		
		
		maxParteien=wzd.getDesignValue(DesignKeys.MAXPARTIES);//13;
		maxKandidaten=wzd.getDesignValue(DesignKeys.MAXSTIMMEN);//71;
		//F�r den mittleren Bereich der gestrichenen Kandidtaen
		schriftGestricheneVorschlag=wzd.getDesignValue(DesignKeys.FONTSIZE_CROSSED);//4;
		schriftGestricheneMin=wzd.getDesignValue(DesignKeys.FONTSIZE_CROSSED_MIN);//2;
		schriftGestricheneUeberschriftVorschlag=wzd.getDesignValue(DesignKeys.FONTSIZE_CROSSED_HEADLINE);//4;
		marginRandGestrichene=wzd.getDesignValue(DesignKeys.MARGIN_CROSSED);//1;
		
		subUeberschriftGroesse=wzd.getDesignValue(DesignKeys.FONTSIZE_HEADLINE_SUB);//4;
		ueberschriftGroesse=wzd.getDesignValue(DesignKeys.FONTSIZE_HEADLINE);//8;
		
		txtTitle="Stimmzettel";
		//txtSubTitle="f�r die Wahl zur Stadtverordnetenversallung der Wissenschaftsstadt Darmstadt am 27. M�rz 2011";
		txtSubTitle="Wahlprognose der Kommunalwahlen 2016, Wissenschaftsstadt Darmstadt";
		//Juri: This is the text over the qr-code
		qrCodeTitle = "QR-Code";
		qrCodeSubTitle1 = "zur automat.";
		qrCodeSubTitle2 = "Ausz�hlung";
	}

	/* (non-Javadoc)
	 * @see de.tud.vcd.votedevice.model.IBallotCardImageCreator#createImage(java.awt.Color)
	 */
	public ImageIcon createImage(Color bgcolor) throws Exception { 
		
    	ArrayList<Candidate> votedCandidates= new ArrayList<Candidate>();
    	crossedCandidates= new ArrayList<Candidate>();
    	
    	ArrayList<Party> pl=bc.getPartyList();
    	for (Party p: pl){
    		ArrayList<Candidate> cl=bc.getParty(p.getName()).getCandidates();
    		for(Candidate c:cl){
    			if (c.getCountedVotes()>0){
    				votedCandidates.add(c);
    			}
    			if (p.isVoted() && c.isCrossedOut()){
    				crossedCandidates.add(c);
    				System.out.println("crossed");
    			}
    		}
    	}
    	

        //Bild erzeugen und 2D Oberfl�che f�r besseres Rendering erstellen
        BufferedImage bufferedImage = new BufferedImage(resolution.width, resolution.height, BufferedImage.TYPE_INT_RGB); 
       
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        //Hintergrund einf�rben
        g2d.setColor(bgcolor);
        g2d.fillRect(0,0, resolution.width, resolution.height);
        
        //debugDrawHelpLines(g2d);
    	
        
        //Schriftfarbe setzen:
        g2d.setColor(Color.BLACK);
        
        //�berschrift setzen:
        g2d.setFont(new Font("SansSerif", Font.BOLD,getYMeasures(ueberschriftGroesse)));
    	FontMetrics fm = g2d.getFontMetrics(g2d.getFont());
		int height=fm.getAscent()-fm.getDescent();
    	int startX=getXMeasures(0+abstandLinks);
    	int startY=getYMeasures(0+abstandOben)+height;
    	g2d.drawString(txtTitle,startX, startY);
    	
//   	g2d.setFont(new Font("SansSerif", Font.PLAIN, 6));
//    	g2d.drawString("18:20", startX, startY-15);
    	//Juri: Puts the first two line over the QR-Code
    	g2d.setFont(new Font("SansSerif", Font.PLAIN,getYMeasures(subUeberschriftGroesse)));
    	fm = g2d.getFontMetrics(g2d.getFont());
    	height = fm.getAscent() - fm.getDescent();
    	g2d.drawString(qrCodeTitle, getXMeasures(seitenbreite-abstandRechts-qrCodeBreite), getYMeasures(0+abstandOben)+height);
    	g2d.drawString(qrCodeSubTitle1, getXMeasures(seitenbreite-abstandRechts-qrCodeBreite), getYMeasures(0+abstandOben)+(height*2));
    	
    	//Sub�berschrift setzen:
        g2d.setFont(new Font("SansSerif", Font.PLAIN,getYMeasures(subUeberschriftGroesse)));
    	fm = g2d.getFontMetrics(g2d.getFont());
		height=fm.getAscent()-fm.getDescent();
    	startX=getXMeasures(0+abstandLinks);
    	startY=startY+height;
    	g2d.drawString(txtSubTitle,startX, startY);
    	
    	//Juri: Puts the third line over the QR-Code
    	g2d.drawString(qrCodeSubTitle2, getXMeasures(seitenbreite-abstandRechts-qrCodeBreite), startY);
    	
        
    	//Parteifeld zeichnen:
    	startX=getXMeasures(0+abstandLinks);
    	startY=startY+getYMeasures(1);
    	g2d.drawRect(startX,startY ,getXMeasures(parteiFeldBreite ),getYMeasures(parteiFeldHoehe));
    	drawParteibox(g2d, startX,startY ,getXMeasures(parteiFeldBreite),getYMeasures(parteiFeldHoehe));
    	
    	//Infofeld zeichnen:
    	startX=getXMeasures(0+abstandLinks+parteiFeldBreite+1);
    	startY=startY+0;
    	g2d.drawRect(startX,startY ,getXMeasures(infoBoxBreite),getYMeasures(infoBoxHoehe));
    	drawInfobox(g2d,startX+1,startY+1 ,getXMeasures(infoBoxBreite),getYMeasures(infoBoxHoehe));
    	
    	//QRFeld zeichnen:
    	startX=getXMeasures(seitenbreite-abstandRechts-qrCodeBreite);
    	//startY=startY;
    	g2d.drawRect(startX,startY ,getXMeasures(qrCodeBreite),getYMeasures(qrCodeHoehe));
    	
    	//--------------QR Code----------------
        
		if (qrcodedrucken > 0) { //>0
			int qr_pos_x = startX + 1;
			int qr_pos_y = startY + 1;

			BufferedImage bimg = VotingQRCodeNew.encodeWahlzettelToQR(bc,
					(int) getXMeasures(qrCodeBreite)-1); //10
			g2d.drawImage(bimg, qr_pos_x, qr_pos_y, null);
		}
    	//--------------QR Code ENDE----------------
  if(bc.isValid()){
		int maxFelder=maxKandidaten+maxParteien+1;
    	int vertPlatz=seitenhoehe-abstandUnten-beginnStimmen;
    	int vertZeile=getYMeasures(vertPlatz);
    	
    	int startStimmen=getYMeasures(beginnStimmen);
    	int zeilenProSpalte=maxFelder/2;
    	
    	double zeilenhoehe=((double)vertZeile/(double)zeilenProSpalte);
    	
    	Candidate c_old=null;
    	int position=0;
    	int posX=getXMeasures(abstandLinks);
    	
   
    	
    	for (Candidate c: votedCandidates){
    	
    		//Damit man nur die k�rzere Version des Stimmzettels ausdrucken kann.
    //		if(c.getVotes() > 0) {
    		
    		if (c_old==null || !c.getParty().equals(c_old.getParty())){
    			if (position==zeilenProSpalte-1){
    				position++;
    			}
        		if(position<zeilenProSpalte){
        		}else{
        			posX=getXMeasures(seitenbreite-abstandRechts-spalteKandidatBreite);
        		}
    			//Partei malen:
        		drawPartei(g2d, posX,(int)(startStimmen+ (position % zeilenProSpalte)*zeilenhoehe),c.getParty(),bc.getParty(c.getParty()).countVotesToPrint(),(int)(zeilenhoehe-1));
        		System.out.println("Stimmen zum Ausdrucken: " + bc.getParty(c.getParty()).countVotesToPrint());
        		position++;
    		}
    		//Nun Kandidate malen
    		Align align;
    		if(position<zeilenProSpalte){
    			align=Align.LEFT;
    		}else{
    			align=Align.RIGHT;
    			int oldPosX=posX;
    			posX=getXMeasures(seitenbreite-abstandRechts-spalteKandidatBreite);
    			//Beim Spaltenwechsel oben noch eine Linie malen
    			if (oldPosX!=posX){
    				g2d.drawLine(posX,startStimmen, posX+getXMeasures(spalteKandidatBreite), startStimmen);
    	    		
    			}
    		}
    		//System.out.println(startStimmen+ (position % zeilenProSpalte)*zeilenhoehe);
    		double anteilTextVonZeile=0.7;
    		
    		//Diese Abfrage stellt sicher, dass keine Abst�nde entstehene
    		if(c.getVotesToPrint() > 0){
    		//Juri: This call to the method has been extended, accordingly and the last variable (method call) returns the required result
    		drawStimme(g2d, posX,(int)(startStimmen+ (position % zeilenProSpalte)*zeilenhoehe + ((1-anteilTextVonZeile)/2)*zeilenhoehe), ""+c.getId(),c.getName(), c.getPrename(),c.getVotesToPrint(), c.getReducedVotes(),3,(int)(zeilenhoehe*anteilTextVonZeile), align, c.getVotes());
    		g2d.drawLine(posX,(int)(startStimmen+ (position % zeilenProSpalte)*zeilenhoehe+zeilenhoehe), posX+getXMeasures(spalteKandidatBreite), (int)(startStimmen+ (position % zeilenProSpalte)*zeilenhoehe+zeilenhoehe));
    		//Weiterz�hlen
    		position++;
    		}
    		c_old=c;
    	}
  //  	}		
    	drawGestricheneStimmen(g2d,crossedCandidates);
    	
    }	
        //Bild nun ausgeben:
        ImageIcon icon = new ImageIcon(bufferedImage);
		return icon;
	}
	
	/**
	 * Zeichnet die gestrichenen Kandidaten in die mittlere Spalte
	 * @param g2d
	 * @param crossed_cl
	 */
	private void drawGestricheneStimmen(Graphics2D g2d, ArrayList<Candidate> crossed_cl){
		//Nur malen, wenn auch wirklich ben�tigt:
		if (!(crossed_cl!=null && crossed_cl.size()>0)){
			return;
		}
		//Kasten malen
		int x= getXMeasures(seitenbreite-spalteGestrichene)/2;
		int y= getYMeasures(beginnStimmen);
		int width=getXMeasures(spalteGestrichene);
		int height=getYMeasures(seitenhoehe-beginnStimmen-abstandUnten);
		
		
		//�berschrift einf�gen
		String text1="Gestrichene Kandidaten";
		String text2="im gekennzeichneten Wahlvorschlag";
		
		int marginRandPixelX=getXMeasures(marginRandGestrichene);
		int marginRandPixelY=getYMeasures(marginRandGestrichene);
		schriftGestricheneUeberschriftVorschlag=getYMeasures(schriftGestricheneUeberschriftVorschlag);
		schriftGestricheneVorschlag=getYMeasures(schriftGestricheneVorschlag);
		schriftGestricheneMin=getYMeasures(schriftGestricheneMin);
		
		//die zur verf�gung stehende Breite ausnutzen bis zur angegebenen Maximalschriftgr��e
		Font plainFont;;
		FontMetrics fm;
		int platzText=0;
		do{
			plainFont=new Font("SansSerif", Font.PLAIN,schriftGestricheneUeberschriftVorschlag);
			fm = g2d.getFontMetrics(plainFont);
			platzText=Math.max(fm.stringWidth(text1),fm.stringWidth(text2));
			schriftGestricheneUeberschriftVorschlag--;
		}while(platzText>(width-marginRandPixelX) && platzText>0);
		
		g2d.setFont(plainFont);
		g2d.drawString(text1, x+marginRandPixelX, y+fm.getAscent()-fm.getDescent()+marginRandPixelY);
		g2d.drawString(text2, x+marginRandPixelX, y+2*fm.getAscent()-fm.getDescent()+marginRandPixelY);
		int yLine=y+2*fm.getHeight()+marginRandPixelY-2*fm.getDescent();
		g2d.drawLine(x, yLine, x+width, yLine);
		
		//Zur Verf�gung stehenden Platz berechnen und anfangen die Liste auszugeben:
		int platz=height-yLine+y;
		double zeilenhoehe=(double)platz/maxKandidaten;
		//System.out.println("Z: "+zeilenhoehe);
		
		
		
		//Sicherstellen, dass die Schriftart nicht gr��er wird als der Vorschlag, um Riesenbuchstaben zu vermeiden bei wenigen Kandidaten
		if(zeilenhoehe>schriftGestricheneVorschlag){
			zeilenhoehe=schriftGestricheneVorschlag;
		}
		
		//Wenn Zeilenh�he kleiner ist als das Minimum, dann umschalten auf zwei spalten und nur Id ausgeben:
		int hoeheGestrichene=0;
		if (zeilenhoehe>=schriftGestricheneMin){
			//einspaltig ausgeben:
			plainFont=new Font("SansSerif", Font.PLAIN,(int)zeilenhoehe);
			g2d.setFont(plainFont);
			fm = g2d.getFontMetrics(plainFont);
			
			int position=0;
			for (Candidate c : crossed_cl){
				String cText=c.getId()+" "+c.getName()+", "+c.getPrename();
				//Pr�fen, ob Text reinpasst, ansonsten Vorname abk�rzen:
				int txtWidth=fm.stringWidth(cText);
				if (txtWidth>width-2*marginRandPixelX){
					cText=c.getId()+" "+c.getName()+", "+c.getPrename().substring(0,1)+".";
				}
				//Wenn es immer noch nicht passen solte, dann nachnamen auch noch beschneiden solange bis es passt
				String name=c.getName();
				while(fm.stringWidth(cText)>width-2*marginRandPixelX){
					name=name.substring(0, name.length()-1);
					cText=c.getId()+" "+name.substring(0, name.length()-1)+"., "+c.getPrename().substring(0,1)+".";
				}
				
				//Nun den Namen auch noch ausgeben:
				g2d.drawString(cText, x+marginRandPixelX, Math.round(yLine+fm.getAscent()+position*zeilenhoehe));
				
				//Juri: This four lines cross out the crossed out candidates with an orange line
		//		g2d.setColor(Color.BLACK); //ORANGE
		//		g2d.setStroke(new BasicStroke(2));
		//		g2d.drawLine(x, yLine + fm.getHeight() / 2 + (int) (position * zeilenhoehe), x+width, yLine + fm.getHeight() / 2 + (int) (position * zeilenhoehe));
				g2d.setColor(Color.BLACK);
				
				hoeheGestrichene=(int)Math.round(yLine+fm.getAscent()+position*zeilenhoehe+fm.getDescent());
				position++;
			}
		}else{
			//zweispaltig ausgeben (gucken, ob mit der vorgeschlagenen Gr��e gearbeitet werden kann:
			if (zeilenhoehe*2 > schriftGestricheneUeberschriftVorschlag){
				zeilenhoehe=schriftGestricheneVorschlag;
			}else{
				zeilenhoehe=2*zeilenhoehe;
			}
			
			plainFont=new Font("SansSerif", Font.PLAIN,(int)zeilenhoehe);
			fm = g2d.getFontMetrics(plainFont);
			g2d.setFont(plainFont);
			
			int position=0;
			int spalte=0;
			int idsProSpalte=(int)(platz/zeilenhoehe);
			for (Candidate c : crossed_cl){
				if (position>=idsProSpalte){
					spalte=width/2;
				}
				String cText=c.getId()+"";
				g2d.drawString(cText, x+marginRandPixelX+spalte, Math.round(yLine+fm.getAscent()+(position % idsProSpalte)*zeilenhoehe));
				hoeheGestrichene=(int)Math.round(yLine+fm.getAscent()+(position % idsProSpalte)*zeilenhoehe+fm.getDescent());
				position++;
				
				
			}
			
		}
		
		//Juri: This line makes sure that the width of the box of crossed out candidates is similar to all other lines
		g2d.setStroke(new BasicStroke(1));
		
		g2d.drawRect(x, y, width, hoeheGestrichene-y);
		
	}
	
	
	/**
	 * Zeichnet die Parteibox in der Infoleiste
	 * @param g2d
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	private void drawParteibox(Graphics2D g2d, int x, int y, int w, int h) {
		//g2d.drawString("parteibx", x+5, y+30);
		int kreuzMarginX=getXMeasures(2);
		int kreuzMarginY=getYMeasures(2);
		//erstmal ung�ltige abhandeln:
		if (!bc.isValid()){
			g2d.setFont(new Font("SansSerif", Font.BOLD,24));
			g2d.drawString("Ung�ltig", x+5, y+30);
			
		}else{
			//Stimmzettel ist zumindest zeilweise g�ltig:
			EnumSet<Validity> set = EnumSet.of(Validity.VALID_ONLY_PARTY, Validity.VALID_PARTY_AND_CANDIDATE, Validity.VALID_REDUCE_CANDIDATES);
			Validity validity=bc.getValidity();
			if (set.contains(validity  )){
				//eine Parteistimme ist vorhanden
				int kreuzGroesseX=getXMeasures(8);
				int kreuzGroesseY=getYMeasures(8);
				
				
				Stroke stroke=g2d.getStroke();
				BasicStroke stroke1
		        = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
		                          BasicStroke.JOIN_MITER);
				g2d.setStroke(stroke1);
			
			if(bc.countVotedParties() == 1){	
				//Juri: fills in the circle of the selected party with orange
				g2d.drawOval(x+kreuzMarginX, y+kreuzMarginY, kreuzGroesseX, kreuzGroesseY);
			//	g2d.setColor(Color.BLACK);//ORANGE
			//	g2d.fillOval(x+kreuzMarginX, y+kreuzMarginY, kreuzGroesseX, kreuzGroesseY);
				g2d.setColor(Color.BLACK);
				
				g2d.drawLine(x+kreuzMarginX, y+kreuzMarginY, x+kreuzMarginX+kreuzGroesseX, y+kreuzMarginY+kreuzGroesseY);
				g2d.drawLine(x+kreuzMarginX, y+kreuzMarginY+kreuzGroesseY, x+kreuzMarginX+kreuzGroesseX, y+kreuzMarginY);
				
				g2d.setStroke(stroke);
				String partyName="";
				for(Party p: bc.getPartyList()){
					if (p.isVoted())partyName=p.getName();
				}
				g2d.setFont(new Font("SansSerif", Font.PLAIN,getYMeasures(6)));
				g2d.drawString(partyName, x+2*kreuzMarginX+kreuzGroesseX, y+kreuzMarginY+g2d.getFontMetrics(g2d.getFont()).getAscent());
			}	
				//in der Parteistimme sind gestrichene Kandidaten oder
				//in der Parteistimme sind zus�tzliche Kreuze:
				
			//	if (validity==Validity.VALID_PARTY_AND_CANDIDATE || validity==Validity.VALID_REDUCE_CANDIDATES){
			//		g2d.setFont(new Font("SansSerif", Font.PLAIN,getYMeasures(4)));
					//g2d.drawString("Liste wurde ver�ndert.", x+kreuzMarginX, y+kreuzMarginY+kreuzGroesseY+g2d.getFontMetrics(g2d.getFont()).getAscent());
			//	}else{
			//		g2d.setFont(new Font("SansSerif", Font.PLAIN,getYMeasures(4)));
			//		g2d.drawString("Das Listenkreuz entspricht", x, y+kreuzMarginY+kreuzGroesseY+g2d.getFontMetrics(g2d.getFont()).getAscent());
			//		g2d.drawString("der untenstehenden Darstellung.", x, y+kreuzMarginY+kreuzGroesseY+g2d.getFontMetrics(g2d.getFont()).getAscent()+g2d.getFontMetrics(g2d.getFont()).getHeight());
			//	}
				
			}else if(validity==Validity.VALID_REDUCE_PARTIES){
				 //Mehrfach vergebene Kopfstimmen werden ignoriert, da noch zus�tzliche Stimmen da sind
				g2d.setFont(new Font("SansSerif", Font.BOLD,getYMeasures(4)));
				FontMetrics fm = g2d.getFontMetrics(g2d.getFont());
				int hoehe=fm.getAscent();
				g2d.drawString("Hinweis:", x+kreuzMarginX, y+kreuzMarginY+hoehe);
				hoehe+=fm.getHeight();
				
				g2d.setFont(new Font("SansSerif", Font.PLAIN,getYMeasures(4)));
				fm = g2d.getFontMetrics(g2d.getFont());
				g2d.drawString("Mehrfach an der Kopfleiste", x+kreuzMarginX, y+kreuzMarginY+hoehe);
				hoehe+=fm.getHeight();
				g2d.drawString("gekennzeichnete Wahl-", x+kreuzMarginX, y+kreuzMarginY+hoehe);
				hoehe+=fm.getHeight();
				g2d.drawString("vorschl�ge werden ignoriert.", x+kreuzMarginX, y+kreuzMarginY+hoehe);
			}else if(validity==Validity.VALID_NO_PARTY){
			//	g2d.setFont(new Font("SansSerif", Font.PLAIN,getYMeasures(4)));
			//	g2d.drawString("Kein Wahlvorschlag wurde an der Kopfleiste gekennzeichnet", x+kreuzMarginX, y+kreuzMarginY+g2d.getFontMetrics(g2d.getFont()).getAscent());
				g2d.setFont(new Font("SansSerif", Font.BOLD,getYMeasures(4)));
				FontMetrics fm = g2d.getFontMetrics(g2d.getFont());
				int hoehe=fm.getAscent();
				g2d.drawString("Hinweis:", x+kreuzMarginX, y+kreuzMarginY+hoehe);
				hoehe+=fm.getHeight();
				
				g2d.setFont(new Font("SansSerif", Font.PLAIN,getYMeasures(4)));
				g2d.drawString("Kein Wahlvorschlag wurde an", x+kreuzMarginX, y+kreuzMarginY+hoehe);
				hoehe+=fm.getHeight();
				g2d.drawString("der Kopfleiste gekennzeichnet.", x+kreuzMarginX, y+kreuzMarginY+hoehe);
			}
		
			
		}
		
		
	}
	
	/**
	 * Zeichnet die Infobox oben in der Mitte und legt den Text fest.
	 * @param g2d
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	private void drawInfobox(Graphics2D g2d, int x, int y, int w, int h){
		int manual=bc.countCandidatesManualVotes();
		int dist=bc.countCandidatesDistributedVotes();
		int vakant=maxKandidaten-manual-dist;
		
		String text=maxKandidaten+" Stimmen stehen insgesamt zur Verf�gung. Direkt vergebene Stimmen: "+manual+", �ber die Kopfleiste zugeteilte Stimmen: "+dist+", nicht vergebene Stimmen: "+vakant+".";
		
		String korrekturMehrereListen="Der Stimmzettel wurde geheilt, da mehr als ein Wahlvorschlag gekennzeichnet wurde. Es wurden daher nur die an einzelnen Bewerber*innen direkt vergebenen Stimmen beachtet.";
		String korrekturZuVieleStimmen="Der Stimmzettel wurde geheilt, da mehr als "+maxKandidaten+" Stimmen innerhalb eines Wahlvorschlags vergeben wurden. Die zus�tzlichen Stimmen wurden daher abgeschnitten. Die zu z�hlenden Stimmen wurden ausgedruckt.";
		
		String ungueltigManuell="Der Stimmzettel ist ung�ltig, da \"Ung�ltig w�hlen\" ausgew�hlt wurde.";
		String ungueltigMehr1Liste="Der Stimmzettel ist ung�ltig, da mehr als ein Wahlvorschlag in der Kopfleiste gekennzeichnet wurde, jedoch keine einzelnen Bewerber*innen ausgew�hlt wurden.";
		String ungueltigZuVieleKandidaten="Der Stimmzettel ist ung�ltig, da mehr als " + maxKandidaten + " Stimmen auf Bewerber*innen verschiedener Wahlvorschl�ge vergeben wurden.";
		String ungueltigLeereListe="Der Stimmzettel ist ung�ltig, da keine Stimmen vergeben wurden.";
		
		//String gueltigGestrichene="In der gew�hlten Liste wurden Kandidaten gestrichen.";
		EnumSet<Validity> set = EnumSet.of(Validity.VALID_NO_PARTY,Validity.VALID_ONLY_PARTY, Validity.VALID_PARTY_AND_CANDIDATE , Validity.VALID_REDUCE_PARTIES);
		Validity validity=bc.getValidity();
		
		//Ung�ltige F�lle
		if (!bc.isValid()){
			//ArrayList<RejectReasons> rr= bc.getFails();
			if (validity==Validity.INVALID_MANUAL){
				drawTextToInfobox(g2d, x, y, w, h, ungueltigManuell);
			}else if (validity==Validity.INVALID_ONLY_PARTIES){
				drawTextToInfobox(g2d, x, y, w, h, ungueltigMehr1Liste);
			}else if (validity==Validity.INVALID_TOOMUCHCANDIDATES){
				drawTextToInfobox(g2d, x, y, w, h, ungueltigZuVieleKandidaten);
			}else if (validity==Validity.INVALID_EMPTY){
				drawTextToInfobox(g2d, x, y, w, h, ungueltigLeereListe);
			}
		}else 
		//G�ltiger Stimmzettel
			
		if (set.contains(bc.getValidity()) && bc.countVotedParties() <= 1){
			drawTextToInfobox(g2d, x, y, w, h, text);
		}else 
			//G�ltiger Stimmzettel
		if (validity==Validity.VALID_REDUCE_CANDIDATES){
			drawTextToInfobox(g2d, x, y, w, h, korrekturZuVieleStimmen);
		}
		
		if(set.contains(bc.getValidity()) && bc.countVotedParties() > 1){
			drawTextToInfobox(g2d, x, y, w, h, korrekturMehrereListen);
		}
		
	}
	
	/**
	 * Zeichnet den Text in die Infobox und berechnet dabei den Zeilenumbruch automatisch
	 * @param g2d
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param text
	 */
	private void drawTextToInfobox(Graphics2D g2d, int x, int y, int w, int h, String text){
		int margin=getXMeasures(2);
		String umbruch="$";
		
		int startSchriftgoesse=getYMeasures(6);
		int schriftHoehe;
		ArrayList<String> zeilen=new ArrayList<String>();
		do{
			zeilen.clear();
			String textSchleife=text;
		
		g2d.setFont(new Font("SansSerif", Font.PLAIN,startSchriftgoesse));
		
		FontMetrics fm = g2d.getFontMetrics(g2d.getFont());
		schriftHoehe=fm.getHeight();
		//int platzName=fm.stringWidth(name);
		textSchleife=textSchleife+" ";
		
		while (textSchleife.length() != 0) {
			int index = 0;
			String zeile = "";
			int platzBedarf = 0;
			do {

				zeile = textSchleife.substring(0, index);
				platzBedarf = fm.stringWidth(zeile);
				index++;
			} while (platzBedarf < (w - 2 * margin) && index <= textSchleife.length());
			// Jetzt ist Platz �berschritten, nun zur�ck bis zum letzten
			// Leerzeichen
			int posLeerzeichen;
			if (index <= textSchleife.length()) {
				posLeerzeichen = zeile.lastIndexOf(" ");
			} else {
				posLeerzeichen = zeile.length() - 1;
			}
			if (zeile.indexOf(umbruch) != -1) {
				posLeerzeichen = zeile.indexOf(umbruch);
			}
			if (posLeerzeichen == -1)
				posLeerzeichen = 0;
			zeile = zeile.substring(0, posLeerzeichen);
			textSchleife = textSchleife.substring(posLeerzeichen + 1);
			zeilen.add(zeile);
			
			
		}
		startSchriftgoesse--;
		//System.out.println("Ist: "+zeilen.size()+"  Soll: "+((h-2*margin)/(schriftHoehe))+" Schriftgr��e: "+(startSchriftgoesse+1));
		}while(zeilen.size()>((h-2*margin)/(schriftHoehe)) && startSchriftgoesse>0);
		
		//nun endlich ausgeben:
		FontMetrics fm = g2d.getFontMetrics(g2d.getFont());
		int zeilenNummer = 0;
		for (String z : zeilen) {
			g2d.drawString(z, x + margin, y + margin + fm.getAscent()
					+ zeilenNummer * fm.getHeight());
			zeilenNummer++;
		}
	}
	
	
	
	//private void debugDrawHelpLines(Graphics2D g2d){
		//DEBUG: MA?BAND
//    	g2d.setColor(Color.RED);
//    	g2d.setFont(new Font("SansSerif", Font.PLAIN,8));
//    	
//    	for(int i=0;i<31;i++){
//    		g2d.drawLine(0, getYMeasures(i*10), 5, getYMeasures(i*10));
//    		g2d.drawString(""+i, 5, getYMeasures(i*10));
//    	}
//    	
//    	for(int i=0;i<22;i++){
//    		g2d.drawLine(getXMeasures(i*10), 0, getXMeasures(i*10), 5);
//    		g2d.drawString(""+i, getXMeasures(i*10), 10);
//    	}
//    	
//    	g2d.drawLine(0, getYMeasures(abstandOben), getXMeasures(seitenbreite), getYMeasures(abstandOben));
//    	g2d.drawLine(0, getYMeasures(seitenhoehe-abstandUnten), getXMeasures(seitenbreite), getYMeasures(seitenhoehe-abstandUnten));
//    	g2d.drawLine(getXMeasures(abstandLinks), 0, getXMeasures(abstandLinks), getYMeasures(seitenhoehe));
//    	g2d.drawLine(getXMeasures(seitenbreite-abstandRechts), 0, getXMeasures(seitenbreite-abstandRechts), getYMeasures(seitenhoehe));
    	
	//}
	
	/**
	 * Schreibt eine Parteitrennung in die Liste. Dabei wird der Name und die Anzahl an Stimmen innerhalb der Partei ausgegeben.
	 * @param g2d
	 * @param x
	 * @param y
	 * @param name
	 * @param votes
	 * @param lineHeight
	 */
	private void drawPartei(Graphics2D g2d, int x, int y, String name, int votes, int lineHeight){
		int kandidatFontSize=(int)Math.round(lineHeight*0.75);//(int)(kreuzAnteil*lineHeight);
		Font boldFont=new Font("SansSerif", Font.BOLD,kandidatFontSize);
		Font plainFont=new Font("SansSerif", Font.PLAIN,kandidatFontSize);
		g2d.setFont(boldFont);
		FontMetrics fm = g2d.getFontMetrics(plainFont);
		int platzName=fm.stringWidth(name);
		
		int platzVotes=fm.stringWidth(""+votes+" Stimmen");
		if ( (platzName+platzVotes)>getXMeasures(spalteKandidatBreite)){
			System.out.println("WARNUNG: Partei mit Stimmen kann nicht ganz abgebildet werden (Partei: "+name+")");
		}
		
		g2d.drawRect(x, y, getXMeasures(spalteKandidatBreite), lineHeight-1);
		
		int fontY=y+fm.getAscent()-fm.getDescent()+(int)Math.round(lineHeight*0.125);;
		g2d.drawString(name,x+3, fontY);
		g2d.setFont(plainFont);
		g2d.drawString(votes+" Stimmen", x+getXMeasures(spalteKandidatBreite)-platzVotes-3, fontY);
	}
	
	/**
	 * Zeichnet eine Stimme auf den Stimmzettel
	 * @param g2d
	 * @param x
	 * @param y
	 * @param id
	 * @param name
	 * @param forename
	 * @param votes
	 * @param reducedVotes
	 * @param possibleVotes
	 * @param lineHeight
	 * @param align
	 */
	//Juri: This method has been extended with the variable votesmanually, such that it is possible to identify those votes that 
	//are cast manually and for those to fill in the rectangle with Orange
	private void drawStimme(Graphics2D g2d, int x, int y, String id, String name, String forename, int votes,int reducedVotes, int possibleVotes,int lineHeight, Align align, int votesmanually){
		int margin=3; 
		//double zeilenAnteil=0.7;
		
		
		int kandidatFontSize=(int)Math.round(lineHeight*0.75);//(int)(kreuzAnteil*lineHeight);
		//System.out.println("LINEHEUIGHT"+lineHeight);
		//Platzbedarf berechnen:
		//Kreuze:
		int platzKreuze=possibleVotes*((int)(lineHeight*0.8)+margin)-margin;
		//ID:
		g2d.setFont(new Font("SansSerif", Font.PLAIN,kandidatFontSize));
		FontMetrics fm = g2d.getFontMetrics(g2d.getFont());
		int platzId=fm.stringWidth("0000")+margin;
		int realerPlatzId=fm.stringWidth(id);
		int platzName=getXMeasures(spalteKandidatBreite-2)-platzId-platzKreuze;
		
		//Pr�fen, ob der Platz f�r den Namen ausreichen w�rde
		int realerPlatzName=fm.stringWidth(name+", "+forename);
		if (realerPlatzName>platzName){
			System.out.println("WARNUNG: Name kann nicht ganz abgebildet werden (ID: "+id+")");
		}
		
		
		int fontY=y+fm.getAscent()-fm.getDescent()+1+2;
		switch(align){
		case LEFT:
			drawMehrereKreuze(g2d,(int)((Math.round(x+lineHeight*0.1))),(int)(Math.round(y+lineHeight*0.1)),margin,votes,reducedVotes,possibleVotes,(int)(lineHeight*0.8), votesmanually);
			g2d.drawString(name+", "+forename, x+platzKreuze+3*margin,fontY);
			g2d.drawString(id, x+getXMeasures(spalteKandidatBreite)-realerPlatzId, fontY);
			break;
		case RIGHT:
			g2d.drawString(id,x+platzId-realerPlatzId , fontY);
			g2d.drawString(name+", "+forename, x+platzId+3*margin, fontY);
			drawMehrereKreuze(g2d,x+getXMeasures(spalteKandidatBreite)-platzKreuze,y,margin,votes,reducedVotes,possibleVotes,(int)(lineHeight*0.8), votesmanually);	
			break;
		default:
			System.out.println("FEHLER: Falsche Ausrichtung (ID: "+id+")");
			break;
			
		}
		
		//drawMehrereKreuze(g2d,x,y,margin,votes,possibleVotes,lineHeight);
	}
	
	/**
	 * Malt die entsprechende Anzahl an Kreuzen und leeren Felde auf das Blatt. 
	 * 
	 * @param g2d
	 * @param x: X Position Ecke links oben
	 * @param y Y Position Ecke links oben
	 * @param margin Abstand zwischen den K�stchen
	 * @param gekreuzt Anzahl an gekreuzten K�stchen
	 * @param gesamt Anzahl an K�stchen gesamt. Also gekreuzte und leere
	 */
	//Juri: This method has been extended with the variable votesmanually, such that it is possible to identify those votes that 
		//are cast manually and for those to fill in the rectangle with Orange
	private void drawMehrereKreuze(Graphics2D g2d, int x, int y,int margin, int gekreuzt,int reducedVotes, int gesamt, int lineHeight, int votesmanually){
		int width=lineHeight;
		
	//	System.out.println("gekreuzt: " + gekreuzt + "reducedvotes: " + reducedVotes + "gesamt: " + gesamt + "votesmanually: " + votesmanually);
		
	//	for(int i=gekreuzt;i<reducedVotes;i++){
			//drawKreuz(g2d, x+i*(width+margin), y, width, width);
	//		g2d.drawRect( x+i*(width+margin), y, width, width);
	//	}
		int kreuze = gekreuzt;
				
		for(int i=0;i<gesamt;i++){
			
			//Juri: The if condition make sure that rects which contain manually cast votes are fill in with orange
			if(votesmanually !=0 && kreuze > 0){
				g2d.drawRect(x+i*(width+margin), y, width, width);
			//	g2d.setColor(Color.BLACK);//ORANGE
			//	g2d.fillRect(x+i*(width+margin), y, width, width);
				g2d.setColor(Color.BLACK);
				votesmanually = votesmanually -1;
				kreuze = kreuze - 1;
			}else{
			  g2d.drawRect( x+i*(width+margin), y, width, width);
			
			}
		}
		
		//Juri: originally this was in the first place, however it has been put last, in order to not blind the crosses due to 
		//filling with color
		for(int i=0;i<gekreuzt;i++){
			drawKreuz(g2d, x+i*(width+margin), y, width, width);
		}
	}
	
	
	/**
	 * Malt ein Kreuz mit Kasten
	 * @param g2d
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	private void drawKreuz(Graphics2D g2d, int x, int y, int width, int height){
		double kreuzAnteil=0.8;
		
		g2d.drawRect(x, y, width, height);
		g2d.drawLine(x+(int)(width*(1-kreuzAnteil)), y+(int)(width*(1-kreuzAnteil)), x+(int)(width*kreuzAnteil),y+(int)(height*kreuzAnteil));
		g2d.drawLine(x+(int)(width*(1-kreuzAnteil)), y+(int)(height*kreuzAnteil), x+(int)(width*kreuzAnteil),y+(int)(width*(1-kreuzAnteil)));
	}

	  
    
    /**
     * Rechnet die X Koordinate von mm in Pixel um 
     * @param x Millimeter
     * @return int Pixel
     */
    private int getXMeasures(int x){
    	return (int)((x*resolution.width)/210);
    }
    
    /**
     * Rechnet die Y Koordinate von mm in Pixel um 
     * @param x Millimeter
     * @return int Pixel
     */
    private int getYMeasures(int y){
    	return (int)((y*resolution.height)/297);
    }
    
    
//    private static ImageIcon testImage(int w, int h){
//    	 BufferedImage bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB); 
//         Graphics2D g2d = bufferedImage.createGraphics();
//         g2d.setColor(Color.RED);
//         g2d.fillRect(30, 40, 200, 300);
//        // bufferedImage.getGraphics();
//        ImageIcon icon = new ImageIcon(bufferedImage);
//    	return icon;
//    }

//    public static void main ( String[] args )
//  {
//      try
//      {
//          // 
//          UIManager.setLookAndFeel ( UIManager.getSystemLookAndFeelClassName () );
//      }
//      catch ( Throwable e )
//      {
//          //
//      }
//
//      final JFrame f = new JFrame ();
//      f.setPreferredSize(new Dimension(800, 1000));
//      f.setLayout(null);
//      f.getRootPane ().setOpaque ( true );
//      f.getRootPane ().setBackground ( Color.GRAY );
//      f.getRootPane ().setBorder ( BorderFactory.createEmptyBorder ( 10, 10, 10, 10 ) );
//
//      f.getContentPane ().setBackground ( Color.GRAY );
//      //f.getContentPane ().setLayout ( new BorderLayout ( 5, 5 ) );
//      
//      
//      int w=674;
//      int h=950;
//      JLabel pic= new JLabel();
//      pic.setBounds(10, 10,w , h);
//      pic.setPreferredSize(new Dimension(w, h));
//      pic.setMaximumSize(new Dimension(w,h));
//      pic.setBackground(Color.GRAY);
//      pic.setOpaque(true);
//      
//      
//      try {
//    	 InputStream filename=Starter.class.getClassLoader().getResource("wahlzettel.xml").openStream();
//  		BallotCardDesign bcd= BallotCardDesign.getInstance(filename);
//  		
////  		InputStream filename=getClass().getClassLoader().getResource("wahlzettel.xml").openStream();
////		BallotCardDesign bcd = BallotCardDesign.getInstance(filename);
//  		
//  		BallotCard bc=new BallotCard(bcd);
////  		bc.setCandidateVote("CDU", 108, 1);
////  		bc.setCandidateVote("CDU", 109, 2);
////  		bc.setCandidateVote("CDU", 110, 3);
////  		bc.setCandidateVote("CDU", 111, 3);
//  		for (int i=101;i<145;i++){
//  			bc.setCandidateVote("CDU", i, 3);
//  		}
//  		//bc.
//  		//bc.setCandidateVote("CDU", 114, 3);
//  		bc.setPartyVoted("UWIGA", true);
////  		bc.setCandidateCrossed("FWDA", 1001);
////  		bc.setCandidateVote("FWDA", 1002,2);
//		BallotCardImageCreatorNewDesign bcic= new BallotCardImageCreatorNewDesign(w,h,bc );
//		pic.setIcon(testImage(w,h));
//		//pic.setIcon(bcic.createImage(Color.WHITE));
//		pic.setIcon(bc.createBallotCardReview(w, h));
//	} catch (Exception e) {
//		// Auto-generated catch block
//		e.printStackTrace();
//	}
//      
//      
//      
//      f.getContentPane ().add ( pic);
//      
//      
//	
//      f.setDefaultCloseOperation ( JFrame.EXIT_ON_CLOSE );
//      f.pack ();
//      f.setLocationRelativeTo ( null );
//      f.setVisible ( true );
//  }

}
