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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.tud.vcd.common.PropertyHandler;
import de.tud.vcd.votedevice.multielection.VCDListener;
import de.tud.vcd.votedevice.multielection.VCDView;
import de.tud.vcd.votedevice.multielection.exceptions.CannotConvertListenerException;
import de.tud.vcd.votedevice.municipalElection.model.exceptions.CandidateNotFoundException;
import de.tud.vcd.votedevice.municipalElection.model.exceptions.PartyNotFoundException;
import de.tud.vcd.votedevice.onscreenkeyboard.OnScreenKeyboard;
import de.tud.vcd.votedevice.tuTestElection.TUTestElectionModel.Validity;
import de.tud.vcd.votedevice.tuTestElection.TUTestElectionModel.VoteState;
import de.tud.vcd.votedevice.view.JRoundedButton;
import de.tud.vcd.votedevice.view.VCDElectionContent;
import de.tud.vcd.votedevice.view.VotingDeviceGui;

public class TUTestElectionView extends VCDElectionContent implements VCDView, Observer {
	public enum State {
		  INIT, VOTING, REVIEW, PRINT, FINALISED 
	}
	
	private State state;
	
	PropertyHandler prop;
	private ImageIcon iconPartyVoted;
	private ImageIcon iconCandidateVoted;
	MouseListener localMouseListener;
	JLabel initLabel;
	JPanel reviewPane;
	JLabel reviewImage;
	JLabel reviewText;
	OnScreenKeyboard osk;
	
	
	public TUTestElectionView(VotingDeviceGui vdg) {
		super(vdg);
		state=State.INIT;
		prop= new PropertyHandler("configuration.properties");
		//System.out.println("MunicipalElectionView created");
		
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
		
		//createJMunicicalElectionVotePopup();
		
		createSearchPopup();
	
		
		 //
		ActionListener localOpenSearchListener = new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				System.out.println("Open search");
				
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
				if ((c instanceof TUTestShowOption)&& c.isVisible() && state==State.VOTING)
				{

					//set position of the popup in the visible field
//					int newX=e.getXOnScreen();
//					if( !contentSmallSmall.contains(e.getXOnScreen() - locX+mevp.getWidth(), e.getYOnScreen() - locY)){
//						newX=contentSmallSmall.getWidth()-mevp.getWidth()+locX-5;
//					};
//					
//					int newY=e.getYOnScreen();
//					if( !contentSmallSmall.contains(e.getXOnScreen()- locX, e.getYOnScreen() - locY+mevp.getHeight())){
//						newY=contentSmallSmall.getHeight()-mevp.getHeight()+locY-5;
//					};
					TUTestShowOption ttso= (TUTestShowOption)c;
					
					
					l.actionPerformed(new ActionEvent(c, ActionEvent.ACTION_PERFORMED,ttso.getActionCommmand()));
					
					
//					System.out.println(((ShowACandiate) c)
//							.getActionCommmand());
					//mevp.feld=((ShowACandiate) c).getActionCommmand();
					
					
				}else{
						//l.actionPerformed(new ActionEvent(c, ActionEvent.ACTION_PERFORMED,"-1"));
					
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
	private ArrayList<TUTestShowOption> showp;

	TUTestElectionListener l;
	
	public void update(Observable o, Object arg) {
		System.out.println("MunicipalElectionView updated called");

		
		switch (state) {
		case INIT:
			stateInit(arg);
			
			break;
		case VOTING:
			stateVoting(arg);
			break;
		case REVIEW:
			stateReview(arg);
			break;
		case PRINT:
			statePrint(arg);
			break;
		case FINALISED:
			
			stateLocked(arg);
//			if (arg instanceof TUTestElectionModel){
//				vdg.stateLocked((TUTestElectionModel)arg);
//			}
			
			
//			setState(State.INIT);
//			if (arg instanceof TUTestElectionModel){
//				((TUTestElectionModel)arg).updateObserver();
//			}
			break;
		default:
			break;
		}
		TUTestElectionModel bc=null;
		if (arg instanceof TUTestElectionModel){
			bc=((TUTestElectionModel)arg);
		}
		setInfoBox(bc);
		

		revalidate();
	}
	
//	public void setPositionOfPopUpToCandidate(int id){
//		ShowACandiate sac_selected=null;
//		//Nach der entsprechenen Position suchen:
//		for(ShowACandiate sac : showc){
//			if (sac.getCandidateId()==id){
//				sac_selected=sac;
//				break;
//			}
//		}
//		//Wenn eine gefunden wurde hierzu das PopUp verschieben:
//		if (sac_selected!=null && sac_selected.isVisible()){
//			System.out.println(sac_selected.getX());
//			int xx=sac_selected.getX()+50;
//			int yy=sac_selected.getY()+10;
//			
//			//set position of the popup in the visible field
//			int locX = contentSmallSmall.getLocation().x;
//			int locY = contentSmallSmall.getLocation().y;	
//			
//			int newX=xx+locX;
//			if( !contentSmallSmall.contains(    newX-locX +mevp.getWidth()    , yy )){
//				newX=contentSmallSmall.getWidth()-mevp.getWidth()+locX-5;
//			};
//				
//			int newY=yy+locY;	
//			if( !contentSmallSmall.contains(xx, newY - locY+mevp.getHeight())){
//				newY=contentSmallSmall.getHeight()-mevp.getHeight()+locY-5;
//			};
//				
//			mevp.setLocation(newX,newY);
//				
//				
//				//l.actionPerformed(new ActionEvent(c, ActionEvent.ACTION_PERFORMED,((ShowACandiate)c).getActionCommmand()));
//				
//				
////				System.out.println(((ShowACandiate) c)
////						.getActionCommmand());
//				//mevp.feld=((ShowACandiate) c).getActionCommmand();
//				
//				
//			
//		}
//	}
	
	private void stateLocked(Object arg) {
		contentSmallGiant.removeAll();
		Color foreground= new Color(Integer.parseInt(prop.getProperty("neutralColor","0"),16));
		int marginLeft=(int)(contentSmallSmall.getSize().width*0.0404);
		//contentSmallGiant.add(new ShowACandiate(200, 100, foreground, Color.RED, 3));
		//init label
		
		String initCaption="<html>Wahlgerät ist gesperrt!</html>";
		
		
		String initText = "<html>" +
				"Bitte wenden Sie sich an den Wahlvorstand, um das Wahlgerät zu entsperren und mit der Stimmabgabe zu beginnen.</html>";
		
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
		
//		JRoundedButton beginVoting=new JRoundedButton("Stimmabgabe beginnen", Color.WHITE, foreground,(int)((double)(getScreenSize().width)/40) );
//		int width=400;
//		int height=100;
//		int x=(int)((contentSmallGiant.getWidth()-width)/2);
//		int y=(int)((contentSmallGiant.getHeight()-100-height));
//		beginVoting.setBounds(x, y, width, height);
//		beginVoting.setActionCommand("beginVoting");
//		beginVoting.addActionListener(l);
//		contentSmallGiant.add(beginVoting);
		
		setContentToOneParts();
		repaint();
		revalidate();
	}

	public void setState(State state){
		this.state=state;
	}

	private Dimension resolution=null;
	
	private Dimension getScreenSize(){
		if (resolution==null){
			
			PropertyHandler ph= new PropertyHandler("configuration.properties");
			int res_x=Integer.parseInt(ph.getProperty("RESOLUTION_X", "1920"));
			int res_y=Integer.parseInt(ph.getProperty("RESOLUTION_Y", "1080"));
			resolution= new Dimension(res_x, res_y);
			
		}
		return resolution;
	}
	
	
	private void stateReview(Object arg){
		setContentToThreeParts();
		if (arg instanceof TUTestElectionModel){
			paintJMenuSmall((TUTestElectionModel)(arg));
			paintContentSmallSmall_Review((TUTestElectionModel)(arg));
		}	
		
		repaint();
		revalidate();
	}
	
	private void stateInit(Object arg){
		/*
Herzlich willkommen zur Kommunalwahl 
des Landes Hessen 2012


Sie können alle 71 Stimmen an verschiedene Bewerberinnen und Bewerber in verschiedenen Wahlvorschlägen vergeben – panaschieren – und dabei jeder Person auf dem Stimmzettel bis zu drei Stimmen geben – kumulieren – (                oder                oder               ).

 Sie können, wenn Sie nicht alle 71 Stimmen einzeln vergeben wollen oder noch Stimmen übrig haben, zusätzlich einen Wahlvorschlag in der Kopfleiste kennzeichnen      . In diesem Fall hat das
Ankreuzen der Kopfleiste zur Folge, dass den Bewerberinnen und Bewerbern des betreffenden Wahlvorschlags in der Reihenfolge ihrer Benennung so lange eine weitere Stimme zugerechnet wird,
bis alle Stimmen verbraucht sind.

 Sie können einen Wahlvorschlag auch nur in der Kopfleiste kennzeichnen       , ohne Stimmen an Personen zu vergeben. Das hat zur Folge, dass jede Person in der Reihenfolge des
Wahlvorschlags so lange jeweils eine Stimme erhält, bis alle 31 Stimmen vergeben oder jeder Person des Wahlvorschlags drei Stimmen zugeteilt sind.

Falls Sie einen Wahlvorschlag in der Kopfleiste kennzeichnen, können Sie auch Bewerberinnen und Bewerber in diesem Wahlvorschlag streichen; diesen Personen werden keine Stimmen zugeteilt.

		 */
		contentSmallGiant.removeAll();
		Color foreground= new Color(Integer.parseInt(prop.getProperty("neutralColor","0"),16));
		int marginLeft=(int)(contentSmallSmall.getSize().width*0.0404);
		//contentSmallGiant.add(new ShowACandiate(200, 100, foreground, Color.RED, 3));
		//init label
		
		String initCaption="<html>Herzlich willkommen zur elektronischen Abstimmung zum Thema:</html>";
		
		/*
		 * Sind Sie grundsätzlich mit Ihrem …
· O-Phasen-Tutor (neue Studenten)
· Mentor (nur Inf. Studenten)
· BA-Betreuer (Studenten)
· MA-Betreuer (Studenten)
· PhD-Betreuer (Doktoranden)
· Vorgesetzten (Mitarbeiter)
· Präsidenten (Professoren) 
… zufrieden?
		 */
		String initText = "<html><b>Sind Sie grundsätzlich mit Ihrem...</b><ul>" +
				"<li>&nbsp;&nbsp;&nbsp;O-Phasen-Tutor (neue Studenten)"+
				"<li>&nbsp;&nbsp;&nbsp;Mentor (nur Inf. Studenten)"+
				"<li>&nbsp;&nbsp;&nbsp;BA-Betreuer (Studenten)"+
				"<li>&nbsp;&nbsp;&nbsp;MA-Betreuer (Studenten)"+
				"<li>&nbsp;&nbsp;&nbsp;PhD-Betreuer (Doktoranden)"+
				"<li>&nbsp;&nbsp;&nbsp;Vorgesetzten (Mitarbeiter)"+
				"<li>&nbsp;&nbsp;&nbsp;Präsidenten (Professoren) "+
				"</ul><b>...zufrieden?</b>"+
				"<br><br><br><br><b>Wahlregel:</b><br>" +
				"<ul><li>Um eine gültige Stimme abzugeben, können<br> Sie genau eine Antwort auswählen."+
				"</li></ul> </html>";
		
		JLabel initStateCaption= new JLabel(initCaption);
		initStateCaption.setBounds(marginLeft, 30, contentSmallGiant.getWidth()-marginLeft*2, 200);
		initStateCaption.setVerticalAlignment(SwingConstants.TOP);
		
		
		initStateCaption.setFont(new Font("SansSerif", Font.PLAIN, (int)((double)(getScreenSize().width)/32) ));
		initStateCaption.setForeground(foreground);
		initStateCaption.setVisible(true);
		initStateCaption.setFocusable(false);
		contentSmallGiant.add(initStateCaption);
		
		JLabel initStateLabel= new JLabel(initText);
		initStateLabel.setBounds(marginLeft+200, 150, contentSmallGiant.getWidth()-marginLeft*2-200, contentSmallGiant.getHeight()-300-150);
		initStateLabel.setVerticalAlignment(SwingConstants.TOP);
		initStateLabel.setFont(new Font("SansSerif", Font.PLAIN, (int)((double)(getScreenSize().width)/70)));
		initStateLabel.setForeground(foreground);
		initStateLabel.setVisible(true);
		initStateLabel.setFocusable(false);
		contentSmallGiant.add(initStateLabel);
		
		JRoundedButton beginVoting=new JRoundedButton("Stimmabgabe beginnen", Color.WHITE, foreground,(int)((double)(getScreenSize().width)/40) );
		int width=400;
		int height=100;
		int x=(int)((contentSmallGiant.getWidth()-width)/2);
		int y=(int)((contentSmallGiant.getHeight()-100-height));
		beginVoting.setBounds(x, y, width, height);
		beginVoting.setActionCommand("beginVoting");
		beginVoting.addActionListener(l);
		contentSmallGiant.add(beginVoting);
		
		setContentToOneParts();
		repaint();
		revalidate();
	}
	
private void statePrint(Object arg){
	/*
	Der Stimmzettel wird gedruckt….
	Nehmen Sie anschließend Ihre Stimme aus dem Drucker.
	Vergewissern Sie sich, dass die gedruckte Stimme Ihrer Wahlabsicht entspricht.
	Ist dies der Fall, dann werfen Sie diesen in die Wahlurne ein, um Ihre Stimme abzugeben.

	Beinhaltet der Stimmzettel nicht Ihre Wahlabsicht, so wenden Sie sich an einen Wahlhelfer, um diese Stimme zu entwerten. Sie erhalten daraufhin die Möglichkeit eine neue Stimme abzugeben.
	Drücken Sie bevor Sie die Wahlkabine verlassen den Knopf „Stimmabgabe beenden“.
*/
		contentSmallGiant.removeAll();
		Color foreground= new Color(Integer.parseInt(prop.getProperty("neutralColor","0"),16));
		int marginLeft=(int)(contentSmallSmall.getSize().width*0.0404);
		//contentSmallGiant.add(new ShowACandiate(200, 100, foreground, Color.RED, 3));
		//init label
		
		String printText = "<html><font size=\"+2\"><b>Der Stimmzettel wird gedruckt...</b></font><br><br>" +
				"<b>Dieser Vorgang wird ein wenig dauern.</b><br><br>" +
				"" +
				"	Drücken Sie auf \"Stimmabgabe beenden\", bevor Sie die Wahlkabine verlassen.</html>";
		
		JLabel printStateLabel= new JLabel(printText);
		printStateLabel.setBounds(marginLeft, 30, contentSmallGiant.getWidth()-marginLeft*2, contentSmallGiant.getHeight()-300);
		printStateLabel.setVerticalAlignment(SwingConstants.TOP);
		printStateLabel.setFont(new Font("SansSerif", Font.PLAIN,  (int)((double)(getScreenSize().width)/1920*28)));
		printStateLabel.setForeground(foreground);
		printStateLabel.setVisible(true);
		//printStateLabel.setEnabled(false);
		printStateLabel.setFocusable(false);
		//printStateLabel.setOpaque(true);
		//printStateLabel.setBackground(Color.BLUE);
		//printStateLabel.setMaximumSize(new Dimension(200,200));
		//printStateLabel.setPreferredSize(new Dimension(200,200));
		contentSmallGiant.add(printStateLabel);
		
		JRoundedButton endVoting=new JRoundedButton("Stimmabgabe beenden", Color.WHITE, foreground, (int)((double)(getScreenSize().width)/40));
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
	
	private void stateVoting(Object arg){
		setContentToThreeParts();
		if (arg instanceof TUTestElectionModel){
			TUTestElectionModel bc=(TUTestElectionModel)arg;
		
			
			calculateStatusText(bc);
			
			paintJMenuSmall(bc);
			try {
				paintContentSmallSmall_Voting(bc, bc.getVoteStates());
				//calculateStatusPopupMenu(bc);
				//System.out.println("MEVP sichtbar("+candSelected+"): "+mevp.isVisible());
			} catch (CandidateNotFoundException e) {
				//e.printStackTrace();
			} catch (PartyNotFoundException e) {
				//e.printStackTrace();
			}
		}
	}
	
	
//	private void calculateStatusPopupMenu(BallotCard bc) throws CandidateNotFoundException, PartyNotFoundException{
//		boolean candSelected=(bc.getSelectedCandidate()!=-1);
//		if(candSelected){
//			mevp.setCandidate(bc.getParty(bc.getSelectedParty()).getCandidate(bc.getSelectedCandidate()), bc.getSelectedCandidateAutoDistributionForecast());
//		}
//		setComponentZOrder(mevp,0);
//		//this is needed to get the correct order of the repaints, but don't ask why!
//		mevp.setVisible(false);
//		mevp.setVisible(candSelected);
//	}
	
	
	private void calculateStatusText(TUTestElectionModel bc){
		Validity status=bc.getValidity();
		String result="";
		if (bc.getState().equals(TUTestElectionModel.State.INIT)){
			result="Sie haben bisher noch keine Stimme vergeben.";
			statusBar.setNeutralColor();
		}else if (status.equals(Validity.VALID)){
			result="Ihre Stimme ist momentan gültig.";
			statusBar.setValidColor();
		}else if (status.equals(Validity.INVALID_EMPTY)){
			result="Ungültig, da keine Stimme vergeben wurde.";
			statusBar.setInvalidColor();
		}else if (status.equals(Validity.INVALID_MANUALINVALID)){
			result="Sie haben Ihre Stimme aktuell als ungültig markiert.";
			statusBar.setInvalidColor();
		}else if (status.equals(Validity.INVALID_TOOMUCH)){
			result="Sie haben mehr als ein Kreuz gemacht. Es ist jedoch nur eins erlaubt.";
			statusBar.setInvalidColor();
		}else {
			result="Unbekannter Status";
			statusBar.setWarningColor();
		}
		
		

		statusBar.setText(result);
		//repaint();
	}
	
	private String getReviewText(TUTestElectionModel arg){
		String ret="";
		if (arg.isValid()){
			ret="<html>";
			ret+="<br><br>Kontrollieren Sie Ihre Stimme. Ist sie korrekt, so drücken Sie „Stimme drucken“. " +
					"Bei „Zurück“ können Sie Ihre Stimme weiter bearbeiten.</html>";
		}else{
			ret="<html>Ihre Stimme ist ungültig. Wenn Sie dies beabsichtigen, " +
					"dann drucken Sie den Stimmzettel nun aus. Ansonsten klicken Sie auf „zurück“ und ändern Sie Ihre Stimme. " +
					"</html>";
		}
		return ret;
	}
	
	private void createContentSmallSmall_Review(){
		Color foreground= new Color(Integer.parseInt(prop.getProperty("neutralColor","0"),16));
		reviewPane= new JPanel();
		reviewPane.setLayout(null);
		reviewPane.setBounds(0, 0, contentSmallSmall.getWidth(), contentSmallSmall.getHeight());
		//reviewPane.setBackground(foreground);
		//reviewPane.setOpaque(true);
		reviewPane.setVisible(false);
		
		int imageWidth=(int)(reviewPane.getWidth()*0.7);
		int imageHeight=(int)(imageWidth*1.41);
		
		reviewText= new JLabel();
		//reviewText.setBackground(Color.GRAY);
		//reviewText.setOpaque(true);
		reviewText.setForeground(foreground);
		reviewText.setVerticalAlignment(SwingConstants.TOP);
		reviewText.setFont(new Font("SansSerif", Font.PLAIN, 20));
		reviewText.setBounds(20, 20, reviewPane.getWidth()-imageWidth,imageHeight );
		//reviewPane.add(reviewText);
		
		reviewImage= new JLabel();
		reviewImage.setBackground(Color.GRAY);
		reviewImage.setOpaque(true);
		reviewImage.setBorder(BorderFactory.createLineBorder(foreground));
		reviewImage.setBounds((reviewPane.getWidth()-imageWidth)/2, 20, imageWidth,imageHeight );
		reviewPane.add(reviewImage);
		
		
	
		contentSmallSmall.add(reviewPane);
	}

	private void createContentSmallSmall_Voting(){
		Color foreground= new Color(Integer.parseInt(prop.getProperty("neutralColor","0"),16));
		//Color crossedColor= new Color(Integer.parseInt(prop.getProperty("invalidColor","0"),16));
		int marginLeft=(int)(contentSmallSmall.getSize().width*0.0404);
		
		//init label
		
		String initText = "<html><b>Sind Sie grundsätzlich mit Ihrem...</b><ul>" +
				"<li>&nbsp;&nbsp;&nbsp;O-Phasen-Tutor (neue Studenten)"+
				"<li>&nbsp;&nbsp;&nbsp;Mentor (nur Inf. Studenten)"+
				"<li>&nbsp;&nbsp;&nbsp;BA-Betreuer (Studenten)"+
				"<li>&nbsp;&nbsp;&nbsp;MA-Betreuer (Studenten)"+
				"<li>&nbsp;&nbsp;&nbsp;PhD-Betreuer (Doktoranden)"+
				"<li>&nbsp;&nbsp;&nbsp;Vorgesetzten (Mitarbeiter)"+
				"<li>&nbsp;&nbsp;&nbsp;Präsidenten (Professoren) "+
				"</ul><b>...zufrieden?</b>"+
				" </html>";
		
//		String initText = "<html>";
//		initText += "Sind Sie grundsätzlich mit Ihrem <br>Tutor/Mentor/BA/MA/Promotions-Betreuer bzw. Vorgesetzten zufrieden?";
//		initText+="</html>";
		
		initLabel= new JLabel(initText);
		
//		initLabel.setOpaque(true);
//		initLabel.setBackground(Color.GREEN);
		initLabel.setBounds(marginLeft, 15, contentSmallSmall.getWidth()-marginLeft*2, 220);
		initLabel.setVerticalAlignment(SwingConstants.TOP);
		initLabel.setHorizontalAlignment(JLabel.CENTER);
		initLabel.setFont(new Font("SansSerif", Font.PLAIN, (int)((double)(getScreenSize().width)/70)));
		initLabel.setForeground(foreground);
		initLabel.setVisible(true);
		initLabel.setFocusable(false);
		
		contentSmallSmall.add(initLabel);
		showp= new ArrayList<TUTestShowOption>();
		//create party representation
		VoteState[] vsA= VoteState.values();
		int a_size=vsA.length;
		int width=(int)(contentSmallSmall.getSize().width/1.4);
		int y= (contentSmallSmall.getSize().width-width)/2;
		for (int i=0;i<a_size;i++){
			TUTestShowOption option= new TUTestShowOption(y, 250+79*i,width ,80, foreground, vsA[i]);
			option.addMouseListener(localMouseListener);
			showp.add(option);
			contentSmallSmall.add(option);
		}
		
		
		
		
	}
	
	private void setInfoBox(TUTestElectionModel bc){
		System.out.println("State: "+state.toString());
		
		String infoHeadline="Info:";
		
		String initInfo="<html>Lesen Sie sich die Wahlregeln aufmerksam durch. Beginnen Sie anschließend die Stimmabgabe " +
				"indem Sie auf \"Stimmabgabe beginnen\" drücken.</html>";
		
		String votingInfo="<html>Wählen Sie links Ihre gewünschte Meinung aus." +
				"<br><br>" +
				"Legende:<br>" +
				"<table><tr>" +
				"<td><img src=\"" + getClass().getClassLoader().getResource("ballotUnchecked.gif") + "\" height=\"25\"  width=\"25\"></td>" +
				"<td>Diese Auswahl trifft nicht zu.</td>" +
				"</tr><tr>" +
				"<td><img src=\"" + getClass().getClassLoader().getResource("ballotChecked.gif") + "\"  height=\"25\"  width=\"25\"></td>" +
				"<td>Diese Auswahl trifft zu.</td>" +
				"</tr></table>" +
				"</html>";
		String voteInvalidInfo="<html>Hier können Sie Ihre Auswahl treffen." +
				"<br><br>" +
				"Legende:<br>" +
				"<table><tr>" +
				"<td><img src=\"" + getClass().getClassLoader().getResource("ballotUnchecked.gif") + "\" height=\"25\"  width=\"25\"></td>" +
				"<td>Diese Meinung trifft nicht zu.</td>" +
				"</tr><tr>" +
				"<td><img src=\"" + getClass().getClassLoader().getResource("ballotChecked.gif") + "\"  height=\"25\"  width=\"25\"></td>" +
				"<td>Diese Meinung trifft zu.</td>" +
				"</tr></table>" +
				"</html>";
		String reviewInfo="<html>Kontrollieren Sie die Vorschau auf Korrektheit. Sollten " +
				"Sie einen Fehler entdecken, so gehen Sie zurück, um Ihre Eingabe zu " +
				"korrigieren. Ansonsten drucken Sie die Stimme." +
				"<br><br>" +
				"Der QR-Code enthält die gleichen Informationen wie der Stimmzettel und dient der schnelleren Auszählung.</html>";
		String printInfo="<html></html>";
		String finishedInfo="<html>Das Wahlgerät muss von einem Wahlhelfer für die erneute Eingabe freigeschaltet werden.</html>";
		
		String retInfo="";
		switch (state) {
		case INIT:
			retInfo=initInfo;
			vdg.setSearchVisible(false);
			break;
		case VOTING:
			if (bc!=null && !bc.isValid()){
				retInfo=voteInvalidInfo;
			}else{
				retInfo=votingInfo;
			}
			vdg.setSearchVisible(false);
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
	
	
	private void paintContentSmallSmall_Review(TUTestElectionModel arg){
		//hide all content
		
		initLabel.setVisible(false);
//		showp.setParty(null);
		for (TUTestShowOption sac : showp) {
			sac.setVisible(false);
		}
		//show review
		reviewText.setText(getReviewText(arg));
		//IBallotCardImageCreator bcic;
		try {
			
			//bcic = new BallotCardImageCreatorNewDesign(reviewImage.getWidth(), reviewImage.getHeight(), bc);
			reviewImage.setIcon(arg.createBallotCardReview(reviewImage.getWidth(), reviewImage.getHeight()));
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		reviewPane.setVisible(true);
	}
	
	private void paintContentSmallSmall_Voting(TUTestElectionModel bc, ArrayList<VoteState> voteState)
			throws CandidateNotFoundException, PartyNotFoundException {
		reviewPane.setVisible(false);
		//initLabel.setVisible(bc.getSelectedParty().equals(""));
		contentSmallSmall.setComponentZOrder(initLabel,
				contentSmallSmall.getComponentCount() - 1);
		contentSmallSmall.setComponentZOrder(reviewPane,
				contentSmallSmall.getComponentCount() - 1);
		initLabel.setVisible(true);
//System.out.println("Hier sollte es noch ankommen");
		for (TUTestShowOption ttso: showp){
			ttso.setVisible(true);
			if (bc.getVoteStates().contains(ttso.getVoteState()))
				ttso.setVoteState(ttso.getVoteState(), bc.getVoteStateText(ttso.getVoteState()), true);
			else{
				ttso.setVoteState(ttso.getVoteState(), bc.getVoteStateText(ttso.getVoteState()), false);
			}
		}
		
		
		
		
		//if (!bc.getSelectedParty().equals("")) {
			//ArrayList<Candidate> candidates;
//			if (party.equals("invalid")) {
//				Party p = new Party(-1, "invalid", 0,0);
//				p.setVoted(bc.isVoteManualInvalid());
//				showp.setParty(p);
//				candidates = new ArrayList<Candidate>();
//			} else {
//				if (bc.getSelectedParty().equals("")){
//					showp.setParty(null);
//					candidates = new ArrayList<Candidate>();
//				}else{
//				// party
//					showp.setParty(bc.getParty(party));
//					candidates = bc.getParty(party).getCandidates();
//				}
//				// candidates
//				
//			}
//			Iterator<Candidate> it = candidates.iterator();
//			for (ShowACandiate sac : showc) {
//				if (it.hasNext()) {
//					Candidate c = it.next();
//					sac.setCandidate(c);
//					// sac.setActionCommand(c.getId()+"");
//					// sac.setVisible(true);
//				} else {
//					sac.setCandidate(null);
//					// sac.setVisible(false);
//				}
//			}

		//}

		// repaint();

	}
	
	
	private void createSearchPopup(){
		Color foreground= new Color(Integer.parseInt(prop.getProperty("municipalElectionView.partyButton.foreground","0"),16));
        Color background= new Color(Integer.parseInt(prop.getProperty("municipalElectionView.partyButton.background","0"),16));
        
        osk=new OnScreenKeyboard(foreground,background,null, vdg.getWidth(), vdg.getHeight(),Toolkit.getDefaultToolkit().getScreenSize());
		osk.setVisible(false);
		threeParts.add(osk,0);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x=(screenSize.width-osk.getWidth())/2;
		int y=(screenSize.height-osk.getHeight())/2;
		osk.setLocation(x, y);
	}
	
	private void paintJMenuSmall(TUTestElectionModel bc){
		Color foreground= new Color(Integer.parseInt(prop.getProperty("municipalElectionView.partyButton.foreground","0"),16));
        Color background= new Color(Integer.parseInt(prop.getProperty("municipalElectionView.partyButton.background","0"),16));
		
		if (state == State.VOTING) {
			jmenuSmall.removeAll();
			jmenuSmall.add(Box.createVerticalGlue());

			JRoundedButton b;

			
			b = new JRoundedButton("<html> &lt;&lt;&lt; Zurück</html>", foreground, background,16);
			b.setPreferredSize(new Dimension(jmenuSmall.getWidth() - 50, 100));
			b.setMaximumSize(new Dimension(jmenuSmall.getWidth() - 50, 100));
			b.setActionCommand("close");
			b.addActionListener(l);
			jmenuSmall.add(b);
			jmenuSmall.add(Box.createRigidArea(new Dimension(10, 8)));
			b = new JRoundedButton("<html>Eingaben<br> löschen</html>", foreground, background,16);
			b.setPreferredSize(new Dimension(jmenuSmall.getWidth() - 50, 100));
			b.setMaximumSize(new Dimension(jmenuSmall.getWidth() - 50, 100));
			b.setActionCommand("reset");
			b.addActionListener(l);
			jmenuSmall.add(b);
			jmenuSmall.add(Box.createRigidArea(new Dimension(10, 8)));
			jmenuSmall.add(Box.createRigidArea(new Dimension(10, 8)));
			b = new JRoundedButton("<html>Weiter &gt;&gt;&gt;</html>",  background, foreground,16);
			b.setPreferredSize(new Dimension(jmenuSmall.getWidth() - 50, 100));
			b.setMaximumSize(new Dimension(jmenuSmall.getWidth() - 50, 150));
			b.setActionCommand("showVote");
			b.addActionListener(l);
			
			jmenuSmall.add(b);
			jmenuSmall.add(Box.createRigidArea(new Dimension(10, 50)));

		} else if (state == State.REVIEW) {
			System.out.println("review aussehen");
			jmenuSmall.removeAll();
			
			jmenuSmall.add(Box.createVerticalGlue());
			
			jmenuSmall.add(Box.createRigidArea(new Dimension(10, 50)));
			
			JRoundedButton b = new JRoundedButton("<<< Zurück", foreground,background, 16);
				b.setPreferredSize(new Dimension(jmenuSmall.getWidth() - 50, 100));
				b.setMaximumSize(new Dimension(jmenuSmall.getWidth() - 50, 100));
				b.setActionCommand("backToVoting");
				b.addActionListener(l);
			jmenuSmall.add(b);
			jmenuSmall.add(Box.createRigidArea(new Dimension(10, 8)));
			 b = new JRoundedButton("<html>Stimme <br>drucken&gt;&gt;&gt;</html>",background, foreground,16);
			b.setPreferredSize(new Dimension(jmenuSmall.getWidth() - 50, 100));
			b.setMaximumSize(new Dimension(jmenuSmall.getWidth() - 50, 100));
			b.setActionCommand("printVote");
			b.addActionListener(l);
			
			jmenuSmall.add(b);
			
			
			
			jmenuSmall.add(Box.createRigidArea(new Dimension(10, 50)));
			//jmenuSmall.add(Box.createVerticalGlue());
		}
		revalidate();
		repaint();
	}
	

	public void setListener(VCDListener l) throws CannotConvertListenerException {
		if (l instanceof TUTestElectionListener){
			this.l=(TUTestElectionListener)l;
//			showp.addActionListener((MunicipalElectionListener)l);
//			mevp.addActionListener((MunicipalElectionListener)l);
		}else{
			throw new CannotConvertListenerException(l, this.l);
		}
		
		
	}

}
