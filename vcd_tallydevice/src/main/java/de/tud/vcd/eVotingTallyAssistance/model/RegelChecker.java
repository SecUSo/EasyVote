/**
 * 
 */
package de.tud.vcd.eVotingTallyAssistance.model;

import java.util.HashSet;

import javax.swing.JOptionPane;

import de.tud.vcd.common.BallotCardDesign;
import de.tud.vcd.common.BallotCardDesign.DesignKeys;




/**
 * Pr�ft, ob die �bergebene Stimme auch wirklich konform ist.
 * 
 * @author Roman J�ris <roman.joeris@googlemail.com>
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
		// Es werden alle internen Abh�ngigkeiten gepr�ft, die erf�llt sein
		// m�ssen. Dabei ist nicht zu �berpr�fen, ob der Wahlzettel als Ung�ltig
		// markiert wurde.

		// F�r alle Kandidaten pr�fen, dass die maximale Anzahl an Kreuzen nicht
		// �berschritten wird.
		int anzParteien=0;
		HashSet<Integer> parties=new HashSet<Integer>();
//		for(Candidate c: cAl){
//			candIds.put(c.getId(), c);
//		}
		int gesamtStimmen = 0;
		for (Stimme st : wahlzettel.getStimmen()) {
			// Pr�fen, ob die Anzahl an Stimmen f�r diesen Kandidaten das
			// Maximum nicht �berschreitet
			if (st.getValue() > VOTESPROKANDIDAT)
				return Validity.INVALID;
			gesamtStimmen += st.getAngekreuzteStimmen();
			parties.add(st.getId()/100);
		}
		//Pr�fen, ob der Status korrigierbar w�re:
		if (gesamtStimmen>MAXSTIMMEN && parties.size()<=1){
			return Validity.REDUCE_CANDIDATES;
		}
		// Pr�fen, ob die Maximalanzahl an Stimmen eingehalten ist:
		if (gesamtStimmen > MAXSTIMMEN)
			return Validity.INVALID;

		// Wenn keine Pr�fung fehlschl�gt, dann frei geben.
		return Validity.VALID;
	}
}
