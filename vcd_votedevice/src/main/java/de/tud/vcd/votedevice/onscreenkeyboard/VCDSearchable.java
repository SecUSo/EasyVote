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