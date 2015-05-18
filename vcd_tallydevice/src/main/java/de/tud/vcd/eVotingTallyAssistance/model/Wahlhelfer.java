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
package de.tud.vcd.eVotingTallyAssistance.model;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import de.tud.vcd.eVotingTallyAssistance.common.exceptions.WahlhelferException;


/**
 * Definiert einen Wahlhelfer mit Namen, Kennwort und dem Status, ob er
 * eingeloggt ist. Die Kennwörter werden mit dem Namen gesalzen (salted
 * passwords), um den Schutz vorm Ausspähen zu erhöhen, wenn man die Hashes
 * auslesen würde.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 */
public class Wahlhelfer {
	private String name; // Name des Wahlhelfers
	private String pwdHash; // PwdHash des Wahlhelferkennworts
	private boolean active; // Status, ob ein gültiger Login vorliegt.

	/**
	 * Erzeugt einen Wahlhelfer mit Namen und Kennwort, aber setzt ihn noch
	 * nicht als aktiv!
	 * 
	 * @param name
	 * @param password
	 * @throws WahlhelferException
	 */
	public Wahlhelfer(String name, String password) throws WahlhelferException {
		this.name = name;
		this.pwdHash = hashPassword(password);
		this.active = false;
	}

	/**
	 * Versucht den User mit dem übergebenen Kennwort anzumelden und liefert den
	 * Status davon zurück.
	 * 
	 * @param password
	 *            Kennwort zum einloggen
	 * @return Boolean Loginstatus
	 * @throws WahlhelferException
	 */
	public boolean login(String password) throws WahlhelferException {
		String newHash = hashPassword(password);
		if (newHash.equals(pwdHash)) {
			this.active = true;
		} else {
			this.active = false;
		}
		return this.active;
	}

	/**
	 * Meldet den angemeldeten Wahlhelfer ab bzw. sperrt ihn so dass er sich
	 * erneut anmelden muss.
	 * 
	 * @return Boolean Loginzustand
	 */
	public boolean logout() {
		this.active = false;
		return this.active;
	}

	public boolean isActive() {
		return active;
	}

	public String getName() {
		return name;
	}

	/**
	 * hashed das übergebene Kennwort (momentan mit sha1). Dabei wird es zudem
	 * gesalzen, um ein zufällig gleiches Kennwort nicht als solches erkennbar
	 * zu machen.
	 * 
	 * @param pwd
	 *            String Kennwort des Wahlhelfers
	 * @return String SHA1 String
	 * @throws WahlhelferException
	 */
	private String hashPassword(String pwd) throws WahlhelferException {
		String sha1 = "";
		// Prüfen, dass der Name schon gesetzt ist.
		try {

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			if (name == "")
				throw new WahlhelferException(
						"Der Name des Wahlhelfers ist nicht eingetragen. Kennwort kann deswegen nicht berechnet werden.");
			// Hash "salt-en" mit dem Namen des Wahlhelfers, damit zwei
			// zufällige Kennwörter nicht gleich aussehen.
			String toHash = name + pwd;
			// hashen...
			MessageDigest hashAlgo = MessageDigest.getInstance("SHA-1");
			hashAlgo.reset();
			hashAlgo.update(toHash.getBytes("UTF-8"));
			sha1 = byteToHex(hashAlgo.digest());
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			throw new WahlhelferException(
					"Es trat ein Fehler beim Erzeugen der Hashwerte für den Wahlhelfer auf. ");
		}
		return sha1;
	}

	/**
	 * wird innerhalb der hashPassword aufgerufen, um aus dem ByteArray einen
	 * String zu machen
	 * 
	 * @param hash
	 *            : das Bytearray welches umgewandelt werden soll
	 * @return String : der Hashwert als String
	 */
	private static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String str = formatter.toString();
		formatter.close();
		return str;
	}

}
