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
package de.tud.vcd.eVotingTallyAssistance.gui.wahlleiter;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import de.tud.vcd.eVotingTallyAssistance.common.exceptions.WahlleiterNichtKorrektException;


/**
 * Diese Klasse dient nur als Dummy zur Vervollständigung der Funktion des
 * Wahlmoduls. Der Wahlleiter wird hier über eine einfache Textdatei
 * authentifiziert. Dies soll später über eine SmartCard geschehen. Die Datei
 * liegt im Standardfall im Ordner config und heißt wahlleiter.ini. Die
 * Kennwörter werden jedoch nur mit normalen SHA1 Hash gespeichert und nicht
 * "gesalted" oder ähnliches.
 * 
 * Die Hashes können zum Beispiel online über die Seite:
 * http://www.sha1generator.de/ erzeugt werden und in der Datei gespeichert
 * werden.
 * 
 * ACHTUNG!!!!! Diese Klasse dient also wirklich nur als Zwischenlösung!!!!
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 */
public class Wahlleiter extends Component {

	private static final long serialVersionUID = 1L;
	private String filename;
	private URL file;

	/**
	 * Constructor mit definiertem Dateinamen aus dem die Wahlleiter gelesen
	 * werden sollen.
	 * 
	 * @param filename
	 */
	

	/**
	 * Constructor, der die Standarddatei zum laden der Wahlleiter nimmt.
	 */
	public Wahlleiter() {
		super();
		filename="wahlleiter.ini";
		//this.file= getClass().getClassLoader().getResource("wahlleiter.ini");
	}

	/**
	 * Legt eine Liste der Wahlleiter an, die im System vorhanden sind. Nicht
	 * produktiv eingebunden.
	 * 
	 * @return ArrayList<String> : Liste der Wahlleiter
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("unused")
	private ArrayList<String> getWahlleiter() throws FileNotFoundException,
			IOException {
		Properties props = new Properties();
		ArrayList<String> wahlleiter = new ArrayList<String>();
		//props.load(new FileInputStream(file.openStream())));
		props.load(this.getClass().getClassLoader().getResourceAsStream(filename));
		//props.load(file.openStream());
		Enumeration<?> propertyNames = props.propertyNames();
		while (propertyNames.hasMoreElements()) {
			wahlleiter.add((String) propertyNames.nextElement());
		}

		return wahlleiter;
	}

	/**
	 * Lädt das Kennwort des übergebenen Wahlleiters aus der beim Erzeugen der
	 * Instanz definierten Datei. Wird der String nicht gefunden, so wird ein
	 * leerer String zurückgeliefert.
	 * 
	 * @param wahlleiter
	 *            String: Der Name des Wahlleiters dessen Kennwort gesucht
	 *            werden soll.
	 * @return String: "" wenn nicht gefunden oder der SHA1 Hash als String von
	 *         dem gesuchten Wahlleiter.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOExcept	ion
	 */
	private String getWahlleiterPwd(String wahlleiter)
			throws FileNotFoundException, IOException {
		Properties props = new Properties();
		//props.load(file.openStream());
		props.load(this.getClass().getClassLoader().getResourceAsStream(filename));
		return props.getProperty(wahlleiter, "");

	}

	/**
	 * Setzt einen Wahlleiter mit Kennwort in die Datei.
	 * 
	 * @param wahlleiter
	 *            Name des Wahlleiters
	 * @param pwd
	 *            : Kennwort des Wahlleiters im Klartext
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws NoSuchAlgorithmException
	 */
	@SuppressWarnings("unused")
	private void setWahlleiterPwd(String wahlleiter, String pwd)
			throws FileNotFoundException, IOException, NoSuchAlgorithmException {
		Properties props = new Properties();
		props.load(new FileInputStream(filename));
		props.setProperty(wahlleiter, sha1(pwd));

		props.store(new FileOutputStream(filename, true),
				"Wahlleiterdatei mit SHA-1 Hashes");

	}

	/**
	 * Erzeugt einen SHA1 Wert vom übergebenen String. Übernommen von
	 * http://www.sha1-online.com/sha1-java/
	 * 
	 * @param pwd
	 * @return sha1-Wert
	 * @throws NoSuchAlgorithmException
	 */
	private static String sha1(String pwd) throws NoSuchAlgorithmException {
		MessageDigest sha1 = MessageDigest.getInstance("SHA1");
		byte[] result = sha1.digest(pwd.getBytes());
		StringBuffer hash = new StringBuffer();
		for (int i = 0; i < result.length; i++) {
			hash.append(Integer.toString((result[i] & 0xff) + 0x100, 16)
					.substring(1));
		}
		return hash.toString();
	}

	/**
	 * Dummyfunktion, die den Namen und das Kennwort des Wahlleiters abfragt und
	 * nur registrierte zuläßt.
	 * 
	 * @return String: der Name des Wahlleiters.
	 * @throws WahlleiterNichtKorrektException
	 *             : wird geworfen, wenn ein Algorithmusfehler der Hashfunktion
	 *             auftritt oder die Anmeldung abgebrochen wurde.
	 */
	public String checkWahlleiter() throws WahlleiterNichtKorrektException {
		String name = "";
		// Nach Namen fragen (solange bis ein gültiger Name in DB gefunden
		// wurde.
		String s = "";
		while (s.equals("")) {
			String input = JOptionPane.showInputDialog(this,
					"Der Name des Wahlleiters ist:", "Wahlleiter",
					JOptionPane.QUESTION_MESSAGE);
			if (input == null) {
				input = "";
			}
			try {
				s = getWahlleiterPwd(input);
			} catch (IOException e) {
				throw new WahlleiterNichtKorrektException(
						"Die Datei mit den Wahlleitern konnte nicht gelesen werden. "
								+ e.getMessage());
			}
			// System.out.println("hash:"+s);
			if (s.equals("")) {
				int nochmal = JOptionPane
						.showConfirmDialog(
								null,
								"Der Name ist erforderlich und der angegebene Name nicht im System. Bitte eingeben. Soll das Programm beendet werden?",
								"Warnung ", JOptionPane.YES_NO_OPTION);
				if (nochmal == JOptionPane.YES_OPTION) {
					// beenden einleiten
					throw new WahlleiterNichtKorrektException(
							"Die Anmeldung wird abgebrochen.");
				}
			} else {
				name = input;
			}
		}
		// Nach PWD fragen und dieses mit dem eingespeichertem vergleichen.
		// Event. so lange wiederholen, bis es korrekt ist.
		boolean pwdcheck = false;
		while (!pwdcheck) {
			// String pwd=JOptionPane.showInputDialog(this,
			// "Kennwort des Wahlleiters:", "Wahlleiter",
			// JOptionPane.QUESTION_MESSAGE);
			String pwd = showPasswordDialog();

			if (pwd == null) {
				pwd = "";
			}
			try {
				pwdcheck = s.equals(sha1(pwd));
			} catch (NoSuchAlgorithmException e) {
				throw new WahlleiterNichtKorrektException(
						"Das Programm kann die Kennwörter nicht vergleichen. Die Anmeldung wird abgebrochen.");
			}
			if (!pwdcheck) {
				int nochmal = JOptionPane
						.showConfirmDialog(
								null,
								"Das Kennwort ist falsch. Bitte korrekt eingeben. Soll das Programm beendet werden?",
								"Warnung ", JOptionPane.YES_NO_OPTION);
				if (nochmal == JOptionPane.YES_OPTION) {
					// beenden einleiten
					throw new WahlleiterNichtKorrektException(
							"Die Anmeldung wird abgebrochen.");
				}
			}
		}

		return name;
	}

	/**
	 * Fragt die Kennworteingabe des Wahlleiters als Sternchentext ab. Dient zur
	 * "sicheren" Eingabe des Wahlleiterkennworts.
	 * 
	 * @return übergebener String
	 */
	public String showPasswordDialog() {
		JPasswordField passwordField = new JPasswordField(10);
		JOptionPane.showConfirmDialog(null, passwordField,
				"Wahlleiterkennwort eingeben: ", JOptionPane.OK_CANCEL_OPTION);
		return String.valueOf(passwordField.getPassword());
	}

}
