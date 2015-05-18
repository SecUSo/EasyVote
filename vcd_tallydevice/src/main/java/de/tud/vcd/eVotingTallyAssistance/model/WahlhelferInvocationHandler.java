package de.tud.vcd.eVotingTallyAssistance.model;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import de.tud.vcd.eVotingTallyAssistance.common.exceptions.LoginVotingException;


/**
 * Der InvocationHandler dient als SchutzProxy für die Wahlurne. Es werden
 * jeweils nur bestimmte Methoden durchgelassen und andere verworfen. So sind
 * die Statusabfragen immer möglich, jedoch der Zugriff auf die
 * Editierfunktionen erst, wenn ein Login erfolgt ist. Generell sind nach einem
 * Login alle Funktionen der Wahlurne verfügbar.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 */
public class WahlhelferInvocationHandler implements InvocationHandler {

	private WahlurneInterface realSubject = null;

	public WahlhelferInvocationHandler(WahlurneInterface realSubject) {
		this.realSubject = (WahlurneInterface) realSubject;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result = null;

		// Bestimmte Methoden immer durchlassen
		if (method.getName().startsWith("login")) {
			result = method.invoke(realSubject, args);
		} else if (method.getName().startsWith("isGesperrt")) {
			result = method.invoke(realSubject, args);
		} else if (method.getName().startsWith("isStatus")) {
			result = method.invoke(realSubject, args);
		} else if (method.getName().startsWith("updateModel")) {
			result = method.invoke(realSubject, args);
		} else {
			// alle anderen nur bei erfolgreicher Anmeldung
			if (!((Wahlurne) realSubject).isGesperrt()) {
				result = method.invoke(realSubject, args);
			} else {
				// sonst gibt es einen Fehler
				throw new LoginVotingException(
						"Zum Weiterarbeiten müssen sich beide Wahlhelfer wieder anmelden.");
			}
		}

		return result;
	}

}
