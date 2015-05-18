package de.tud.vcd.votedevice.model;

import java.awt.Color;

import javax.swing.ImageIcon;

public interface IBallotCardImageCreator {

	/**
	 * Erzeugt ein ImageIcon mit dem Wahlzettel. 
	 * 
	 * @param filename
	 * @throws Exception
	 */
	public abstract ImageIcon createImage(Color bgcolor) throws Exception;

}