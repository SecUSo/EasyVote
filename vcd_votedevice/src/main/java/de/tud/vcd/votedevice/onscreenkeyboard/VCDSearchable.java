package de.tud.vcd.votedevice.onscreenkeyboard;


/**
 * 
 * Interface welches implementiert werden muss, wenn das Objekt durchsucht werden können soll durch den Searcher-
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public interface VCDSearchable {

	/**
	 * Ruft den externen Searcher auf und bindet die eigenen Datenfelder an diesen.
	 * @param s
	 * @param str
	 */
	public abstract void search(Searcher s, String str);
	

}