package de.tud.vcd.votedevice.municipalElection.controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ImageIcon;

import de.tud.vcd.common.BallotCardDesign;
import de.tud.vcd.votedevice.controller.Language;
import de.tud.vcd.votedevice.controller.VCDPrintJob;
import de.tud.vcd.votedevice.controller.VotingDeviceController;
import de.tud.vcd.votedevice.model.IBallotCardImageCreator;
import de.tud.vcd.votedevice.multielection.ElectionContainer;
import de.tud.vcd.votedevice.multielection.VCDListener;
import de.tud.vcd.votedevice.multielection.VCDModel;
import de.tud.vcd.votedevice.multielection.exceptions.CannotCastModelException;
import de.tud.vcd.votedevice.municipalElection.model.BallotCard;
import de.tud.vcd.votedevice.municipalElection.model.BallotCardImageCreatorNewDesign;
import de.tud.vcd.votedevice.municipalElection.model.Candidate;
import de.tud.vcd.votedevice.municipalElection.model.Party;
import de.tud.vcd.votedevice.municipalElection.model.BallotCard.Validity;
import de.tud.vcd.votedevice.municipalElection.model.exceptions.CandidateNotFoundException;
import de.tud.vcd.votedevice.municipalElection.model.exceptions.PartyNotFoundException;
import de.tud.vcd.votedevice.municipalElection.view.JShowAParty;
import de.tud.vcd.votedevice.municipalElection.view.MunicipalElectionView;
import de.tud.vcd.votedevice.municipalElection.view.MunicipalElectionVotePopup;
import de.tud.vcd.votedevice.municipalElection.view.ShowACandiate;
import de.tud.vcd.votedevice.municipalElection.view.MunicipalElectionView.State;
import de.tud.vcd.votedevice.onscreenkeyboard.OnScreenKeyboard;
import de.tud.vcd.votedevice.onscreenkeyboard.VCDSearchable;
import de.tud.vcd.votedevice.timeofvoting.VotingTime;
import de.tud.vcd.votedevice.view.JRoundedButton;
import de.tud.vcd.votedevice.view.JRoundedPartyButton;
import de.tud.vcd.votedevice.votecastingmanipulation.PrintoutManipulation;

/**
 * Controller der Kommunalwahl. Hier[ber werden die verschiedenen Zustände der Oberfläche festgelegt und was der Wähler
 * grade  können soll.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class MunicipalElectionListener implements VCDListener, ActionListener {

	BallotCard bc;
	ElectionContainer ec;
	
	//Juri: Zeitmessen
	long before;
    // some amazing blocking function
	long after;
	long runningTime;
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		//set a selected candidate to show the correct popup menu
		if (e.getSource() instanceof ShowACandiate){
			//bc.setPartyVoted(e.getActionCommand(), true);
			int candId;
			try{
				candId=Integer.parseInt(e.getActionCommand());
			}catch(NumberFormatException ex){
				candId=-1;
			}
			bc.setSelectedCandidate(candId);
		}
		
		//vote a party
		if (e.getSource() instanceof JShowAParty){
			try {
				if (e.getActionCommand().equals("invalid")){
					bc.setManualVoteInvalid(!bc.isVoteManualInvalid());
				}else{
					bc.setSelectedPartyVoted( !(bc.getParty(bc.getSelectedParty()).isVoted()) );
				}
				bc.setSelectedCandidate(-1);
			} catch (PartyNotFoundException e1) {
				//e1.printStackTrace();
			}
		}
		
		// set selected party for further actions
		if (e.getSource() instanceof JRoundedPartyButton) {
			if (e.getActionCommand().equals("voteInvalid")) {
				bc.setSelectedParty("invalid");
			} else {
				// bc.setPartyVoted(e.getActionCommand(), true);
				bc.setSelectedParty(e.getActionCommand());
				bc.setSelectedCandidate(-1);
			}
		}

		//Ereignis ist ein Suchobjekt
		if (e.getSource() instanceof VCDSearchable){
			System.out.println("Suche wurde durchgeführt!!!!");
			VCDSearchable s= (VCDSearchable)e.getSource();
			if (s instanceof Candidate){
				System.out.println(e.getActionCommand());
				bc.setSelectedParty(e.getActionCommand());
				bc.setSelectedCandidate(((Candidate) s).getId());
				((MunicipalElectionView)ec.v).setPositionOfPopUpToCandidate(((Candidate) s).getId());
			}else if(s instanceof Party){
				bc.setSelectedParty(((Party) s).getName());
			}
		}
		//Ereignis ist eine Tasteneingabe der Bildschirmtastatur
		if (e.getSource() instanceof OnScreenKeyboard){
			((OnScreenKeyboard)e.getSource()).setModel(bc);
			((OnScreenKeyboard)e.getSource()).addActionListener(this);
			bc.setSelectedCandidate(-1);
		}
		// call command at the model, this is a candidate voting command
		try {
			if (e.getSource() instanceof MunicipalElectionVotePopup) {
				if (e.getActionCommand().equals("close")) {
					bc.setSelectedCandidate(-1);
				} else if (e.getActionCommand().equals("crossed")) {
					bc.setSelectedCandidateCrossed();
//				} else if (e.getActionCommand().equals("autodist")) {
//					bc.setSelectedCandidateAutoDistribution();
				}else if (e.getActionCommand().startsWith("vote_")){
					String strVotes=e.getActionCommand().substring(5);
					bc.setSelectedCandidateVote(Integer.parseInt(strVotes));
				}
				bc.setSelectedCandidate(-1);
			}
		} catch (PartyNotFoundException e1) {
			//e1.printStackTrace();
		} catch (CandidateNotFoundException e1) {
			//e1.printStackTrace();
		}

		if (e.getSource() instanceof JRoundedButton){
			if (e.getActionCommand().equals("beginVoting") && (ec.v instanceof MunicipalElectionView)){	
				((MunicipalElectionView)ec.v).setState(State.VOTING);
				//Juri: startzeit
				before = System.currentTimeMillis();
				
				ec.getStatusSignaling().setRed();
				bc.updateObserver();
			}else if (e.getActionCommand().equals("showVote")){
				System.out.println("showVote");
				setState(State.REVIEW);
			}else if (e.getActionCommand().equals("reset")){
				bc.resetBallotCard();
				setState(State.VOTING);
			}else if (e.getActionCommand().equals("close")){
				//Umbiegen auf Auswahlmenü oder Startansicht
				//System.exit(0);
				bc.resetBallotCard();
				setState(State.INIT);
				ec.getStatusSignaling().setRed();
			}else if (e.getActionCommand().equals("backToVoting")){
				
				//Juri
				//Diese IF-Abfrage dient dazu, den Bug bzgl. Stimmenreduzierung zu aufzuheben. Dies geschieht, indem die
				//aufgerufene Methode, die reduzierten Stimmen wieder auf Null setzt, wenn der Wähler zurückgeht.
				if(bc.getValidity() == Validity.VALID_REDUCE_CANDIDATES){
				     
				     for (Party p : bc.getPartyList()) {
						if (p.countManualVotes()> 0) {
								p.updateReducedVotes();
							}
						}
				}
				setState(State.VOTING);
				bc.setError("");
			}else if (e.getActionCommand().equals("printVote")){
				ec.getStatusSignaling().setOrange();
				
				//Juri: startzeit
				after = System.currentTimeMillis();
				runningTime = (after - before) / 1000L;
				//System.out.println("Zeit im Programm: " + runningTime);
				VotingTime vt = new VotingTime();
				try {
					vt.WriteFile("Zeit im Programm: "  + runningTime + "s");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				//Druckprozess anstoßen!!!
				int resX=(int)(595.275590551181*3);
		  		int resY=(int)(841.8897637795276*3);
		  		
//		  		resX=(int)(1000);
//		  		resY=(int)(1200);
				
				VCDPrintJob sfp = new VCDPrintJob();
				if (sfp.setupPageFormat()) {
					if (sfp.setupJobOptions()) {
						try {
							
							
							//Juri
						//	System.out.println("BC count: " + bc.count);
					//		if((bc.count % 2) == 0){
					//		    new PrintoutManipulation(bc);	
					//		}
					//		bc.count++;
														
							IBallotCardImageCreator bcic= new BallotCardImageCreatorNewDesign(resX,resY, this.bc );
							ImageIcon img=bcic.createImage(Color.WHITE);
							sfp.printFile(img);
						} catch (Exception ex) {
							ex.printStackTrace();
							//System.exit(1);
						}
					}
				}
				bc.resetBallotCard();
				bc.setError("");
			//	setState(State.PRINT);
				
				
				setState(State.FINALISED);
				ec.getStatusSignaling().setGreen();
				System.out.println("finished");
				
			}else if (e.getActionCommand().equals("finished_")){
			//	setState(State.FINALISED);
				
				
				
			//	ec.getStatusSignaling().setGreen();
			//	System.out.println("finished");
				
				//Juri: Damit der Balken nicht mehr rot ist, nachdem man einmal seine Stimme abgegeben hat
			//	bc.resetBallotCard();
			//    VotingDeviceController vdc= new VotingDeviceController();
			 //   vdc.showView();
			//	bc.setError("");
			}
			else if (e.getActionCommand().startsWith("lang_")){
				System.out.println("Sprache ändern...");
				Language.getInstance().setLanguage(e.getActionCommand().substring(5, 7));
				
				//ec.v= new MunicipalElectionView(ec.v.getVdg());
				ec.v.update(null, Language.getInstance());
			}
		}
		
	}
	
	/**
	 * Legt den Bedienschritt der Wahl fest.
	 * @param state
	 */
	private void setState(State state){
		if (ec.v instanceof MunicipalElectionView){
			((MunicipalElectionView)ec.v).setState(state);
			bc.updateObserver();
		}
	}

	/* (non-Javadoc)
	 * @see de.tud.vcd.votedevice.multielection.VCDListener#setModel(de.tud.vcd.votedevice.multielection.VCDModel)
	 */
	public void setModel(VCDModel m) throws CannotCastModelException {
		if (m instanceof BallotCard){
			this.bc=(BallotCard)m;
		}else{
			throw new CannotCastModelException(m, this.bc);
		}
		
	}
	
	/**
	 * Liefert das aktuelle Modell zurück
	 * @return
	 */
	public BallotCard getModel() {
		return this.bc;
		
	}

	public void setContainer(ElectionContainer c) {
		//this is needed to call the finished method of the container
		this.ec=c;
		
		
	}

}
