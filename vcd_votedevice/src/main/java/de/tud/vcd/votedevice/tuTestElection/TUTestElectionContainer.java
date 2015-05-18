package de.tud.vcd.votedevice.tuTestElection;

import de.tud.vcd.common.BallotCardDesign;
import de.tud.vcd.votedevice.controller.VotingDeviceController;
import de.tud.vcd.votedevice.multielection.ElectionContainer;

public class TUTestElectionContainer extends ElectionContainer {

	public TUTestElectionContainer(VotingDeviceController vdc, BallotCardDesign bcd) throws Exception {
		super(vdc);
		//create concrete objects:
		this.v= new TUTestElectionView(vdc.getVotingDeviceGui());
		this.m=new TUTestElectionModel(bcd);
		this.l= new TUTestElectionListener();
		
		//register each other
		m.addObserver(v);
		l.setModel(m);
		l.setContainer(this);
		v.setListener(l);
		
	}

}
