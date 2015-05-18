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
/**
 * 
 */
package de.tud.vcd.eVotingTallyAssistance.controller;

import java.awt.image.BufferedImage;

import javax.swing.JOptionPane;

import de.tud.vcd.eVotingTallyAssistance.common.exceptions.OCRException;



/**
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class WahlzettelScanner  {
/*
	Scanner scanner;
	int scannerSourceId;
	boolean bildDa;
	Tallying_C c;
	
	private static WahlzettelScanner instance = null;
	
	public static synchronized WahlzettelScanner getInstance(Tallying_C c) throws Exception {
		if (instance == null) {
			instance = new WahlzettelScanner(c);
		}
		return instance;
	}
	
	
	private WahlzettelScanner(Tallying_C c) throws OCRException, ScannerIOException {
		scannerSourceId=-1;
		this.c=c;
		String[] s=Scanner.getDevice().getDeviceNames();
		if (s.length<1){
			throw new OCRException("Es wurde kein angeschlossener Scanner gefunden.");
		}else if(s.length==1){
			scannerSourceId=0;
		}else{
			//Es sind mehr als ein Scanner angeschlossen, daher wird beim ersten Scannen nach der Quelle gefragt.
			//Also die SourceId bleibt erstmal ungesetzt.
		}
	}
	
	private void selectSource() throws ScannerIOException{
		
			String[] s=Scanner.getDevice().getDeviceNames();
			for (String st: s){
				System.out.println(st);
			}
			Scanner.getDevice().select();
			
	}
	
	final BufferedImage[] variable = new BufferedImage[1];
	
	
	public void scan() throws ScannerIOException{
		bildDa=false;
		//erstmal nach Quelle fragen, wenn sie noch nicht bestimmt wurde.
		if (scannerSourceId==-1){
			JOptionPane.showConfirmDialog(null, "Beim ersten Scanvorgang wird nach der Quelle gefragt. \nBitte drücken Sie danach erneut den Knopf zum Scannen.", "Hinweis:", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
			selectSource();
			scannerSourceId=1;
		}else{
			//
			scanner = Scanner.getDevice();
		    scanner.acquire();
		    scanner.addListener(c.getScannerList());
		}
	}


*/

}
