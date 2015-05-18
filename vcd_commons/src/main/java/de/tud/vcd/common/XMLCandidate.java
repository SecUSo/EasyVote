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
