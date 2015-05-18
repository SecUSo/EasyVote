package de.tud.vcd.votedevice.onscreenkeyboard;

import java.awt.Color;

import de.tud.vcd.votedevice.view.JRoundedButton;

/**
 * Taste der Bildschirmtastatur. Basiert auf den abgerundeten Buttons.
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class OnScreenKey extends JRoundedButton{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Color originalBGColor;
	Color disabledColor;
	
	/**
	 * Erzeugt eine Taste mit vorgegebenen Buchstaben und Farbgestaltung
	 * @param name
	 * @param foregroundColor
	 * @param backgroundColor
	 */
	public OnScreenKey(String name, Color foregroundColor, Color backgroundColor) {
		super(name, foregroundColor, backgroundColor);
		originalBGColor=backgroundColor;
		disabledColor= new Color(Math.max(0, originalBGColor.getRed()-100), Math.max(0, originalBGColor.getGreen()-100),Math.max(0,  originalBGColor.getBlue()-100));
	}
	
	/* (non-Javadoc)
	 * @see de.tud.vcd.votedevice.view.JRoundedButton#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled){
		super.setEnabled(enabled);
		if (enabled){
			backgroundColor=originalBGColor;
		}else{
			backgroundColor=disabledColor;
		}
	}
	
	

}
