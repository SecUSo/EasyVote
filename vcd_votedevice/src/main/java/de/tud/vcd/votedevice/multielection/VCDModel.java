package de.tud.vcd.votedevice.multielection;

import java.util.Observer;

public interface VCDModel {
	public void addObserver(Observer o);
	public void updateObserver();
	public void resetBallotCard();

}
