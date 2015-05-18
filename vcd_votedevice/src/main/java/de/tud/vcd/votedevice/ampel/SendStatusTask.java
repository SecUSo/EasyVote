package de.tud.vcd.votedevice.ampel;

import java.io.IOException;
import java.util.TimerTask;

import de.tud.vcd.votedevice.controller.VotingDeviceController;

/**
 * Dient der Kommunikation mit der Freischaltkomponente. Wird als TimerTask eingebunden und sendet dann fortlaufend den Status an
 * die Freigabekomponente
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class SendStatusTask extends TimerTask implements StatusSignaling {

	String status="";
	VCDSerialPort serialPort;
	String newData="";
	VotingDeviceController vdc;
	
	/**
	 * Erzeugt eine Verbindung mit dem übergebenen Seriellport und dem entsprechendem Controller
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
