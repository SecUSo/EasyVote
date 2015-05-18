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
package de.tud.vcd.eVotingTallyAssistance.controller;

/**
 * Definiert die erlaubten Befehle, die die GUI an den Controller schicken darf.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class ControllerCalls {

	public enum Calls {
	   LOADIMAGE, INVALID,LOADWAHLURNE, SCAN,BARCODE,LOADBALLOT, EDITBALLOT, SUBMITCHANGES, DISCARDCHANGES, SUBMITBALLOT, DISCARDBALLOT, LOCKUSER, CLOSEPROGRAM,CLOSEPROGRAMDIRECT, LOGIN 
	}
	
	/**
	 * Gibt den ENUM Wert des übergebenen Strings zurück. Wenn er nicht gefunden wird, wird er auf INVALID gesetzt.
	 * @param str String Name des Calls
	 * @return ENUM Calls der wirkliche ENUM Typ.
	 */
	public static Calls getValue(String str) {
		for (Calls c:  Calls.values()){
			if (str.equals(c.name())){
				return c;
			}
		}
		
		return Calls.INVALID;
	}

	
}
