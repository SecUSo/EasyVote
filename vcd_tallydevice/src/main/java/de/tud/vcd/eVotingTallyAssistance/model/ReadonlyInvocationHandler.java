package de.tud.vcd.eVotingTallyAssistance.model;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import de.tud.vcd.eVotingTallyAssistance.common.exceptions.LoginVotingException;


/**
 * Der InvocationHandler dient als SchutzProxy für die Wahlurne. Es werden
 * jeweils nur bestimmte Methoden durchgelassen und andere verworfen. So sind
 * die Statusabfragen immer möglich, jedoch der Zugriff auf die
 * Editierfunktionen wird komplett gesperrt, da kein Login bei einer Einsicht
 * der Wahlurne notwendig ist. Es werden nur lesende Funktionen zugelassen.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 */
public class ReadonlyInvocationHandler implements InvocationHandler {

	private WahlurneInterface realSubject = null;

	public ReadonlyInvocationHandler(WahlurneInterface realSubject) {
		this.realSubject = (WahlurneInterface) realSubject;
	}

	/**
	 * Hier sind alle erlaubten Befehle aufgelistet, die nur lesend und nicht
	 * schreibend auf das Model zugreifen.
	 * 
	 * @author Roman Jöris <roman.joeris@googlemail.com>
	 * 
	 */
	private enum erlaubteBefehle {
		isEditWahlzettel, isGesperrt, getNextId, getAktiverWahlzettel, getErgebnis, getWahlvorstand, getWh1, getWh2, getErstelldatum, updateModel, isWahlzettelGueltig, loadWahlzettel, isStatusLOCK, isStatusEDIT, isStatusOPENBALLOT, isStatusINIT, isStatusWAITING, isStatusREADONLY, addObserver,

	};

	/**
	 * Prüft den Zugriff auf das reale Objekt im Modell. Dabei werden nur die in
	 * der ENUM befindlichen Befehle durchgelassen. Alle anderen führen zu einem
	 * Fehler.
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result = null;

		// Bestimmte Methoden immer durchlassen
		// alle anderen die schreibend drauf zugreifen würden, hier schon
		// ausschließen als Sicherheitsschleuse, die erlaubten
		// sind in der ENUM aufgelistet.
		int maxBefehle = erlaubteBefehle.values().length;

		for (int i = 0; i < maxBefehle; i++) {
			if (method
					.getName()
					.toLowerCase()
					.startsWith(
							erlaubteBefehle.values()[i].name().toLowerCase())) {
				result = method.invoke(realSubject, args);
				maxBefehle = -1;
			}
		}

		if (maxBefehle > -1) {
			// sonst gibt es einen Fehler
			throw new LoginVotingException(
					"Diese Anweisung ist im Lesemodus nicht erlaubt.");
		}

		return result;
	}

}
