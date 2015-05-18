package de.tud.vcd.votedevice.multielection;

import java.util.Observer;

import de.tud.vcd.votedevice.multielection.exceptions.CannotConvertListenerException;
import de.tud.vcd.votedevice.view.VotingDeviceGui;

public interface VCDView extends Observer{
	public void setListener(VCDListener l)throws CannotConvertListenerException;
	public VotingDeviceGui getVdg();
	
	//public void setContentToTwoParts();
	//public void setContentToThreeParts();

}
