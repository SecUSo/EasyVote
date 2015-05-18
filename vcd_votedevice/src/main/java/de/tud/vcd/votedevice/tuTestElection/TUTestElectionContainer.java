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
