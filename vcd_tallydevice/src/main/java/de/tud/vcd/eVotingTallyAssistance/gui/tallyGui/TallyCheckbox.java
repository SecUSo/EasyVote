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
/**
 * 
 */
package de.tud.vcd.eVotingTallyAssistance.gui.tallyGui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JCheckBox;

/**
 * Eine spezialisierte Version der Checkbox, um die Id in der Checkbox und die
 * Position zu speichern. Damit ist ein sinnvolles zuordnen der
 * ItemListenerEvents möglich, da die Checkbox weiß für wen sie zuständig ist
 * und an welcher Position sie steht.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 */
public class TallyCheckbox extends JCheckBox {
	
	public enum CheckboxState {VALID, INVALID, UNCHECKED};
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id; // Id auf dem Feld
	private int position;// Spalte der Checkbox
	private int candId; //wirkliche Kandidatenposition
	private CheckboxState state; //Den Zustand, ob die Checkbox angekreuzt ist oder nicht
    private Color col;
	

	/**
	 * Erzeugt eine neue Checkbox. Dabei kann direkt die Id, die Position und
	 * der Status übergeben werden. Dies muss nur einmal beim erzeugen gesetzt
	 * werden.
	 * 
	 * @param selected
	 *            Boolean, ob die Box angekreuzt ist oder nicht.
	 * @param id
	 *            int Zeile in der die Checkbox steht
	 * @param position
	 *            int Spalte in der die Checkbox steht
	 */
	public TallyCheckbox(boolean selected, int id, int position) {
		super("", selected);
		this.id = id;
		this.position = position;
		setOpaque(true);
		state=CheckboxState.UNCHECKED;
		//setBackground(Color.GREEN);
		this.col = Color.WHITE;
	}

	/**
	 * @return the candId
	 */
	public int getCandId() {
		return candId;
	}

	/**
	 * @param candId the candId to set
	 */
	public void setCandId(int candId) {
		this.candId = candId;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		//setSize(getHeight(), getHeight());
		
		
		getParent().invalidate();
		int size=getHeight();
		Dimension d=new Dimension(size, size);
		setPreferredSize(d);
		setMaximumSize(d);
		setMinimumSize(d);
		setSize(d);
		getParent().validate();
		//super.paintComponent(g);
		Graphics2D g2d=(Graphics2D)g;
		
		// Antialiasing einschalten
		g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, 
			RenderingHints.VALUE_ANTIALIAS_ON );
//		if (state.equals(CheckboxState.VALID)){
//			g2d.setColor(new Color(0x90ee90));
//		}else{
//			
//		}
		g2d.setColor(super.getBackground());
		g2d.fillRect(0, 0, getWidth(), getHeight());
		int margin=2;
		int maxSize=Math.min(this.getWidth(), this.getHeight());
		int offset=(this.getWidth()-maxSize)/2;
		
		if (state.equals(CheckboxState.VALID)){
			//g2d.setColor(new Color(0x90ee90));
			g2d.setColor(Color.WHITE);
		}else if (state.equals(CheckboxState.INVALID)){
			g2d.setColor(new Color(0xffa500));
		}else{
			g2d.setColor(Color.WHITE);
		}
		g2d.fill3DRect(offset+margin, 0+margin, maxSize-2*margin,maxSize-2*margin, true);
		g2d.setColor(Color.BLACK);
		
		g2d.draw3DRect(offset+margin, 0+margin, maxSize-2*margin,maxSize-2*margin, false);
		
		if(this.isSelected()){
			//Juri: this three lines make sure that the checkbox is orange
			g2d.setColor(this.col);
			g2d.fill3DRect(offset+margin, 0+margin, maxSize-2*margin,maxSize-2*margin, false);
			g2d.setColor(Color.BLACK);
			
			g2d.drawLine(offset+margin, 0+margin, offset+margin+maxSize-2*margin, 0+margin+maxSize-2*margin);
			g2d.drawLine(offset+margin, 0+margin+maxSize-2*margin, offset+margin+maxSize-2*margin,  0+margin);
		}
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.AbstractButton#setSelected(boolean)
	 */
	@Override
	public void setSelected(boolean b) {
		super.setSelected(b);
		state=(b)?CheckboxState.VALID:CheckboxState.UNCHECKED;
	}
	
	public void setSelected(CheckboxState b) {
		switch(b){
			case VALID:
				setSelected(true);
				break;
			case UNCHECKED:
				setSelected(false);
				break;
			case INVALID:
				setSelected(true);
				break;
				
			default:
				break;
		}
		state=b;
		repaint();
	}
	
	
	public Color setcolor(Color color){
		return this.col = color;
	}

}
