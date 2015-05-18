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
