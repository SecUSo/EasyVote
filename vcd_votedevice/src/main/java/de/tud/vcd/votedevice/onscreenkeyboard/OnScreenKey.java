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
