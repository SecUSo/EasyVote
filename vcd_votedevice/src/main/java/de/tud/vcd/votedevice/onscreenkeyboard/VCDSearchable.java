package de.tud.vcd.votedevice.onscreenkeyboard;


/**
 * 
 * Interface welches implementiert werden muss, wenn das Objekt durchsucht werden k�nnen soll durch den Searcher-
 * 
 * @author Roman J�ris <roman.joeris@googlemail.com>
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