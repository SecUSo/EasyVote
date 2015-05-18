package de.tud.vcd.eVotingTallyAssistance.model;

import java.util.Date;
import java.util.Observer;

import de.tud.vcd.eVotingTallyAssistance.common.exceptions.UrneIdUnbekannt;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.UrneSpeichernException;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.WahlhelferException;


/**
 * Das Interface für die Wahlurne, um sie mit einem InvocationHandler zu schützen. Sie beinhaltet alle Funktionen der Wahlurne.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public interface WahlurneInterface {

	/**
	 * @return the editWahlzettel
	 */
	public abstract boolean isEditWahlzettel();

	public abstract boolean isGesperrt();

	public abstract boolean login(String pwd1, String pwd2) throws WahlhelferException;

	public abstract boolean logout() throws Exception;

	public abstract int getNextId();
	
	public abstract void addObserver(Observer o);

	public abstract void createBackupOfAktiverWahlzettel();

	public boolean isWahlzettelGueltig();
	
	public void saveUrne(String filename)throws UrneSpeichernException;
	
	/**
	 * @return the aktiverWahlzettel
	 * @throws Exception 
	 */
	public abstract Wahlzettel getAktiverWahlzettel() throws Exception;

	/**
	 * @return the ergebnis
	 */
	public abstract Wahlergebnis getErgebnis();

	/**
	 * @return the wahlvorstand
	 */
	public abstract String getWahlvorstand();

	/**
	 * @return the wh1
	 */
	public abstract Wahlhelfer getWh1();

	/**
	 * @return the wh2
	 */
	public abstract Wahlhelfer getWh2();

	/**
	 * @return the erstelldatum
	 */
	public abstract Date getErstelldatum();

	/**
	 * @param aktiverWahlzettel
	 *            the aktiverWahlzettel to set
	 * @throws Exception
	 */
	public abstract void setAktiverWahlzettel(Wahlzettel aktiverWahlzettel)
			throws Exception;

	public abstract void editAktiverWahlzettel() throws Exception;

	public abstract void submitEditWahlzettel() throws Exception;

	public abstract void discardEditWahlzettel() throws Exception;

	public abstract int commitWahlzettel() throws Exception;

	public abstract void discardWahlzettel() throws Exception;

	public abstract void loadWahlzettel(int id) throws UrneIdUnbekannt,Exception;

	public abstract boolean isStatusLOCK();

	public abstract boolean isStatusEDIT();

	public abstract boolean isStatusOPENBALLOT();

	public boolean isStatusWAITING();
	
	public boolean isStatusINIT();
	
	public boolean isStatusREADONLY();
	
	public void updateModel();

	public abstract boolean isEqualToPartyVote(Wahlzettel aktiverWahlzettel);

}