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

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;


/**
 * Stellt die Verbindung zum Seriellprot her. Kapselt die RXTX Bibliothek, um diese leichter austauschen zu k�nnen.
 * Die Baudwerte sind dabei fest kodiert (9600Baud, 8 Datenbits, 1 Stopbit, keine Parit�t)
 * 
 * @author Roman J�ris <roman.joeris@googlemail.com>
 *
 */
public class VCDSerialPort {

	CommPortIdentifier serialPortId;
	Enumeration<?> enumComm;
	SerialPort serialPort;
	OutputStream outputStream;
	InputStream inputStream;
	Boolean serialPortGeoeffnet = false;

	int baudrate = 9600;
	int dataBits = SerialPort.DATABITS_8;
	int stopBits = SerialPort.STOPBITS_1;
	int parity = SerialPort.PARITY_NONE;
	
	public VCDSerialPort() {
		
	}

	
//	/**
//	 * Innere Klasse als Listener, die meldet, wenn Daten eingetroffen sind.
//	 * 
//	 * @author Roman J�ris <roman.joeris@googlemail.com>
//	 *
//	 */
//	class serialPortEventListener implements SerialPortEventListener {
//		public void serialEvent(SerialPortEvent event) {
//			System.out.println("serialPortEventlistener");
//			switch (event.getEventType()) {
//			case SerialPortEvent.DATA_AVAILABLE:
//				try {
//					serialPortDatenVerfuegbar();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				break;
//			case SerialPortEvent.BI:
//			case SerialPortEvent.CD:
//			case SerialPortEvent.CTS:
//			case SerialPortEvent.DSR:
//			case SerialPortEvent.FE:
//			case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
//			case SerialPortEvent.PE:
//			case SerialPortEvent.RI:
//			default:
//			}
//		}
//	}	
	
	/**
	 * F�gt den Listener der Instanz hinzu. Es kann dabei nur einen Listener geben, sonst wird eine Exception geworfen.
	 * 
	 * @param spel
	 * @throws TooManyListenersException
	 */
	public void addPortEventListener(SerialPortEventListener spel) throws TooManyListenersException{
			serialPort.addEventListener(spel);
		
	}
	
//	/**
//	 * @throws IOException
//	 */
//	void serialPortDatenVerfuegbar() throws IOException {
//			byte[] data = new byte[150];
//			int num;
//			while(inputStream.available() > 0) {
//				num = inputStream.read(data, 0, data.length);
//				System.out.println("Empfange: "+ new String(data, 0, num));
//				//empfangen.append(new String(data, 0, num));
//			}
//		
//	}
	
	/**
	 * Liefert die angekommenen Daten als Text zur�ck. Die L�nge variiert dabei je nach Geschwindigkeit. Es ist daher sicherer
	 * jeweils nur auf ein Zeichen zu pr�fen.
	 * 
	 * @return String
	 * @throws IOException
	 */
	public String getVerfuegbareDatenr() throws IOException {
			byte[] data = new byte[150];
			int num=0;
			while(inputStream.available() > 0) {
				num = inputStream.read(data, 0, data.length);
			}
			return new String(data, 0, num);
		
		
	}
	
	/**
	 * Sendet die �bergebene Nachricht �ber den Seriellport an das Endger�t
	 * @param nachricht String
	 * @throws IOException
	 */
	void sendeSerialPort(String nachricht) throws IOException
	{
		//System.out.println("Sende: " + nachricht);
		if (serialPortGeoeffnet != true)
			return;
			outputStream.write(nachricht.getBytes());
		
	}
	
	/**
	 * Schlie�t den seriellen Port
	 */
	void schliesseSerialPort()
	{
		if ( serialPortGeoeffnet == true) {
			System.out.println("Schlie�e Serialport");
			serialPort.close();
			serialPortGeoeffnet = false;
		} else {
			System.out.println("Serialport bereits geschlossen");
		}
	}
	
	/**
	 * Versucht den seriellen Port zu �ffnen. Klappt nur, wenn er noch nicht von einer anderen Anwendung benutzt wird. Der jeweilige
	 * Port wird mit �bergeben. UNter Windows ist die Schreibweise "COM8" zum Beispeil unter Linux "dev/tts0"
	 * @param portName
	 * @return
	 * @throws PortInUseException
	 * @throws IOException
	 * @throws UnsupportedCommOperationException
	 */
	public boolean oeffneSerialPort(String portName) throws PortInUseException, IOException, UnsupportedCommOperationException
	{
		Boolean foundPort = false;
		if (serialPortGeoeffnet != false) {
			System.out.println("Serialport bereits ge�ffnet");
			schliesseSerialPort();
			return false;
		}
		System.out.println("�ffne Serialport");
		enumComm = CommPortIdentifier.getPortIdentifiers();
		while(enumComm.hasMoreElements()) {
			serialPortId = (CommPortIdentifier) enumComm.nextElement();
			System.out.println("SerialportIDs:" + serialPortId.getName());
			if (portName.contentEquals(serialPortId.getName())) {
				foundPort = true;
				break;
			}
		}
		if (foundPort != true) {
			System.out.println("Serialport nicht gefunden: " + portName);
			return false;
		}
		    serialPort = (SerialPort) serialPortId.open("�ffnen und Senden", 500);
			outputStream = serialPort.getOutputStream();
			inputStream = serialPort.getInputStream();
		
		serialPort.notifyOnDataAvailable(true);
			serialPort.setSerialPortParams(baudrate, dataBits, stopBits, parity);
		
		
		serialPortGeoeffnet = true;
		return true;
	}

}
