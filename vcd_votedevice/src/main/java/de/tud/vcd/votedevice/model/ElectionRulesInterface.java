package de.tud.vcd.votedevice.model;

import de.tud.vcd.votedevice.municipalElection.model.BallotCard;
import de.tud.vcd.votedevice.municipalElection.model.BallotCard.Validity;

public interface ElectionRulesInterface {


	/**
	 * Interface: Liefert die Gültigkeit des übergebenen Models zurück
	 * @param bc
	 * @return
	 */
	public abstract Validity getValidity(BallotCard bc);
}