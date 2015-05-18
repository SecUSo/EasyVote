package de.tud.vcd.votedevice.municipalElection.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.tud.vcd.common.BallotCardDesign;
import de.tud.vcd.common.BallotCardDesign.DesignKeys;
import de.tud.vcd.common.PropertyHandler;
import de.tud.vcd.common.exceptions.DesignKeyNotInXMLException;
import de.tud.vcd.votedevice.controller.Language;
import de.tud.vcd.votedevice.multielection.VCDListener;
import de.tud.vcd.votedevice.multielection.VCDView;
import de.tud.vcd.votedevice.multielection.exceptions.CannotConvertListenerException;
import de.tud.vcd.votedevice.municipalElection.controller.MunicipalElectionListener;
import de.tud.vcd.votedevice.municipalElection.model.BallotCard;
import de.tud.vcd.votedevice.municipalElection.model.BallotCard.Validity;
import de.tud.vcd.votedevice.municipalElection.model.Candidate;
import de.tud.vcd.votedevice.municipalElection.model.Party;
import de.tud.vcd.votedevice.municipalElection.model.exceptions.CandidateNotFoundException;
import de.tud.vcd.votedevice.municipalElection.model.exceptions.PartyNotFoundException;
import de.tud.vcd.votedevice.onscreenkeyboard.OnScreenKeyboard;
import de.tud.vcd.votedevice.view.JRoundedButton;
import de.tud.vcd.votedevice.view.JRoundedPartyButton;
import de.tud.vcd.votedevice.view.VCDElectionContent;
import de.tud.vcd.votedevice.view.VotingDeviceGui;

/**
 * Zentrale Anzeige für die Kommunalwahl. Ist die dazugebörige View zur BallotCard
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class MunicipalElectionView extends VCDElectionContent implements VCDView, Observer {
	public enum State {
		  INIT, VOTING, REVIEW, PRINT, FINALISED 
	}
	
	private State state;
	
	PropertyHandler prop;
	private ImageIcon iconPartyVoted;
	private ImageIcon iconCandidateVoted;
	MunicipalElectionVotePopup mevp;
	MouseListener localMouseListener;
	ShowACandiate[] showc;
	JShowAParty showp;
	JLabel initLabel;
	JPanel reviewPane;
	JLabel reviewImage;
	JLabel reviewText;
	OnScreenKeyboard osk;
	
	/**
	 * Erzeugt die Ansicht und erhält das Wissen über die übergeordnete Anzeige
	 * @param vdg
	 */
	public MunicipalElectionView(VotingDeviceGui vdg) {
		super(vdg);
		state=State.INIT;
		prop= new PropertyHandler("configuration.properties");
		
		Color foreground= new Color(Integer.parseInt(prop.getProperty("neutralColor","0"),16));
		vdg.setInfoFontColor(foreground);
		
		//load the iconimages to share ressource and to save time
		URL imageURLl =getClass().getClassLoader().getResource("buttonIconPartyVoted.gif");
		iconPartyVoted=null;
		if (imageURLl != null) {
		    iconPartyVoted = new ImageIcon(imageURLl);
		    Image image = iconPartyVoted.getImage(); // transform it
		    image = image.getScaledInstance(25, 25,  Image.SCALE_FAST); // scale it the smooth way 
		    iconPartyVoted = new ImageIcon(image);  // transform it back
		}
		imageURLl =getClass().getClassLoader().getResource("buttonIconCandidateVoted.gif");
		iconCandidateVoted=null;
		if (imageURLl != null) {
			iconCandidateVoted = new ImageIcon(imageURLl);
		    Image image = iconCandidateVoted.getImage(); // transform it
		    image = image.getScaledInstance(25, 25,  Image.SCALE_FAST); // scale it the smooth way 
		    iconCandidateVoted = new ImageIcon(image);  // transform it back
		}
		
		createJMunicicalElectionVotePopup();
		
		createSearchPopup();
	
		
		
		 //
		ActionListener localOpenSearchListener = new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				//System.out.println("Open search");
				
				if (osk.isVisible()){
					osk.setVisible(false);
					//vd  vdg.openSearch(false);
				}else{
					l.actionPerformed(new ActionEvent(osk, ActionEvent.ACTION_PERFORMED,"configSearch"));
					osk.setVisible(true);
				}
				
			}
		};
		
		vdg.setSearch(localOpenSearchListener);
		
		localMouseListener= new MouseListener() {
			
			public void mouseClicked(MouseEvent e) {
				int locX = contentSmallSmall.getLocation().x;
				int locY = contentSmallSmall.getLocation().y;
				
				Component c = contentSmallSmall.getComponentAt(
						(e.getLocationOnScreen().x - locX),
						(e.getLocationOnScreen().y - locY));
				//System.out.println(c.toString());
				if ((c instanceof ShowACandiate)&& c.isVisible() && !mevp.isVisible())
				{

					//set position of the popup in the visible field
					int newX=e.getXOnScreen();
					if( !contentSmallSmall.contains(e.getXOnScreen() - locX+mevp.getWidth(), e.getYOnScreen() - locY)){
						newX=contentSmallSmall.getWidth()-mevp.getWidth()+locX-5;
					};
					
					int newY=e.getYOnScreen();
					if( !contentSmallSmall.contains(e.getXOnScreen()- locX, e.getYOnScreen() - locY+mevp.getHeight())){
						newY=contentSmallSmall.getHeight()-mevp.getHeight()+locY-5;
					};
					
					mevp.setLocation(newX,newY);
					l.actionPerformed(new ActionEvent(c, ActionEvent.ACTION_PERFORMED,((ShowACandiate)c).getActionCommmand()));
					
				}else{
					l.actionPerformed(new ActionEvent(c, ActionEvent.ACTION_PERFORMED,"-1"));
					
				}
				

			}

			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		};
		contentSmallSmall.addMouseListener(localMouseListener);
		createContentSmallSmall_Review();
		createContentSmallSmall_Voting();
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MunicipalElectionListener l;
	
	/* (non-Javadoc)
	 * @see de.tud.vcd.votedevice.view.VCDElectionContent#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		//System.out.println("MunicipalElectionView updated called");
		if (arg instanceof Language){
			contentSmallSmall.removeAll();
			createContentSmallSmall_Voting();
			createContentSmallSmall_Review();
			showp.addActionListener((MunicipalElectionListener)l);
		}
		switch (state) {
		case INIT:
			stateInit(arg);
			
			break;
		case VOTING:
			stateVoting(arg);
			// Versuche das Bild zu vergrößern, geht von der Theorie aber ungewöhnlich von der Nutzung
//			try {
//				Robot robo= new Robot();
//				Image img= robo.createScreenCapture(new Rectangle(100, 100, 130, 130));
//				img=img.getScaledInstance(260, 260, Image.SCALE_FAST);
//				JFrame frame= new JFrame("Lupe");
//				frame.setSize(300, 300);
//				JLabel l= new JLabel(new ImageIcon(img));
//				
//				frame.add(l);
//				frame.setVisible(true);
//			} catch (AWTException e1) {
//				e1.printStackTrace();
//			}
			break;
		case REVIEW:
			if (arg instanceof BallotCard){
				BallotCard bc=(BallotCard)arg;
				bc.setState(BallotCard.State.VOTING);
			}
			stateReview(arg);
			break;
		case PRINT:
			statePrint(arg);
			break;
		case FINALISED:
			boolean freigabeErforderlich=false;
			PropertyHandler ph= new PropertyHandler("configuration.properties");
			String freigabeString=ph.getProperty("FREIGABEERFORDERLICH", "1");
			if (freigabeString.equals("1")){
				freigabeErforderlich=true;
			}
			
			
				
			if (freigabeErforderlich){
				stateLocked(arg);
			}else{
				setState(State.INIT);
				if (arg instanceof BallotCard){
					((BallotCard)arg).updateObserver();
				}
			}
			
			
			break;
		default:
			break;
		}
		BallotCard bc=null;
		if (arg instanceof BallotCard){
			bc=((BallotCard)arg);
		}
		setInfoBox(bc);
		

		revalidate();
	}
	
	/**
	 * Verschiebt das PopUp zur Stimmauswahl an die entsoprechende Stelle für den Kandidaten
	 * @param id
	 */
	public void setPositionOfPopUpToCandidate(int id){
		
		ShowACandiate sac_selected=null;
		//Nach der entsprechenen Position suchen:
		for(ShowACandiate sac : showc){
			if (sac.getCandidateId()==id){
				sac_selected=sac;
				break;
			}
		}
		//Wenn eine gefunden wurde hierzu das PopUp verschieben:
		if (sac_selected!=null && sac_selected.isVisible()){
			//System.out.println(sac_selected.getX());
			int xx=sac_selected.getX()+50;
			int yy=sac_selected.getY()+10;
			
			//set position of the popup in the visible field
			int locX = contentSmallSmall.getLocation().x;
			int locY = contentSmallSmall.getLocation().y;	
			
			int newX=xx+locX;
			if( !contentSmallSmall.contains(    newX-locX +mevp.getWidth()    , yy )){
				newX=contentSmallSmall.getWidth()-mevp.getWidth()+locX-5;
			};
				
			int newY=yy+locY;	
			if( !contentSmallSmall.contains(xx, newY - locY+mevp.getHeight())){
				newY=contentSmallSmall.getHeight()-mevp.getHeight()+locY-5;
			};
				
			mevp.setLocation(newX,newY);
		}
	}
	
	/**
	 * @param state
	 */
	public void setState(State state){
		this.state=state;
	}

	private Dimension resolution=null;
	
	
	/**
	 * Liefert die gewünschte Bildschirmgröße aus der Konfigurationsdatei.
	 * @return
	 */
	private Dimension getScreenSize(){
		if (resolution==null){
			
			PropertyHandler ph= new PropertyHandler("configuration.properties");
			int res_x=Integer.parseInt(ph.getProperty("RESOLUTION_X", "1920"));
			int res_y=Integer.parseInt(ph.getProperty("RESOLUTION_Y", "1080"));
			resolution= new Dimension(res_x, res_y);
			
		//}
	//    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	//	int width = gd.getDisplayMode().getWidth();
	//	int height = gd.getDisplayMode().getHeight();
	//	resolution = new Dimension(width, height);
		}
		return resolution;
	}

	/**
	 * Legt den Status fest auf die Kontrollansicht REVIEW
	 * @param arg
	 */
	private void stateReview(Object arg){
		setContentToThreeParts();
		//setContentToTwoParts();
		if (arg instanceof BallotCard){
			calculateStatusText((BallotCard)(arg));
			paintJMenuSmall((BallotCard)(arg));
		    paintContentSmallSmall_Review((BallotCard)(arg));
		}	
		
		repaint();
		revalidate();
	}
	
	/**
	 * Legt den Status auf die erste Anzeige fest.
	 * @param arg Ballotcard
	 */
	private void stateInit(Object arg){
		
		contentSmallGiant.removeAll();
		Color foreground= new Color(Integer.parseInt(prop.getProperty("neutralColor","0"),16));
		int marginLeft=(int)(contentSmallSmall.getSize().width*0.0404);
		String initCaption=Language.getInstance().getString("INITCAPTION");
		
		
		String initText = "<html><ul><li>"+Language.getInstance().getString("WAHLREGELN1") +
					"( <img src=\"" + getClass().getClassLoader().getResource("onevote.png") + "\">  "+Language.getInstance().getString("WAHLREGELN2") +
					"<img src=\"" + getClass().getClassLoader().getResource("twovote.png") + "\">  "+Language.getInstance().getString("WAHLREGELN2") +
					"<img src=\"" + getClass().getClassLoader().getResource("threevote.png") + "\"> ).<br><br></li>" +
				"" +
				" <li> "+Language.getInstance().getString("WAHLREGELN3") +
				"<img src=\"" + getClass().getClassLoader().getResource("partycross.png") + "\"> . "+Language.getInstance().getString("WAHLREGELN4")+"<br><br></li>" +
				"" +
				" <li> "+Language.getInstance().getString("WAHLREGELN5") +
				"<img src=\"" + getClass().getClassLoader().getResource("partycross.png") + "\"> , "+Language.getInstance().getString("WAHLREGELN6")+" <br><br></li>" +
				"" +
				"<li>"+Language.getInstance().getString("WAHLREGELN7")+"</li></ul> </html>";
		
		JLabel initStateCaption= new JLabel(initCaption);
		initStateCaption.setBounds(marginLeft, 30, contentSmallGiant.getWidth()-marginLeft*2, contentSmallGiant.getHeight()/5);
//	initStateCaption.setOpaque(true);
//	initStateCaption.setBackground(Color.BLUE);
		initStateCaption.setVerticalAlignment(SwingConstants.TOP);
		initStateCaption.setFont(new Font("SansSerif", Font.PLAIN, (int)((double)(getScreenSize().width)/32)));
		initStateCaption.setForeground(foreground);
		initStateCaption.setVisible(true);
		initStateCaption.setFocusable(false);
		contentSmallGiant.add(initStateCaption);
		
		JLabel initStateLabel= new JLabel(initText);
		initStateLabel.setBounds(marginLeft, (int)(contentSmallGiant.getHeight()/5), contentSmallGiant.getWidth()-marginLeft*2, contentSmallGiant.getHeight()/2);
		initStateLabel.setVerticalAlignment(SwingConstants.TOP);
		initStateLabel.setFont(new Font("SansSerif", Font.PLAIN,(int)((double)(getScreenSize().width)*24/getScreenSize().width)));//1920)));
		initStateLabel.setForeground(foreground);
		initStateLabel.setVisible(true);
		initStateLabel.setFocusable(false);
		contentSmallGiant.add(initStateLabel);
		
		
		JRoundedButton beginVoting=new JRoundedButton(Language.getInstance().getString("WAHLREGELNBUTTON"), Color.WHITE, foreground, (int)((double)(getScreenSize().width)/60));
		int width=400;
		int height=100;
		int x=(int)((contentSmallGiant.getWidth()-width)/2);
		int y=(int)((contentSmallGiant.getHeight()-100-height));
		beginVoting.setBounds(x, y, width, height);
		beginVoting.setActionCommand("beginVoting");
		beginVoting.addActionListener(l);
		contentSmallGiant.add(beginVoting);
		
		
//		JRoundedButton langDe=new JRoundedButton("<html><img src=\"" + getClass().getClassLoader().getResource("de2.png") + "\" height=40 width=55></html>", Color.WHITE, Color.WHITE, (int)((double)(getScreenSize().width)/45));
//		 int width2=100;
//		 height=50;
//		 x=(int)((contentSmallGiant.getWidth())/2);
//		 y=(int)((contentSmallGiant.getHeight()-height-25));
//		 langDe.setBounds(x-10-width2, y, width2, height);
//		 langDe.setActionCommand("lang_de");
//		 langDe.addActionListener(l);
//		 langDe.addActionListener(new ActionListener() {
			
//   		public void actionPerformed(ActionEvent e) {
				//Language.getInstance().setLanguage("de");
				
//			}
//		});
//		contentSmallGiant.add(langDe);
		
//		JRoundedButton langEn=new JRoundedButton("<html><img src=\"" + getClass().getClassLoader().getResource("en2.png") + "\" height=40 width=55></html>", Color.WHITE, Color.WHITE, (int)((double)(getScreenSize().width)/45));
		
//		 langEn.setBounds(x+10, y, width2, height);
//		 langEn.setActionCommand("lang_en");
//		 langEn.addActionListener(l);
//		 langEn.addActionListener(new ActionListener() {
			
//			public void actionPerformed(ActionEvent e) {
				//Language.getInstance().setLanguage("en");
				
//			}
//		});
//		contentSmallGiant.add(langEn);
		
		setContentToOneParts();
		repaint();
		revalidate();
	}
	
/**
 * Status, dass der Stimmzettel nun gedruckt wird.
 * @param arg
 */
private void statePrint(Object arg){
	
		contentSmallGiant.removeAll();
		Color foreground= new Color(Integer.parseInt(prop.getProperty("neutralColor","0"),16));
		int marginLeft=(int)(contentSmallSmall.getSize().width*0.0404);
		
		String printText = Language.getInstance().getString("DRUCKEN");
		
		JLabel printStateLabel= new JLabel(printText);
		printStateLabel.setBounds(marginLeft, 30, contentSmallGiant.getWidth()-marginLeft*2, contentSmallGiant.getHeight()-300);
		printStateLabel.setVerticalAlignment(SwingConstants.TOP);
		printStateLabel.setFont(new Font("SansSerif", Font.PLAIN, (int)((double)(getScreenSize().width)*28/getScreenSize().width)));//1920)));
		printStateLabel.setForeground(foreground);
		printStateLabel.setVisible(true);
		printStateLabel.setFocusable(false);
		contentSmallGiant.add(printStateLabel);
		
		JRoundedButton endVoting=new JRoundedButton(Language.getInstance().getString("STIMMABGABEBEENDEN"), Color.WHITE, foreground ,(int)((double)(getScreenSize().width)/60));//45));
		int width=400;
		int height=100;
		int x=(int)((contentSmallGiant.getWidth()-width)/2);
		int y=(int)((contentSmallGiant.getHeight()-100-height));
		endVoting.setBounds(x, y, width, height);
		endVoting.setActionCommand("finished");
		endVoting.addActionListener(l);
		contentSmallGiant.add(endVoting);
		
		setContentToOneParts();
		repaint();
		revalidate();
	}
	
	/**
	 * Status ist die Votingoberfläche. Stimmauswahl kann erfolgen
	 * @param arg
	 */
	private void stateVoting(Object arg){
		setContentToThreeParts();
		if (arg instanceof BallotCard){
			BallotCard bc=(BallotCard)arg;
		
			
			calculateStatusText(bc);
			
			paintJMenuSmall(bc);
			try {
				paintContentSmallSmall_Voting(bc, bc.getSelectedParty());
				calculateStatusPopupMenu(bc);
				//System.out.println("MEVP sichtbar("+candSelected+"): "+mevp.isVisible());
			} catch (CandidateNotFoundException e) {
				//e.printStackTrace();
			} catch (PartyNotFoundException e) {
				//e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Legt den Status des PopUpMenüs zur Stimmauswahl fest.
	 * @param bc
	 * @throws CandidateNotFoundException
	 * @throws PartyNotFoundException
	 */
	private void calculateStatusPopupMenu(BallotCard bc) throws CandidateNotFoundException, PartyNotFoundException{
		boolean candSelected=(bc.getSelectedCandidate()!=-1);
		if(candSelected){
			mevp.setCandidate(bc.getParty(bc.getSelectedParty()).getCandidate(bc.getSelectedCandidate()), bc.getSelectedCandidateAutoDistributionForecast());
		}
		setComponentZOrder(mevp,0);
		//this is needed to get the correct order of the repaints, but don't ask why!
		mevp.setVisible(false);
		mevp.setVisible(candSelected);
	}
	
	
	/**
	 * Legt die Statustexte der oberen Zeile je nach Status des Modells fest.
	 * @param bc
	 */
	private void calculateStatusText(BallotCard bc){
		int maxStimmen=71;
		try {
			maxStimmen=BallotCardDesign.getInstance().getDesignValue(DesignKeys.MAXSTIMMEN);
		} catch (DesignKeyNotInXMLException e) {
			//e.printStackTrace();
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		//ArrayList<RejectReasons> rrs =bc.getFails();
		EnumSet<Validity> set = EnumSet.of(Validity.VALID_NO_PARTY,Validity.VALID_ONLY_PARTY, Validity.VALID_PARTY_AND_CANDIDATE, Validity.VALID_REDUCE_CANDIDATES, Validity.VALID_REDUCE_PARTIES);
		Validity status=bc.getValidity();
		// Über den Status kann eigentlich direkt was ausgegeben werden und muss hier nicht nochmal geprüft werden!
		String result="";
		if (set.contains(status)){
			if (status==Validity.VALID_REDUCE_CANDIDATES){
				result=Language.getInstance().getString("STATUS9");
				result=result.replaceFirst("XXX", ""+bc.countCandidatesManualVotes());
				result=result.replaceFirst("XXX", ""+maxStimmen);
				result=result.replaceFirst("XXX", ""+(bc.countCandidatesManualVotes()-maxStimmen));
				//result="Sie haben "+bc.countCandidatesVotes()+"/"+71+" Stimmen manuell vergeben. Sie werden beim Druck auf 71 reduziert.";
				statusBar.setWarningColor();
			}else if (status==Validity.VALID_REDUCE_PARTIES){
				result=Language.getInstance().getString("STATUS8");
				//result="Da Sie mehr als eine Party gewählt haben, zählen nur Ihre "+bc.countCandidatesVotes()+"/"+71+" direkten Stimmen.";
				statusBar.setWarningColor();
			}else{
			
			if (bc.countVotedParties()==0){
				result=Language.getInstance().getString("STATUS2");
				result=result.replaceFirst("XXX", ""+maxStimmen);
				result=result.replaceFirst("XXX", ""+bc.countCandidatesManualVotes());
				result=result.replaceFirst("XXX", ""+(maxStimmen-bc.countCandidatesManualVotes()));
				//result="An keine Partei wird eine Stimme verteilt. Sie haben "+bc.countCandidatesVotes()+"/"+71+" Stimmen manuell vergeben.";
			}else{
				if (bc.countCandidatesDistributedVotes()==1){
					result=Language.getInstance().getString("STATUS1");
					result=result.replaceFirst("XXX", ""+maxStimmen);
					result=result.replaceFirst("XXX", ""+bc.countCandidatesManualVotes());
					result=result.replaceFirst("XXX", ""+(bc.countCandidatesDistributedVotes()));
				}else{
					result=Language.getInstance().getString("STATUS1");
					result=result.replaceFirst("XXX", ""+maxStimmen);
					result=result.replaceFirst("XXX", ""+bc.countCandidatesManualVotes());
					result=result.replaceFirst("XXX", ""+(bc.countCandidatesDistributedVotes()));
				}
			}
			statusBar.setValidColor();
			}
		}else if (bc.getState()==BallotCard.State.INIT ){
			result=Language.getInstance().getString("STATUS0");
			statusBar.setNeutralColor();
		}else{
			if (status==Validity.INVALID_MANUAL){
				result=Language.getInstance().getString("STATUS6");
			}else if (status==Validity.INVALID_ONLY_PARTIES){
				result=Language.getInstance().getString("STATUS7");
			}else if (status==Validity.INVALID_TOOMUCHCANDIDATES){
				result=Language.getInstance().getString("STATUS5");
				result=result.replaceFirst("XXX", ""+maxStimmen);
			}else if (status==Validity.INVALID_EMPTY){
				result=Language.getInstance().getString("STATUS3");
			}else{
				result="Ihre Stimme ist aktuell ungültig.";//kommt nicht vor!
			}
			statusBar.setInvalidColor();
		}
		statusBar.setText(result);
	}
	
	/**
	 * Liefert die Texte für die Reviewansicht
	 * @param bc
	 * @return
	 */
	private String getReviewText(BallotCard bc){
		String ret="";
		if (bc.isValid()){
			if (bc.getValidity()==Validity.VALID_REDUCE_CANDIDATES){
				ret=Language.getInstance().getString("REVIEW1");
			}else if(bc.getValidity()==Validity.VALID_REDUCE_PARTIES){
				ret=Language.getInstance().getString("REVIEW2");
			}else{
				ret="<html>";
			}
			ret+="<br><br>"+Language.getInstance().getString("REVIEW3")+"</html>";
		}else{
			ret="<html>"+Language.getInstance().getString("REVIEW4")+"</html>";
		}
		return ret;
	}
	
	/**
	 * Erzeugt die Reviewansicht
	 */
	private void createContentSmallSmall_Review(){
		Color foreground= new Color(Integer.parseInt(prop.getProperty("neutralColor","0"),16));
		reviewPane= new JPanel();
		reviewPane.setLayout(null);
		reviewPane.setBounds(0, 0, contentSmallSmall.getWidth(), contentSmallSmall.getHeight());
		//reviewPane.setBackground(foreground);
		//reviewPane.setOpaque(true);
		reviewPane.setVisible(false);
		
		
		int imageWidth=Math.round((float)(reviewPane.getWidth()*0.7));//Math.round((float)(imageHeight/1.41));
		int imageHeight=Math.round((float)(imageWidth*1.41));
		if ((imageHeight-40)>reviewPane.getHeight()){
			imageHeight=(int)(reviewPane.getHeight()-40);
			imageWidth=Math.round((float)(imageHeight/1.41));
		}
		
		reviewText= new JLabel();
		//reviewText.setBackground(Color.GRAY);
		//reviewText.setOpaque(true);
		reviewText.setForeground(foreground);
		reviewText.setVerticalAlignment(SwingConstants.TOP);
		reviewText.setFont(new Font("SansSerif", Font.PLAIN, (int)((double)(getScreenSize().width)*24/getScreenSize().width)));//1920)));
		reviewText.setBounds(20, 20, reviewPane.getWidth()-imageWidth-60,imageHeight );
		reviewPane.add(reviewText);
		
		reviewImage= new JLabel();
		reviewImage.setBackground(Color.GRAY);
		reviewImage.setOpaque(true);
		reviewImage.setBorder(BorderFactory.createLineBorder(foreground));
		reviewImage.setBounds((reviewPane.getWidth()/2) - (imageWidth/2), 20, imageWidth,imageHeight );
		reviewPane.add(reviewImage);
		
		contentSmallSmall.add(reviewPane);
	}

	/**
	 * Erzeugt die Votingansicht
	 */
	private void createContentSmallSmall_Voting(){
		
		Color foreground= new Color(Integer.parseInt(prop.getProperty("neutralColor","0"),16));
		Color crossedColor= new Color(Integer.parseInt(prop.getProperty("invalidColor","0"),16));
		int marginLeft=(int)(contentSmallSmall.getSize().width*0.0404);
		
		//init label
		
		String initText =Language.getInstance().getString(""); //VOTINGINIT
		initLabel= new JLabel(initText);
		initLabel.setBounds(marginLeft, 15, contentSmallSmall.getWidth()-marginLeft*2, contentSmallSmall.getHeight()-30);
		initLabel.setVerticalAlignment(SwingConstants.TOP);
		initLabel.setFont(new Font("SansSerif", Font.PLAIN, (int)((double)(getScreenSize().width)*24/getScreenSize().width)));//1920)));
		initLabel.setForeground(foreground);
		initLabel.setVisible(false);
		initLabel.setFocusable(false);
		
		contentSmallSmall.add(initLabel);
		
		//create party representation
		//System.out.println("HEIGHT::::"+contentSmallSmall.getHeight());
		showp= new JShowAParty(contentSmallSmall.getSize().width-2*marginLeft,Math.round((float)(contentSmallSmall.getHeight()/16.4)), foreground);
		showp.setLocation(marginLeft,20);
		showp.setVisible(false);
		contentSmallSmall.add(showp);
		
		//create candidates representations
		int zeile=0;
		int spalte=0;
		int maxZeile=35;
		int maxSpalte=3;
		int platzFuerParteiHoehe=90;
		int untererRand=20;
		
		//macht die Anordnung in Zeilen und Spalten variabel, so dass sie ohne Quelltext angepasst werden können:
		PropertyHandler prop= new PropertyHandler("configuration.properties");
		maxZeile= Integer.parseInt(prop.getProperty("maxzeile", "35"));
		maxSpalte= Integer.parseInt(prop.getProperty("maxspalte", "3"));
		
		showc= new ShowACandiate[maxZeile*maxSpalte];
		
		
		int w=(int)((contentSmallSmall.getSize().width-4*marginLeft)/maxSpalte);
		int h=(int)((contentSmallSmall.getSize().height-platzFuerParteiHoehe-untererRand )/(maxZeile));
		
		for (int i=0;i<(maxZeile*maxSpalte);i++){
			
			zeile=(i)%maxZeile;
			spalte=i/maxZeile;
			
			showc[i]=new ShowACandiate(w, h, foreground,crossedColor,3);
			showc[i].setVisible(false);
			showc[i].setLocation(marginLeft+spalte*(showc[i].getSize().width+marginLeft),platzFuerParteiHoehe+(zeile*h));
			showc[i].addMouseListener(localMouseListener);
			contentSmallSmall.add(showc[i]);
		}
		
	}
	
	/**
	 * Legt den Text für die Infobox auf der rechten Seite fest
	 * @param bc
	 */
	private void setInfoBox(BallotCard bc){
		//System.out.println("State: "+state.toString());
		
		String infoHeadline=Language.getInstance().getString("INFOBOXinfoHeadline");;
		
		String initInfo="<html>"+Language.getInstance().getString("INFOBOXinforegeln")+"</html>";
		
		String votingInfo="<html>"+Language.getInstance().getString("INFOBOXvotingInfo1") +
				"<p></p>" + "<b>" +
				Language.getInstance().getString("INFOBOXvotingInfo2")+"</b>:<br>" +
				"<table><tr>" +
				"<td><img src=\"" + getClass().getClassLoader().getResource("buttonIconCandidateVoted.gif") + "\" height=\"25\"  width=\"25\"></td>" +
				"<td>"+Language.getInstance().getString("INFOBOXvotingInfo3")+"</td>" +
				"</tr><tr>" +
				"<td><img src=\"" + getClass().getClassLoader().getResource("ballotChecked.gif") + "\"  height=\"25\"  width=\"25\"></td>" +
				"<td>"+Language.getInstance().getString("INFOBOXvotingInfo4")+"</td>" +
				"</tr><tr>" +
				"<td><img src=\"" + getClass().getClassLoader().getResource("ballotCheckedGray.gif") + "\" height=\"25\"  width=\"25\"></td>" +
				"<td>"+Language.getInstance().getString("INFOBOXvotingInfo5")+"</td>" +
				"</tr></table>" +
				"</html>";
		String voteInvalidInfo="<html>"+Language.getInstance().getString("INFOBOXvoteInvalidInfo1") +
				"<br><br>" +
				Language.getInstance().getString("INFOBOXvotingInfo2")+":<br>" +
				"<table><tr>" +
				"<td><img src=\"" + getClass().getClassLoader().getResource("buttonIconCandidateVoted.gif") + "\" height=\"25\"  width=\"25\"></td>" +
				"<td>"+Language.getInstance().getString("INFOBOXvotingInfo3")+"</td>" +
				"</tr><tr>" +
				"<td><img src=\"" + getClass().getClassLoader().getResource("ballotChecked.gif") + "\"  height=\"25\"  width=\"25\"></td>" +
				"<td>"+Language.getInstance().getString("INFOBOXvotingInfo4")+"</td>" +
				"</tr><tr>" +
				"<td><img src=\"" + getClass().getClassLoader().getResource("ballotCheckedGray.gif") + "\" height=\"25\"  width=\"25\"></td>" +
				"<td>"+Language.getInstance().getString("INFOBOXvotingInfo5")+"</td>" +
				"</tr></table>" +
				"</html>";
		String reviewInfo="<html>"+Language.getInstance().getString("INFOBOXreview")+"</html>";
		String printInfo="<html></html>";
		String finishedInfo="";
		
		String retInfo="";
		switch (state) {
		case INIT:
			retInfo=initInfo;
			vdg.setSearchVisible(false);
			break;
		case VOTING:
			if (bc!=null && bc.getSelectedParty().equals("invalid")){
				retInfo=voteInvalidInfo;
			}else{
				retInfo=votingInfo;
			}
			vdg.setSearchVisible(true);
			break;
			
		case REVIEW:
			retInfo=reviewInfo;
			vdg.setSearchVisible(false);
			break;
		case PRINT:
			retInfo=printInfo;
			infoHeadline="";
			vdg.setSearchVisible(false);
			break;
		case FINALISED:
			retInfo=finishedInfo;
			vdg.setSearchVisible(false);
			
			break;
		default:
			//retInfo="";
			break;
		}
		vdg.setInfo(infoHeadline, retInfo);
		
		
	}
	
	
	/**
	 * Zeigt die Reviewansicht
	 * @param bc
	 */
	private void paintContentSmallSmall_Review(BallotCard bc){
		//hide all content
		
		initLabel.setVisible(false);
		showp.setParty(null);
		for (ShowACandiate sac : showc) {
			sac.setCandidate(null);
		}
		//show review
	//	reviewText.setText(getReviewText(bc));
		try {
			reviewImage.setIcon(bc.createBallotCardReview(reviewImage.getWidth(), reviewImage.getHeight()));
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		reviewPane.setVisible(true);
	}
	
	/**
	 * Malt die Wahloberfläche auf den Bildshcirm
	 * @param bc
	 * @param party
	 * @throws CandidateNotFoundException
	 * @throws PartyNotFoundException
	 */
	private void paintContentSmallSmall_Voting(BallotCard bc, String party)
			throws CandidateNotFoundException, PartyNotFoundException {
		reviewPane.setVisible(false);
		initLabel.setVisible(bc.getSelectedParty().equals(""));
		contentSmallSmall.setComponentZOrder(initLabel,
				contentSmallSmall.getComponentCount() - 1);
		contentSmallSmall.setComponentZOrder(reviewPane,
				contentSmallSmall.getComponentCount() - 1);

		//if (!bc.getSelectedParty().equals("")) {
			ArrayList<Candidate> candidates;
			if (party.equals("invalid")) {
				Party p = new Party(-1, "invalid", 0,0);
				p.setVoted(bc.isVoteManualInvalid());
				showp.setParty(p);
				candidates = new ArrayList<Candidate>();
			} else {
				if (bc.getSelectedParty().equals("")){
					showp.setParty(null);
					candidates = new ArrayList<Candidate>();
				}else{
				// party
					showp.setParty(bc.getParty(party));
					candidates = bc.getParty(party).getCandidates();
				}
				// candidates
				
			}
			Iterator<Candidate> it = candidates.iterator();
			for (ShowACandiate sac : showc) {
				if (it.hasNext()) {
					Candidate c = it.next();
					sac.setCandidate(c);
					// sac.setActionCommand(c.getId()+"");
					// sac.setVisible(true);
				} else {
					sac.setCandidate(null);
					// sac.setVisible(false);
				}
			}

		//}

		// repaint();

	}
	
	/**
	 * Erzeugt das PopUp zur Stimmenauswahl
	 */
	private void createJMunicicalElectionVotePopup(){
		Color foreground= new Color(Integer.parseInt(prop.getProperty("municipalElectionView.partyButton.foreground","0"),16));
        Color background= new Color(Integer.parseInt(prop.getProperty("municipalElectionView.partyButton.background","0"),16));
        Color invalidColor= new Color(Integer.parseInt(prop.getProperty("invalidColor","0"),16));
        
        mevp=new MunicipalElectionVotePopup(foreground,background,invalidColor, 3);
		mevp.setVisible(false);
		threeParts.add(mevp,0);
	}
	
	/**
	 * Erzeugt das Suchenfenster zum finden von Kandidaten mit Hilfe einer Bildschirmtastatur
	 */
	private void createSearchPopup(){
		Color foreground= new Color(Integer.parseInt(prop.getProperty("municipalElectionView.partyButton.foreground","0"),16));
        Color background= new Color(Integer.parseInt(prop.getProperty("municipalElectionView.partyButton.background","0"),16));
        
        Dimension screenSize = getScreenSize();
		
        osk=new OnScreenKeyboard(foreground,background,null, vdg.getWidth(), vdg.getHeight(), screenSize);
		osk.setVisible(false);
		threeParts.add(osk,0);
		int x=(screenSize.width-osk.getWidth())/2;
		int y=(screenSize.height-osk.getHeight())/2;
		osk.setLocation(x, y);
	}
	
	/**
	 * Erzeugt und zeichnet den Inhalt der Buttons auf der linken Seite
	 * @param bc
	 */
	private void paintJMenuSmall(BallotCard bc){
		Color foreground= new Color(Integer.parseInt(prop.getProperty("municipalElectionView.partyButton.foreground","0"),16));
        Color background= new Color(Integer.parseInt(prop.getProperty("municipalElectionView.partyButton.background","0"),16));
		
		if (state == State.VOTING) {
			jmenuSmall.removeAll();

			ArrayList<Party> pl = bc.getPartyList();
			for (Party p : pl) {
				jmenuSmall.add(Box.createRigidArea(new Dimension(10, 8)));
				Color colorF=foreground;
				Color colorB=background;
				
				if (p.getName().equals(bc.getSelectedParty())){
					colorF=background;
					colorB=foreground;
				}
				JRoundedPartyButton b;
				boolean manInvalid=false;
				if (bc.getValidity()==Validity.INVALID_MANUAL){
					manInvalid=true;
				}
				if (p.isVoted()) {
					if (manInvalid){
						b = new JRoundedPartyButton(p.getId(), p.getName(),
								colorF, colorB, null, (int)((double)(getScreenSize().width)*22/getScreenSize().width));//1920));
					}else{
					b = new JRoundedPartyButton(p.getId(), p.getName(),
							colorF, colorB, iconPartyVoted, (int)((double)(getScreenSize().width)*22/getScreenSize().width));//1920));
					}
				} else {
					
					if (p.countManualVotes() > 0) {
						if (manInvalid){
							b = new JRoundedPartyButton(p.getId(), p.getName(),
									colorF, colorB, null, (int)((double)(getScreenSize().width)*22/getScreenSize().width));//1920));
						}else{
						b = new JRoundedPartyButton(p.getId(), p.getName(),
								colorF, colorB, iconCandidateVoted, (int)((double)(getScreenSize().width)*22/getScreenSize().width));//1920));
						}
					} else {
						b = new JRoundedPartyButton(p.getId(), p.getName(),
								colorF, colorB, null, (int)((double)(getScreenSize().width)*22/getScreenSize().width));//1920));
					
					}
				}
				b.setBounds(0, 100, 100, 200);
				b.setSize(new Dimension(200, 100));
				b.setPreferredSize(new Dimension(jmenuSmall.getWidth() - 50, 50));
				b.setMaximumSize(new Dimension(jmenuSmall.getWidth() - 50, 100));
				b.addActionListener(l);
				b.setActionCommand(p.getName());
				jmenuSmall.add(b);


			}

			JRoundedButton b;
			jmenuSmall.add(Box.createVerticalGlue());
			if (bc.isVoteManualInvalid()) {
				b = new JRoundedPartyButton(-1, Language.getInstance().getString("BUTTONVOTINGungueltig"), foreground,
						background, iconPartyVoted, (int)((double)(getScreenSize().width)*22/getScreenSize().width));//1920));
			} else {
				b = new JRoundedPartyButton(-1, Language.getInstance().getString("BUTTONVOTINGungueltig"), foreground,
						background, null, (int)((double)(getScreenSize().width)*22/getScreenSize().width));//1920));
			}

			b.setMaximumSize(new Dimension(jmenuSmall.getWidth() - 50, 100));
			b.setPreferredSize(new Dimension(jmenuSmall.getWidth() - 50, 60));
			b.setActionCommand("voteInvalid");
			b.addActionListener(l);
			jmenuSmall.add(b);
			
			jmenuSmall.add(Box.createRigidArea(new Dimension(10, 8)));
			b = new JRoundedButton(Language.getInstance().getString("BUTTONVOTINGeingabeloeschen"), foreground, background, (int)((double)(getScreenSize().width)*22/getScreenSize().width));//1920));
			b.setMaximumSize(new Dimension(jmenuSmall.getWidth() - 50, 100));
			b.setPreferredSize(new Dimension(jmenuSmall.getWidth() - 50, 60));
			b.setActionCommand("reset");
			b.addActionListener(l);
			jmenuSmall.add(b);
			jmenuSmall.add(Box.createRigidArea(new Dimension(10, 8)));
			b = new JRoundedButton(Language.getInstance().getString("BUTTONVOTINGneustarten"), foreground, background, (int)((double)(getScreenSize().width)*22/getScreenSize().width));//1920));
			b.setMaximumSize(new Dimension(jmenuSmall.getWidth() - 50, 100));
			b.setPreferredSize(new Dimension(jmenuSmall.getWidth() - 50, 60));
			b.setActionCommand("close");
			b.addActionListener(l);
			jmenuSmall.add(b);
			jmenuSmall.add(Box.createRigidArea(new Dimension(10, 8)));
			b = new JRoundedButton(Language.getInstance().getString("BUTTONVOTINGstimmeansehen"),  background, foreground, (int)((double)(getScreenSize().width)*22/getScreenSize().width));//1920));
			b.setMaximumSize(new Dimension(jmenuSmall.getWidth() - 50, 150));
			b.setPreferredSize(new Dimension(jmenuSmall.getWidth() - 50, 100));
			b.setActionCommand("showVote");
			b.addActionListener(l);
			jmenuSmall.add(b);
			jmenuSmall.add(Box.createRigidArea(new Dimension(10, 25)));

		} else if (state == State.REVIEW) {
			//System.out.println("review aussehen");
			jmenuSmall.removeAll();
			
			
			
			jmenuSmall.add(Box.createRigidArea(new Dimension(10, 50)));
			jmenuSmall.add(Box.createVerticalGlue());
			JRoundedButton b = new JRoundedButton(Language.getInstance().getString("BUTTONVOTINGzurueck"), foreground,background, (int)((double)(getScreenSize().width)*22/getScreenSize().width));//1920));
			b.setPreferredSize(new Dimension(jmenuSmall.getWidth() - 50, 100));
			b.setMaximumSize(new Dimension(jmenuSmall.getWidth() - 50, 100));
			b.setActionCommand("backToVoting");
			b.addActionListener(l);
			jmenuSmall.add(b);
			jmenuSmall.add(Box.createRigidArea(new Dimension(10, 8)));
			
			b = new JRoundedButton(Language.getInstance().getString("BUTTONVOTINGstimmedrucken"),background, foreground, (int)((double)(getScreenSize().width)*22/getScreenSize().width));//1920));
			b.setPreferredSize(new Dimension(jmenuSmall.getWidth() - 50, 100));
			b.setMaximumSize(new Dimension(jmenuSmall.getWidth() - 50, 100));
			b.setActionCommand("printVote");
			b.addActionListener(l);
			jmenuSmall.add(b);
			jmenuSmall.add(Box.createRigidArea(new Dimension(10, 25)));
			
		}
		revalidate();
	}
	

	/* (non-Javadoc)
	 * @see de.tud.vcd.votedevice.view.VCDElectionContent#setListener(de.tud.vcd.votedevice.multielection.VCDListener)
	 */
	public void setListener(VCDListener l) throws CannotConvertListenerException {
		if (l instanceof MunicipalElectionListener){
			this.l=(MunicipalElectionListener)l;
			showp.addActionListener((MunicipalElectionListener)l);
			mevp.addActionListener((MunicipalElectionListener)l);
		}else{
			throw new CannotConvertListenerException(l, this.l);
		}
		
		
	}
	
	/**
	 * Sperrt das Wahlgerät, so dass es freigeschaltet werden muss.
	 * @param arg
	 */
	private void stateLocked(Object arg) {
		contentSmallGiant.removeAll();
		Color foreground= new Color(Integer.parseInt(prop.getProperty("neutralColor","0"),16));
		int marginLeft=(int)(contentSmallSmall.getSize().width*0.0404);
		//contentSmallGiant.add(new ShowACandiate(200, 100, foreground, Color.RED, 3));
		//init label
		
		String initCaption="<html>" +Language.getInstance().getString("SPERRE1")+"</html>";
		
		
		String initText = "<html>" +Language.getInstance().getString("SPERRE2")+"</html>";
		
		JLabel initStateCaption= new JLabel(initCaption);
		initStateCaption.setBounds(marginLeft, 30, contentSmallGiant.getWidth()-marginLeft*2, 200);
		initStateCaption.setVerticalAlignment(SwingConstants.TOP);
		
		
		initStateCaption.setFont(new Font("SansSerif", Font.PLAIN, (int)((double)(getScreenSize().width)/32) ));
		initStateCaption.setForeground(foreground);
		initStateCaption.setVisible(true);
		initStateCaption.setFocusable(false);
		contentSmallGiant.add(initStateCaption);
		
		JLabel initStateLabel= new JLabel(initText);
		initStateLabel.setBounds(marginLeft, 250, contentSmallGiant.getWidth()-marginLeft*2, contentSmallGiant.getHeight()-300-250);
		initStateLabel.setVerticalAlignment(SwingConstants.TOP);
		initStateLabel.setFont(new Font("SansSerif", Font.PLAIN, (int)((double)(getScreenSize().width)/60)));
		initStateLabel.setForeground(foreground);
		initStateLabel.setVisible(true);
		initStateLabel.setFocusable(false);
		contentSmallGiant.add(initStateLabel);
	
		setContentToOneParts();
		repaint();
		revalidate();
	}

}
