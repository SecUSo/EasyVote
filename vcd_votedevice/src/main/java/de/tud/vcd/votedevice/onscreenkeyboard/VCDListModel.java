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
package de.tud.vcd.votedevice.onscreenkeyboard;

import javax.swing.DefaultListModel;

/**
 * Listmodell für die Elemente in der Ergebnisübersicht der Suche
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class VCDListModel extends DefaultListModel<VCDSearchable> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/* (non-Javadoc)
	 * @see javax.swing.DefaultListModel#getElementAt(int)
	 */
	public VCDSearchable getElementAt(int index) {
        Object s= super.getElementAt(index);
        return (VCDSearchable)s;
 	}
    
 
    /* (non-Javadoc)
     * @see javax.swing.DefaultListModel#addElement(java.lang.Object)
     */
    public void addElement(VCDSearchable obj) {
        this.add(this.size(), obj);
    	
    }

}
