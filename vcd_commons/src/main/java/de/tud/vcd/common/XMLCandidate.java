package de.tud.vcd.common;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root
public class XMLCandidate implements CandidateImportInterface {
	@Attribute
	private int id;
	@Attribute
	private String name;
	@Attribute(required = false)
	private String partei = "";
	
	@Attribute(required = false)
	private String prename = "";




	public XMLCandidate(@Attribute(name = "id") int id,
			@Attribute(name = "name") String name,
			@Attribute(name = "prename") String prename,
			@Attribute(name = "partei") String partei) {
		super();
		this.id = id;
		this.name = name;
		this.prename=prename;
		this.partei = partei;
	}

	/* (non-Javadoc)
	 * @see de.tud.vcd.common.CandidateImportInterface#getId()
	 */
	public int getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see de.tud.vcd.common.CandidateImportInterface#getName()
	 */
	public String getName() {
		return name;
	}



	/* (non-Javadoc)
	 * @see de.tud.vcd.common.CandidateImportInterface#getParty()
	 */
	public String getParty(){
		return partei;
	}

	public String getPrename() {
		return prename;
	}

}
