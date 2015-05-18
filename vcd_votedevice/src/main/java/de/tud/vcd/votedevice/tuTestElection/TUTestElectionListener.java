package de.tud.vcd.votedevice.tuTestElection;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

import de.tud.vcd.votedevice.controller.VCDPrintJob;
import de.tud.vcd.votedevice.model.IBallotCardImageCreator;
import de.tud.vcd.votedevice.multielection.ElectionContainer;
import de.tud.vcd.votedevice.multielection.VCDListener;
import de.tud.vcd.votedevice.multielection.VCDModel;
import de.tud.vcd.votedevice.multielection.exceptions.CannotCastModelException;
import de.tud.vcd.votedevice.tuTestElection.TUTestElectionModel.VoteState;
import de.tud.vcd.votedevice.tuTestElection.TUTestElectionView.State;
import de.tud.vcd.votedevice.view.JRoundedButton;

public class TUTestElectionListener implements VCDListener, ActionListener {

	TUTestElectionModel bc;
	ElectionContainer ec;
	
	public void actionPerformed(ActionEvent e) {
		System.out.println("A Action is at the listener: "+e.getActionCommand());
		
		//set a selected candidate to show the correct popup menu
		if (e.getSource() instanceof TUTestShowOption){
			//bc.setPartyVoted(e.getActionCommand(), true);
			TUTestElectionModel.VoteState voteState = null;
			String ec= e.getActionCommand();
			if (ec.equals("A")){
				voteState=VoteState.A;
			}else if (ec.equals("B")){
				voteState=VoteState.B;
			}else if (ec.equals("C")){
				voteState=VoteState.C;
			}else if (ec.equals("D")){
				voteState=VoteState.D;
			}else if (ec.equals("E")){
				voteState=VoteState.E;
			}
			if (voteState!=null){
				bc.toogleVoteState(voteState);
			}
		}
		


		if (e.getSource() instanceof JRoundedButton){
			if (e.getActionCommand().equals("beginVoting") && (ec.v instanceof TUTestElectionView)){
				((TUTestElectionView)ec.v).setState(State.VOTING);
				ec.getStatusSignaling().setRed();
				bc.updateObserver();
			}else if (e.getActionCommand().equals("showVote")){
				System.out.println("showVote");
				((TUTestElectionView)ec.v).setState(State.VOTING);
				setState(State.REVIEW);
				
			}else if (e.getActionCommand().equals("reset")){
				bc.resetBallotCard();
				setState(State.VOTING);
				
			}else if (e.getActionCommand().equals("close")){
				//System.exit(0);
				bc.resetBallotCard();
				setState(State.INIT);
				ec.getStatusSignaling().setRed();
			}else if (e.getActionCommand().equals("backToVoting")){
				setState(State.VOTING);
			}else if (e.getActionCommand().equals("printVote")){
				ec.getStatusSignaling().setOrange();
				int resX=(int)(595.275590551181*3);
		  		int resY=(int)(841.8897637795276*3);
		  		
//		  		resX=(int)(1000);
//		  		resY=(int)(1200);
				
				VCDPrintJob sfp = new VCDPrintJob();
				if (sfp.setupPageFormat()) {
					if (sfp.setupJobOptions()) {
						try {
							IBallotCardImageCreator bcic= new TUTestElectionPrintForm(resX,resY,bc );
							ImageIcon img=bcic.createImage(Color.WHITE);
							sfp.printFile(img);
						} catch (Exception ex) {
							ex.printStackTrace();
							//System.exit(1);
						}
					}
				}
				bc.resetBallotCard();
				
				setState(State.PRINT);
				ec.finished();
				//vdc.setGesperrt(true);
			}else if (e.getActionCommand().equals("finished")){
				setState(State.FINALISED);
				ec.getStatusSignaling().setGreen();
				System.out.println("finished");
				ec.finished();
			}
		}
		
	}
	
	private void setState(State state){
		if (ec.v instanceof TUTestElectionView){
			((TUTestElectionView)ec.v).setState(state);
			bc.updateObserver();
		}
	}

	public void setModel(VCDModel m) throws CannotCastModelException {
		if (m instanceof TUTestElectionModel){
			this.bc=(TUTestElectionModel)m;
		}else{
			throw new CannotCastModelException(m, this.bc);
		}
		
	}
	
	public TUTestElectionModel getModel() {
		return this.bc;
		
	}

	public void setContainer(ElectionContainer c) {
		//this is needed to call the finished method of the container
		this.ec=c;
		
		
	}

}
