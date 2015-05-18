package de.tud.vcd.eVotingTallyAssistance.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.CodeSource;
import java.util.Properties;
import java.util.TreeMap;

import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.ElementException;
import org.simpleframework.xml.core.Persister;

import de.tud.vcd.eVotingTallyAssistance.common.exceptions.ConfigFileEntryNotKnownException;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.ConfigFileException;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.ConfigFileNotFoundException;


/**
 * Liefert den Zugriff auf die Konfigurationsvariablen. Diese werden aus dem
 * Unterverzeichnis config aus der Datei config.xml geladen und bieten Zugriff
 * auf alle realisierten Einstellmöglichkeiten. Sobald ein Wert in der Config
 * geändert wird, wird auch probiert die Datei wieder ins Dateisystem zu
 * schreiben.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 */
@Root(name = "evotingconfig")
public class ConfigHandler {
	/**
	 * Erlaubte Werte auf die zugegriffen werden darf
	 */
	public enum ConfigVars {
		LOADFROMFILE, MACHINEID, WAHLZETTELDESIGNDATEI, MONITORTALLYGUI, MONITORRESULTGUI, PLUSMINUSSTEP, PAPERSIZE, RESULTCOLOR1, RESULTCOLOR2,
		RESULT_PARTY_FONTSIZE_MAX, AUSWERTUNGSPROTOKOLL, BALLOTCARD_EQUAL_COLOR
	}

	public static String filename = "config.xml";

	@Path("configparameter")
	@ElementMap(entry = "config", key = "key", attribute = true, inline = true)
	public static TreeMap<ConfigVars, String> config;

	private static ConfigHandler instance = null;

	/**
	 * Legt beim Erstellen des Objekts schonmal alle Keys an, und initialisiert
	 * sie mit dem leeren String. Dies ist für das erste Speichern notwendig.
	 */
	private ConfigHandler() {
		config = new TreeMap<ConfigVars, String>();
		for (ConfigVars ck : ConfigVars.values()) {
			if (!config.containsKey(ck)) {
				config.put(ck, "");
			}
		}
	}

	/**
	 * liefert die Instanz des Singleton Pattern. So wird das Objekt nur einmal
	 * angelegt und verfügt über das Wissen. Wenns noch nicht verfügbar ist,
	 * dann erstellt es die Liste und legt auch alle Configs an und speichert
	 * diese.
	 * 
	 * @return
	 * @throws ConfigFileNotFoundException
	 * @throws ConfigFileEntryNotKnownException
	 */
	public static synchronized ConfigHandler getInstance()
			throws ConfigFileException {
		if (instance == null) {
			// instance= new ConfigHandler();
			instance = loadXML();
			instance.writeConfig();
		}
		return instance;
	}
	
	
	 public static String getJarExecutionDirectory()
	  {
	    File propertiesFile = new File("");
	 
	    if (!propertiesFile.exists())
	    {
	      try
	      {
	        CodeSource codeSource = ConfigHandler.class.getProtectionDomain().getCodeSource();
	        File jarFile = new File(codeSource.getLocation().toURI().getPath());
	        String jarDir = jarFile.getParentFile().getPath();
	        propertiesFile = new File(jarDir + System.getProperty("file.separator") );
	        return propertiesFile.getAbsolutePath()+System.getProperty("file.separator");
	      }
	      catch (Exception ex)
	      {
	      }
	    }
	 
	    return System.getProperty("file.separator");
	  }

	/**
	 * lädt die XML Datei in die Klasse rein. Sollte die Datei nicht gefunden
	 * werden, wird null zurückgegeben.
	 * 
	 * @return
	 * @throws ConfigFileNotFoundException
	 * @throws ConfigFileEntryNotKnownException
	 */
	private static ConfigHandler loadXML() throws ConfigFileException {
		try {
			Serializer deserializer = new Persister();
			String configFile=getJarExecutionDirectory()+filename;
			File source = new File(configFile);
			//System.out.println(source.getAbsolutePath());
			//System.out.println(configFile);
			return deserializer.read(ConfigHandler.class, source);
			// System.out.println(example.election_name);
		} catch (FileNotFoundException e) {
			ConfigHandler ch = new ConfigHandler();
			ch.writeConfig();
			throw new ConfigFileNotFoundException(
					"Die Konfigurationsdatei wurde nicht gefunden. Es wird probiert sie automatisch zu erstellen.");
		} catch (IllegalArgumentException e) {
			// Ein Eintrag ist unbekannt. Aber nicht gleich alles überschreiben,
			// sondern auf den Eintrag hinweisen.
			int punkt = e.getMessage().lastIndexOf(".");
			String fehler = e.getMessage().substring(punkt + 1);
			throw new ConfigFileEntryNotKnownException("Der Parameter '"
					+ fehler + "' ist unbekannt und muss gelöscht werden.");
		} catch (ElementException e) {
			// Ein übergeordnetes Element ist unbekannt. Aber nicht gleich alles
			// überschreiben, sondern auf den Eintrag hinweisen.
			int punkt1 = e.getMessage().indexOf("'");
			int punkt2 = e.getMessage().lastIndexOf("'");
			String fehler = e.getMessage().substring(punkt1 + 1, punkt2);
			throw new ConfigFileEntryNotKnownException("Das Element '" + fehler
					+ "' ist unbekannt und muss gelöscht werden.");
		} catch (Exception e) {
			throw new ConfigFileException(
					"Die Konfigurationsdatei ist defekt. Datei korrigieren oder löschen, um sie neu erstellen zu lassen.");
		}
	}

	/**
	 * Liefert den Wert für eine Configvariable zurück. Als Key können alle
	 * Werte aus der Auflistung ConfigVars verwendet werden.
	 * 
	 * @param key
	 *            : Key aus der ENUM DesignKeys.
	 * @return String : Wert der Variable
	 */
	public String getConfigValue(ConfigVars key) {
		return config.get(key);
	}

	/**
	 * speichert eine Konfigvariable in der Datei.
	 * 
	 * @param key
	 * @param value
	 * @throws ConfigFileException
	 */
	public void setConfigValue(ConfigVars key, String value)
			throws ConfigFileException {
		config.put(key, value);
		writeConfig();
	}

	/**
	 * speichert die ConfigDatei mit den aktuellen Werten.
	 * 
	 * @throws ConfigFileException
	 */
	public void writeConfig() throws ConfigFileException {
		Serializer serializer = new Persister();
		File file = new File(getJarExecutionDirectory()+filename);
		// wenn die Instanz noch nicht existiert, dann erstellt es eine leeren
		// ConfigHandler...
		if (instance == null) {
			// System.out.println("New Handler writing...");
			instance = new ConfigHandler();
		}
		;
		// und versucht diesen zu speichern
		try {
			// System.out.println("Konfig schreiben");
			serializer.write(instance, file);
		} catch (Exception e) {
			throw new ConfigFileException(
					"Die Konfigurationsdatei konnte nicht geschrieben werden. Überprüfen Sie, ob das Programm genügend Rechte zum Schreiben der Datei config/config.xml hat.");
		}
	}

}
