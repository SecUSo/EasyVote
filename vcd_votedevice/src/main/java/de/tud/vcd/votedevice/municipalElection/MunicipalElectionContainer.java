package de.tud.vcd.votedevice.municipalElection;

import de.tud.vcd.common.BallotCardDesign;
import de.tud.vcd.votedevice.controller.VotingDeviceController;
import de.tud.vcd.votedevice.multielection.ElectionContainer;
import de.tud.vcd.votedevice.municipalElection.controller.MunicipalElectionListener;
import de.tud.vcd.votedevice.municipalElection.model.BallotCard;
import de.tud.vcd.votedevice.municipalElection.view.MunicipalElectionView;

public class MunicipalElectionContainer extends ElectionContainer {

	/**
	 * Container der eine Kommunalwahl implementiert. Hier werden Modell, View und Controller zusammen geführt
	 * 
	 * @param vdc
	 * @param bcd
	 * @throws Exception
	 */
	public MunicipalElectionContainer(VotingDeviceController vdc, BallotCardDesign bcd) throws Exception {
		super(vdc);
		//create concrete objects:
		this.v= new MunicipalElectionView(vdc.getVotingDeviceGui());
		this.m=new BallotCard(bcd);
		this.l= new MunicipalElectionListener();
		
		//register each other
		m.addObserver(v);
		l.setModel(m);
		l.setContainer(this);
		v.setListener(l);
		
	}

}
