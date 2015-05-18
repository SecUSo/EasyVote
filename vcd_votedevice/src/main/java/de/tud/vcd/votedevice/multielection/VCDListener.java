package de.tud.vcd.votedevice.multielection;

import de.tud.vcd.votedevice.multielection.exceptions.CannotCastModelException;


public interface VCDListener {
	public void setModel(VCDModel m)throws CannotCastModelException;
	public void setContainer(ElectionContainer c);
	
}
