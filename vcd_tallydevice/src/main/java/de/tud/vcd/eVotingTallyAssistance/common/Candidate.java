/**
 * 
 */
package de.tud.vcd.eVotingTallyAssistance.common;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */

/**
 * Stellt einen Kandidaten mit Id, Name, Partei und Stimmenanzahl dar. Das Feld
 * der Partei wird bisher noch nicht verwendet, jedoch ist dies vielleicht bei
 * der Wahlzettelerzeugung interessant. Bei der Auszählung wird sie bewußt nicht
 * näher betrachtet, da so ein neutraleres Auswerten möglich ist, da das meiste
 * über die Id läuft und daher unpersönlich ist.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 */
@Root
public class Candidate {
	@Attribute
	private int id;
	@Attribute
	private String name;
	@Attribute
	private String prename;
	@Attribute(required = false)
	private String partei = "";
	private int votes;
	
	//Juri: This variable should help identify if a candidate has been manually selected or not
	private boolean mselected;

	/**
	 * Erzeugt einen Kandidtaen mit den Werten id, Name, Partei und Stimmanzahl
	 * 
	 * @param id
	 * @param name
	 * @param partei
	 * @param votes
	 */
	public Candidate(int id, String name, String partei, int votes, boolean selected) {
		super();
		this.id = id;
		this.name = name;
		this.partei = partei;
		this.votes = votes;
		this.mselected = false;
	}

	public Candidate(@Attribute(name = "id") int id,
			@Attribute(name = "name") String name,
			@Attribute(name = "prename") String prename,
			@Attribute(name = "partei") String partei) {
		super();
		this.id = id;
		this.name = name;
		this.prename=prename;
		this.partei = partei;
		this.mselected = false;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name+", "+prename;
	}

	public int getVotes() {
		return votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}

	/**
	 * fügt dem Kandidaten Stimmen hinzu.
	 * 
	 * @return
	 */
	public int addVote() {
		this.votes++;
		return this.votes;
	}
	
	
	//Juri: This method should enable to set to true a candidate that has been manually selected
	public void setManual(){
		this.mselected = true;
	}

}
