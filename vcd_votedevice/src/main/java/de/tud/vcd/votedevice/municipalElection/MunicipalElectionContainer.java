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
