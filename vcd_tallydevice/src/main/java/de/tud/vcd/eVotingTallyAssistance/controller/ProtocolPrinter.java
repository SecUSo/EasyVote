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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.PrintJob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.swing.JFrame;

import de.tud.vcd.common.BallotCardDesign;
import de.tud.vcd.eVotingTallyAssistance.common.ConfigHandler;
import de.tud.vcd.eVotingTallyAssistance.common.ConfigHandler.ConfigVars;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.ConfigFileException;
import de.tud.vcd.eVotingTallyAssistance.model.VotingLogEntry;
import de.tud.vcd.eVotingTallyAssistance.model.VotingLogEntry.LogLevel;
import de.tud.vcd.eVotingTallyAssistance.model.VotingLogger;
import de.tud.vcd.eVotingTallyAssistance.model.Wahlergebnis;
//import de.tud.vcd.eVotingTallyAssistance.common.ConfigHandler;
//import de.tud.vcd.eVotingTallyAssistance.common.ConfigHandler.ConfigVars;
//import de.tud.vcd.eVotingTallyAssistance.common.exceptions.ConfigFileException;
//import de.tud.vcd.eVotingTallyAssistance.model.VotingLogEntry;
//import de.tud.vcd.eVotingTallyAssistance.model.VotingLogEntry.LogLevel;
//import de.tud.vcd.eVotingTallyAssistance.model.VotingLogger;
//import de.tud.vcd.eVotingTallyAssistance.model.Wahlergebnis;


/**
 * Erzeugt mit der print-Methode ein Protokoll der Ergebnisse und druckt diese über einen wählbaren Drucker aus. Es stehen dazu
 * alle auf dem System verfügbaren Drucker zur Verfügung. Es werden nur Standardklassen benötigt, da direkt mit dem Graphics
 * Objekt auf den Drucker zugegriefen wird. 
 * 
 * Die einzige Methode ist die PrintMethode, die mit den entsprechenden Parametern aufgerufen werden muss. Diese Methode müßte 
 * auch angepasst werden, wenn das Aussehen des Protokolls verändert werden soll. In der Methode wird schrittweise durch die 
 * Elemente gegangen. Wenn der Platz für die jeweilige Komponente nicht passt, wird ein Seitenumbruch eingefügt, so dass das
 * Protokoll aus mehreren Seiten bestehen kann, die jeweils mit einer Seitenzahl versehen werden.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class ProtocolPrinter {

	public ProtocolPrinter() {
		
	}
	
	
	/**
	 * Druckt das Protokoll aus, welches dynamisch zu diesem Zeitpunkt erzeugt wird.
	 * 
	 * @param frame JFrame Ein Frame, welches über ein Toolkit verfügt. Z.B. TallyGui, damit der Druckdialog angezeigt werden kann-
	 * @param ergebnis Wahlergebnis: Das Ergebnis aus dem Model
	 * @param wahlvorstand String: Name des Wahlleiters
	 * @param wh1 String: Name des ersten Wahlhelfers
	 * @param wh2 String: Name des zweiten Wahlhelfers
	 * @param erstelldatum Date: Datum des Erstellzeitpunktes der Wahlurne.
	 *  
	 * @return Boolean: Konnte der Druck gestartet werden
	 */
	public boolean print(JFrame frame,Wahlergebnis ergebnis,  String wahlvorstand , String wh1, String wh2, Date erstelldatum)  {
		SimpleDateFormat dateformat=new SimpleDateFormat( "dd.MM.YYYY HH:mm");
		
		//Fragt nach einem Drucker. Dazu wird der Druckdialog angezeigt:
		PrintJob auftrag =frame.getToolkit().getPrintJob(frame, "Auszählprotokoll", null);
	    
	    //wenn ein Drucker ausgewählt wurde, beginnen:
	    if(auftrag != null) {
	    	//Parameter bestimmen
	    	int druckbreite=auftrag.getPageDimension().width;
		    int druckhoehe=auftrag.getPageDimension().height;
		    int seitenRand=30;
		    int randOben=30;
		    int randUnten=30;
	        //Erste Seite anlegen:
	    	Graphics graphik = auftrag.getGraphics();
	        if (graphik != null) {
	        	int y_pos=randOben+18;
	        	
	        	//Überschrift:
	            graphik.setFont(new Font("Arial", Font.PLAIN, 20)); 
	            graphik.drawString("Auszählprotokoll", seitenRand, y_pos);   
	            y_pos+=15;
	            //zur normalen Schrift wechseln:
	            graphik.setFont(new Font("Arial", Font.PLAIN, 10)); 
	            
	            //Seitenzahl schreiben:
	        	int page=1;
	        		//das Schreiben der maximalen Seite ist nicht ohne weiteres möglich, da jede Seite ein eigener Druckbereich ist und somit ein Backtracking benötigt würde.
	        		graphik.drawString(""+ page, druckbreite-seitenRand-12, druckhoehe-randUnten);
	        	
	        	//Header
	            try {
					graphik.drawString("der Wahl: "+ BallotCardDesign.getInstance().getElection_name() + " ("+BallotCardDesign.getInstance().getElection_id()+")", seitenRand, y_pos);
				} catch (Exception e) {
					graphik.drawString("der Wahl: (unbekannt)" , seitenRand, y_pos);
				} 
	            
	            
	            int einrueckung=70;
	            int merkeY=y_pos;
	            y_pos+=20;
	            graphik.drawString("Wahlvorstand: ", seitenRand, y_pos);
	            graphik.drawString( wahlvorstand, seitenRand+einrueckung, y_pos);
	            y_pos+=12;
	            graphik.drawString("Wahlhelfer: ", seitenRand, y_pos);
	            graphik.drawString(wh1, seitenRand+einrueckung, y_pos);
	            y_pos+=12;
	            graphik.drawString("Wahlhelfer: ", seitenRand, y_pos);
	            graphik.drawString(wh2, seitenRand+einrueckung, y_pos);
	            
	            y_pos=merkeY;
	            y_pos+=20;
	            einrueckung=(druckbreite-seitenRand-seitenRand)/2;
	            int shift2=(druckbreite-seitenRand-seitenRand)/2+70;
	            
	            //y_pos+=12;
	            graphik.drawString( "Beginn: ", seitenRand+einrueckung, y_pos);
	            graphik.drawString( dateformat.format(erstelldatum), seitenRand+shift2, y_pos);
	            y_pos+=12;
	            graphik.drawString("Protokolldruck:", seitenRand+einrueckung, y_pos);
	            graphik.drawString(dateformat.format(new Date()), seitenRand+shift2, y_pos);
	            y_pos+=12;
	            graphik.drawString("Rechner:", seitenRand+einrueckung, y_pos);
	            try {
					graphik.drawString(ConfigHandler.getInstance().getConfigValue(ConfigVars.MACHINEID), seitenRand+shift2, y_pos);
				} catch (ConfigFileException e) {
					graphik.drawString("unbekannt", seitenRand+shift2, y_pos);
				}
	           
	            
	            //Anzahl Wahlzettel ausgeben:
	            y_pos+=30;
	            graphik.drawString("Enthaltene Stimmzettel:", seitenRand, y_pos);
	            graphik.drawString(""+ergebnis.getAnzahlWahlzettel(), seitenRand+120, y_pos);
	            y_pos+=12;
	            graphik.drawString("davon ungültig:", seitenRand, y_pos);
	            graphik.drawString(""+ergebnis.getAnzahlUngueltigeWahlzettel(), seitenRand+120, y_pos);
	            
	            
	            y_pos+=30;
	            graphik.drawString("Gezählte Stimmen:", seitenRand, y_pos);
	            //-------------------------------------
	            //nun Ergebnistabelle abarbeiten
	            //-------------------------------------
	            einrueckung=45; //Einrückung bis zum nächsten Kandidaten
	            
	            //Laden der möglichen Kandidaten aus dem Verzeichnis:
			    ArrayList<Integer> candidates;
			    int kandidatenandAnz;
			    
			    //Y-Pos ist der aktuelle vertikale Wert für die Position
			    y_pos+=8;
			    
			    //Wenn die Liste vorhanden ist schreiben:
			    try {
					candidates = BallotCardDesign.getInstance().getCandidateIds();
					
					Collections.sort(candidates);
					kandidatenandAnz=candidates.size();
				
		        
					int kandidatenPos=0;
					
					//Berechnen wie viele Kandidaten in eine Zeile passen:
					int kandProZeile=(druckbreite-2*seitenRand) / einrueckung;
			    
					//-------------------------------------
					//Schreibt alle Kandidatenergebnisse auf das Protokoll und fügt gegebenenfalls Seitenumbrüche ein.
					while (kandidatenPos<kandidatenandAnz){
						//eventuell Seitenwechsel einfügen:
						if (y_pos+35 > (druckhoehe-randUnten) ){
							graphik.dispose();
							auftrag.getGraphics();
							//if (graphik == null){ throw new Exception();};
							y_pos=randOben;
							//Bei Seitenwechsel auch die Seitenzahl schreiben:
							page++;
							graphik.drawString(""+ page, druckbreite-seitenRand-12, druckhoehe-randUnten);
							
						}
						
						//den grauen Streifen drucken:
						graphik.setColor(Color.LIGHT_GRAY);
						graphik.fillRect(seitenRand, y_pos, (druckbreite - 2 * seitenRand),12);

						//Nun die Kandidatennummer und die Stimmenanzahl ausgeben:
						graphik.setColor(Color.BLACK);
						for (int i = 0; i < kandProZeile; i++) {
							if ((kandidatenPos + i)<kandidatenandAnz){
								graphik.drawString(("" + candidates.get(kandidatenPos + i)),seitenRand + 5 + i * einrueckung, y_pos + 9);
								graphik.drawString(("" + ergebnis.getErgebnisOfCandidate(candidates.get(kandidatenPos + i)) ), seitenRand + 5 + i* einrueckung, y_pos + 22);
							}
						}
						//Neue Positionen berechnen:
						y_pos = y_pos + 35;
						kandidatenPos += kandProZeile;
					};
					// -------------------------------------
     
			    } catch (Exception e) {
			    	//Fehlerbehandlung, wenn die Kandidatenliste nicht vorhanden sein sollte.
					kandidatenandAnz=0;
					graphik.drawString("Die Ergebnisse können nicht gedruckt werden. ",seitenRand + 5 , y_pos + 9);
					y_pos+=12;
					graphik.drawString("Bitte schreiben Sie die Ergebnisse per Hand auf ein ",seitenRand + 5 , y_pos + 9);
					y_pos+=12;
					graphik.drawString("separates Blatt, welches Sie dem Protokoll unterschrieben beilegen.",seitenRand + 5 , y_pos + 9);
					y_pos+=12;
				}
			    
			    //Kandidatenliste ist abgeschlossen 
			    //nun das Änderungsprotokoll drucken
			    if (y_pos+35 > (druckhoehe-randUnten) ){
					graphik.dispose();
					auftrag.getGraphics();
					//if (graphik == null){ throw new Exception();};
					y_pos=randOben;
					//Bei Seitenwechsel auch die Seitenzahl schreiben:
					page++;
					graphik.drawString(""+ page, druckbreite-seitenRand-12, druckhoehe-randUnten);
					
				}
			    String printAuswertung="1";
			    try {
					printAuswertung=ConfigHandler.getInstance().getConfigValue(ConfigVars.AUSWERTUNGSPROTOKOLL);
				} catch (ConfigFileException e) {
				}
			    if (printAuswertung.equals("1")){
			    graphik.drawString("Das Auswertungsprotokoll enthält folgende Einträge:",seitenRand  , y_pos + 9);
				//Neue Positionen berechnen:
				y_pos = y_pos + 15;
			    
			    ArrayList<VotingLogEntry> vleAL= VotingLogger.getInstance().getVotingLogEntries(LogLevel.INFO);
			    for (VotingLogEntry vle: vleAL){
					//eventuell Seitenwechsel einfügen:
					if (y_pos+35 > (druckhoehe-randUnten) ){
						graphik.dispose();
						auftrag.getGraphics();
						//if (graphik == null){ throw new Exception();};
						y_pos=randOben;
						//Bei Seitenwechsel auch die Seitenzahl schreiben:
						page++;
						graphik.drawString(""+ page, druckbreite-seitenRand-12, druckhoehe-randUnten);
						
					}
					graphik.drawString(vle.getMsg(),seitenRand + 15 , y_pos + 9);
					//Neue Positionen berechnen:
					y_pos = y_pos + 15;
			    }
			    }
			   //nun noch das Unterschriftenfeld einbauen
	            
			    //prüfen, ob Platz für die Unterschriftenzeile ist:
			    //eventuell Seitenwechsel einfügen:
			    int platzFuerUeberschrift=106;
				if (y_pos+platzFuerUeberschrift > (druckhoehe-randUnten) ){
					graphik.dispose();
					auftrag.getGraphics();
					y_pos=randOben+60;
					//Seitenzahl bei Wechsel drucken
					page++;
					graphik.drawString(""+ page, druckbreite-seitenRand-12, druckhoehe-randUnten);
				}
				
				// -------------------------------------
			    //Nun Unterschriften drucken:
				int haelfteDerSeite=(druckbreite-2*seitenRand)/2;
				y_pos+=30;
				graphik.drawString("______________________________________",seitenRand  , y_pos);
				graphik.drawString("______________________________________",seitenRand + haelfteDerSeite, y_pos);
				y_pos+=12;
				graphik.drawString(wh1+" (Wahlhelfer)",seitenRand  , y_pos);
				graphik.drawString(wh2+" (Wahlhelfer)",seitenRand + haelfteDerSeite , y_pos);
				
				y_pos+=40;
				graphik.drawString("______________________________________",seitenRand  , y_pos);
				y_pos+=12;
				graphik.drawString(wahlvorstand+" (WahlVorstand)",seitenRand  , y_pos);
				// -------------------------------------
				
				//letzte Seite abschließen:
	            graphik.dispose();
	        }else{
	        	//Falsch zurückgeben, wenn das Grahics null ist und somit kein Druck erstellt werden kann.
	        	return false;
	        }
	        //Druckauftrag abschließen/rausschicken:
	        auftrag.end();
	        return true;
	    }else{
	    	//gibt false zurück, wenn der Druckdialog abgebrochen wurde.
	    	return false;
	    }
	}

}
