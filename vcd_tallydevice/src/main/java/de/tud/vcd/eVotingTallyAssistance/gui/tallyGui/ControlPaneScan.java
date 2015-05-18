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
package de.tud.vcd.eVotingTallyAssistance.gui.tallyGui;

import java.awt.event.ActionListener;

import javax.swing.JButton;

import de.tud.vcd.eVotingTallyAssistance.common.ConfigHandler;
import de.tud.vcd.eVotingTallyAssistance.common.ConfigHandler.ConfigVars;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.ConfigFileException;
import de.tud.vcd.eVotingTallyAssistance.controller.ControllerCalls;


/**
 * Erzeugt ein Kontrollfeld, um einen Wahlzettel zu scannen oder einen
 * bisherigen zu laden.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 */
public class ControlPaneScan extends ControlPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Erzeugt das Feld mit dem übergebenen Namen als Überschrift und einem
	 * ActionListener, der bei den Kindern registriert wird.
	 * 
	 * @param al
	 * @param name
	 */
	public ControlPaneScan(ActionListener al, String name) {
		super(name);
		JButton scanButton = new JButton("Wahlzettel scannen...");
		scanButton.setVisible(false);
		JButton barcodeButton = new JButton("Barcode scannen...");
		JButton loadButton = new JButton("Wahlzettel laden...");
		JButton loadImage = new JButton("Bild interpretieren...");
		loadImage.setVisible(false);
		scanButton.setActionCommand(ControllerCalls.Calls.SCAN.name());
		barcodeButton.setActionCommand(ControllerCalls.Calls.BARCODE.name());
		loadButton.setActionCommand(ControllerCalls.Calls.LOADBALLOT.name());
		loadImage.setActionCommand(ControllerCalls.Calls.LOADIMAGE.name());
		scanButton.addActionListener(al);
		barcodeButton.addActionListener(al);
		loadButton.addActionListener(al);
		loadImage.addActionListener(al);
		scanButton
				.setToolTipText("Der Scanner wird angesprochen, um einen Wahlzettel einzuscannen. Anschließend erfolgt die automatische Erkennung");
		barcodeButton
		.setToolTipText("Per Barcode-Scanner wird ein Wahlzettel eingelesen. Anschließend erfolgt die automatische Erkennung");
		loadButton
				.setToolTipText("Lädt einen Wahlzettel, der bisher schon gespeichert wurde.");
		loadImage
		.setToolTipText("Lädt ein Bild von der Festplatte und erkennt dieses.");
		addNewComponent("scan", scanButton);
		addNewComponent("barcode", barcodeButton);
		addNewComponent("loadImage", loadImage);
		addNewComponent("load", loadButton);
		
		int erlaubt=0;
		try {
			erlaubt=Integer.valueOf(ConfigHandler.getInstance()
					.getConfigValue(ConfigVars.LOADFROMFILE));
		} catch (ConfigFileException | NumberFormatException e) {
			//einfach nichts machen und der Button bleibt verboten
		} 
		// Wieder reinnehmen
//		if (erlaubt>0){
//			loadImage.setVisible(true);
//		}else{
//			loadImage.setVisible(false);
//		}
	}

}
