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
package de.tud.vcd.votedevice.controller;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import de.tud.vcd.common.BallotCardDesign;
import de.tud.vcd.common.PropertyHandler;
import de.tud.vcd.votedevice.ampel.NoStatusSignaling;
import de.tud.vcd.votedevice.ampel.SendStatusTask;
import de.tud.vcd.votedevice.ampel.StatusSignaling;
import de.tud.vcd.votedevice.ampel.VCDSerialPort;
import de.tud.vcd.votedevice.multielection.ElectionContainer;
import de.tud.vcd.votedevice.municipalElection.MunicipalElectionContainer;
import de.tud.vcd.votedevice.municipalElection.view.MunicipalElectionView;
import de.tud.vcd.votedevice.municipalElection.view.MunicipalElectionView.State;
import de.tud.vcd.votedevice.view.VotingDeviceGui;

/**
 * zentraler Contoller der MVC Architektur.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class VotingDeviceController {

	private VotingDeviceControllerListener vdcl;
	//private BallotCard _model_bc;
	private VotingDeviceGui _view_vdg;//global view for all elections
	
	private StatusSignaling _statusSignaling;
	
	
	ArrayList<ElectionContainer> elections;
	int activElection;
	boolean gesperrt=false;
	VCDSerialPort serialPort;
	
	/**
	 * Erzeugt den Controller. Dieser erzeugt die grafischen Oberflächen und legt zentrale Parameter fest.
	 */
	public VotingDeviceController() {
		//initialize ArrayList for elections
		
		Language.getInstance().setLanguage("de");
		
		elections= new ArrayList<ElectionContainer>();
		
		_view_vdg= new VotingDeviceGui();
		_view_vdg.showVCD();
		
		boolean freigabeErforderlich=false;
		PropertyHandler ph= new PropertyHandler("configuration.properties");
		String freigabeString=ph.getProperty("FREIGABEERFORDERLICH", "1");
		if (freigabeString.equals("1")){
			freigabeErforderlich=true;
		}
		
		try {
			// wenn Freigabe erforderlich ist, wird die Freigabekomponente
			// eingebunden. Ansonsten wird ein leerer Dummy benutzt
			if (freigabeErforderlich) {

				serialPort = new VCDSerialPort();
				String comport = ph.getProperty("COM_PORT", "COM3");
				System.out.println("serialport ist:" + comport);
				serialPort.oeffneSerialPort(comport);
				
				//interner Listener, um die empfangenen Befehle der Freischaltkomponente zu verarbeiten
				class serialPortEventListener implements
						SerialPortEventListener {
					public void serialEvent(SerialPortEvent event) {
						// System.out.println("serialPortEventlistener");
						switch (event.getEventType()) {
						case SerialPortEvent.DATA_AVAILABLE:
							String data = "";
							try {
								data = serialPort.getVerfuegbareDatenr();
							} catch (IOException e) {
								e.printStackTrace();
							}
							if (data.indexOf("F") >= 0) {// && isGesperrt()){
								setGesperrt(false);
								nextElection();
							}
							System.out.println("VCD DATA: " + data);
							break;
						case SerialPortEvent.BI:
						case SerialPortEvent.CD:
						case SerialPortEvent.CTS:
						case SerialPortEvent.DSR:
						case SerialPortEvent.FE:
						case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
						case SerialPortEvent.PE:
						case SerialPortEvent.RI:
						default:
						}
					}
				}

				serialPort.addPortEventListener(new serialPortEventListener());

				
				SendStatusTask statusTask = new SendStatusTask(serialPort, this);
				Timer timer = new Timer();
				timer.schedule(statusTask, 500, 1000);

				_statusSignaling = statusTask;
			} else {
				_statusSignaling = new NoStatusSignaling();
				//Alternativ, um eine Ampel zu sehen:
				// _statusSignaling= new Ampel();
			}
			_statusSignaling.setInit();
			// Load Config

			// Load Design
			InputStream filename = getClass().getClassLoader()
				.getResource("wahlzettel.xml").openStream();
			
				
			BallotCardDesign bcd = BallotCardDesign.getInstance(filename);

			// initialize new election
			ElectionContainer municipalElection = new MunicipalElectionContainer(
					this, bcd);
			elections.add(municipalElection);
			
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane
					.showConfirmDialog(
							null,
							"<html><body>Beim Starten ist ein Problem aufgetreten. Das Programm wird daher beendet.<br><br>"
									+ e.getClass().toString()
									+ ":"
									+ e.getMessage() + " </body></html>",
							"Fehler:", JOptionPane.DEFAULT_OPTION,
							JOptionPane.ERROR_MESSAGE);
			System.exit(JFrame.NORMAL);
		}
		
		activElection=0;
		
	}
	
	/**
	 * Zeigt die zentrale Oberfläche an und 
	 */
	public void showView(){
		_view_vdg.showVCD();
		elections.get(0).getModel().updateObserver();	
	}
	
	
	
	/**
	 * Vorbereitet, um mehrere Wahlen abwickeln zu können. Aber nicht fertig implementiert. Daher wird immer auf die erste Wahl verwiesen.
	 */
	public void nextElection(){
		//System.out.println("Election is finished... show next election... or selection or the same election");
		
		//_view_vdg.setState(state)
		
			elections.get(0).getModel().resetBallotCard();
			((MunicipalElectionView)(elections.get(0).getView())).setState(State.INIT);
			showView();
			elections.get(0).getStatusSignaling().setInit();
		if (elections.size()==1){
			
		}
		
	}
	
	public class VotingDeviceControllerListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
		}
	}
	
	/**
	 * @return the vdcl
	 */
	public VotingDeviceControllerListener getVdcl() {
		return vdcl;
	}
	
	/**
	 * @return the gesperrt
	 */
	public boolean isGesperrt() {
		return gesperrt;
	}

	/**
	 * @param gesperrt the gesperrt to set
	 */
	public void setGesperrt(boolean gesperrt) {
		this.gesperrt = gesperrt;
	}

	/**
	 * Liefert die Oberfläche zurück
	 * @return
	 */
	public VotingDeviceGui getVotingDeviceGui(){
		return _view_vdg;
	}

	/**
	 * @return the _statusSignaling
	 */
	public StatusSignaling get_statusSignaling() {
		return _statusSignaling;
	}
	
}
