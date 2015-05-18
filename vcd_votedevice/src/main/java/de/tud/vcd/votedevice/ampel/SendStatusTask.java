/*******************************************************************************
 * #  Copyright 2015 SecUSo.org / Jurlind Budurushi / Roman J�ris
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
package de.tud.vcd.votedevice.ampel;

import java.io.IOException;
import java.util.TimerTask;

import de.tud.vcd.votedevice.controller.VotingDeviceController;

/**
 * Dient der Kommunikation mit der Freischaltkomponente. Wird als TimerTask eingebunden und sendet dann fortlaufend den Status an
 * die Freigabekomponente
 * 
 * @author Roman J�ris <roman.joeris@googlemail.com>
 *
 */
public class SendStatusTask extends TimerTask implements StatusSignaling {

	String status="";
	VCDSerialPort serialPort;
	String newData="";
	VotingDeviceController vdc;
	
	/**
	 * Erzeugt eine Verbindung mit dem �bergebenen Seriellport und dem entsprechendem Controller
	 * 
	 * @param serialPort
	 * @param vdc
	 */
	public SendStatusTask(VCDSerialPort serialPort, VotingDeviceController vdc) {
		this.serialPort= serialPort;
		this.vdc=vdc;
	}

	/* (non-Javadoc)
	 * @see de.tud.vcd.votedevice.ampel.StatusSignaling#setRed()
	 */
	public void setRed() {
		status="r";

	}

	/* (non-Javadoc)
	 * @see de.tud.vcd.votedevice.ampel.StatusSignaling#setGreen()
	 */
	public void setGreen() {
		status="g";

	}

	/* (non-Javadoc)
	 * @see de.tud.vcd.votedevice.ampel.StatusSignaling#setOrange()
	 */
	public void setOrange() {
		status="o";

	}

	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	public void run() {
		//Senden, dass Tool weiterhin aktiv ist:
		try {
			serialPort.sendeSerialPort("a");
			serialPort.sendeSerialPort(status);
		} catch (IOException e) {
			//e.printStackTrace();
		}
		//Den momentanen Status senden
		

	}

//	public boolean unlockAllowed() {
//		String temp=newData;
//		if (temp.indexOf("F")>=0){
//			newData="";
//			return true;
//		}else{
//			return false;
//		}
//			
//	}

	/* (non-Javadoc)
	 * @see de.tud.vcd.votedevice.ampel.StatusSignaling#setInit()
	 */
	public void setInit() {
		status="s";
		
	}

	
}
