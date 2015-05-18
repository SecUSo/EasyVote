package de.tud.vcd.votedevice.controller;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;


/**
 * Zentrale Klasse zum übersetzen von Textfragmenten.Arbeitet als Singleton und liefert aus der gewählten Sprache 
 * die übersetzten Texte. Die Texte werden in der language_XX.properties Datei gespeichert. Dabei steht XX für den 
 * Ländercode der Locationsklasse.
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class Language {

	private static Language instance = null;
	
	private static String DEFAULT_LANGUAGE="de";
	
	private HashMap<String, String> translations;
	//private String language;
	
	private Locale lang;
	
	/**
	 * Erzeugt die Klasse und liest die Standardsprache ein. Ist als Private deklariert und verhindert somit die Erstellung
	 * durch eine andere Klasse.
	 */
	private Language() {
		try {
			readLanguage(DEFAULT_LANGUAGE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	/**
	 * liefert die Instanz des Singleton Pattern. So wird das Objekt nur einmal
	 * angelegt und verfügt über das Wissen.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static synchronized Language getInstance(){
		if (instance == null) {
			instance = new Language();
		}
		return instance;
	}
	
	/**
	 * Liest die Sprache ein. Dabei wird ein zweibuchstabiger Ländercode übergeben. Dieser muss mit der Properies datei matchen
	 * @param language
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void readLanguage(String language) throws Exception{
		try{
			//Erzeugen, falls nicht vorhanden
			if (translations==null){
				translations=new HashMap<String, String>();
			}
			//Übersetzungen löschen
			translations.clear();
			//Übersetzungen aus Datei einlesen
			
			BufferedInputStream stream;
			InputStream filename=getClass().getClassLoader().getResource("language_"+language+".properties").openStream();
			stream = new BufferedInputStream(filename);
			Properties properties;
			properties = new Properties();
			properties.load(stream);//  loadFromXML(stream);
			stream.close();
			//jede Property in die hashmap umsetzen um unabhängig zu werden
			translations.putAll((Map)(properties));
			
		}finally{
			//prüfen, ob Liste voll ist, ansonsten default lesen
			if (translations==null || translations.isEmpty()){
				throw new Exception("Sprache nicht vorhanden");
			}else{
				//Festhalten, welche Sprache geladen ist:
				this.lang=new Locale(language);
			}
		}
	}
	
	/**
	 * Liefert für den übergebenen Key die Übersetzung zurück
	 * @param key String
	 * @return String
	 */
	public String getString(String key){
		if (translations.containsKey(key)){
			return translations.get(key);
		}else{
			return key;
		}
		
	}
	
	/**
	 * Setzt eine neue zentrale Sprache. Übergeben wird ein zweibuchstabiger Ländercode
	 * @param language String
	 */
	public void setLanguage(String language){
		try {
			readLanguage(language);
			//System.out.println("Sprache gewechselt auf "+lang.getDisplayLanguage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Liest die aktuelle Sprache als Länderkürzel aus.
	 * @return String (z.B. de oder en)
	 */
	public String getLanguage(){
		return lang.getLanguage();
	}
	
	/**
	 * Liefert den Ländernamen zurück
	 * @return
	 */
	public String getLanguageName(){
		return lang.getDisplayLanguage();
	}
	

}
