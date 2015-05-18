/**
 * 
 */
package de.tud.vcd.eVotingTallyAssistance.model;

import java.util.HashSet;

import javax.swing.JOptionPane;

import de.tud.vcd.common.BallotCardDesign;
import de.tud.vcd.common.BallotCardDesign.DesignKeys;




/**
 * Prüft, ob die übergebene Stimme auch wirklich konform ist.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 */
public class RegelChecker {
	
	public enum Validity {
		  INVALID, REDUCE_CANDIDATES ,VALID
	}

	private int VOTESPROKANDIDAT = 0;
	private int MAXSTIMMEN = 0;

	public RegelChecker() {
		// Die betroffenen Parameter aus der WahlzettelDesign laden
		BallotCardDesign wzd;
		try {
			wzd = BallotCardDesign.getInstance();
			VOTESPROKANDIDAT = wzd.getDesignValue(DesignKeys.VOTESPROKANDIDAT);
			MAXSTIMMEN = wzd.getDesignValue(DesignKeys.MAXSTIMMEN);
		} catch (Exception e) {
			JOptionPane
					.showConfirmDialog(
							null,
							"Der Regelchecker konnte auf Grund einer fehlerhaften Wahlzettel-XML nicht initialisiert werden. Daher gelten zu strenge Regeln. Starten Sie das Programm neu, wenn Sie die Fehler behoben haben.",
							"Fehlermeldung:", JOptionPane.DEFAULT_OPTION,
							JOptionPane.ERROR_MESSAGE);

		}

	}

	public Validity checkWahlzettel(Wahlzettel wahlzettel) {
		// Es werden alle internen Abhängigkeiten geprüft, die erfüllt sein
		// müssen. Dabei ist nicht zu überprüfen, ob der Wahlzettel als Ungültig
		// markiert wurde.

		// Für alle Kandidaten prüfen, dass die maximale Anzahl an Kreuzen nicht
		// überschritten wird.
		int anzParteien=0;
		HashSet<Integer> parties=new HashSet<Integer>();
//		for(Candidate c: cAl){
//			candIds.put(c.getId(), c);
//		}
		int gesamtStimmen = 0;
		for (Stimme st : wahlzettel.getStimmen()) {
			// Prüfen, ob die Anzahl an Stimmen für diesen Kandidaten das
			// Maximum nicht überschreitet
			if (st.getValue() > VOTESPROKANDIDAT)
				return Validity.INVALID;
			gesamtStimmen += st.getAngekreuzteStimmen();
			parties.add(st.getId()/100);
		}
		//Prüfen, ob der Status korrigierbar wäre:
		if (gesamtStimmen>MAXSTIMMEN && parties.size()<=1){
			return Validity.REDUCE_CANDIDATES;
		}
		// Prüfen, ob die Maximalanzahl an Stimmen eingehalten ist:
		if (gesamtStimmen > MAXSTIMMEN)
			return Validity.INVALID;

		// Wenn keine Prüfung fehlschlägt, dann frei geben.
		return Validity.VALID;
	}
}
