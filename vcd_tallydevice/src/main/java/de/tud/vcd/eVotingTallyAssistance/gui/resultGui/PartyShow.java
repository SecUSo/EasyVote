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
package de.tud.vcd.eVotingTallyAssistance.gui.resultGui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import de.tud.vcd.eVotingTallyAssistance.common.ConfigHandler;
import de.tud.vcd.eVotingTallyAssistance.common.ConfigHandler.ConfigVars;
import de.tud.vcd.eVotingTallyAssistance.common.exceptions.ConfigFileException;

/**
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class PartyShow extends CandidateShow {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private JLabel lblName;
	private int fontSize;
	private int fontSizeMax=20;
	/**
	 * @param id
	 * @param name
	 * @param votes
	 */
	public PartyShow(int id, String name) {
		super(id, name, 0);
		removeAll();
		String fontSizeMaxString="20";
		try {
			fontSizeMaxString=ConfigHandler.getInstance().getConfigValue(ConfigVars.RESULT_PARTY_FONTSIZE_MAX);
		} catch (ConfigFileException e1) {
			//Wenn er nicht laden kann, dann Basiswert nehmen
			fontSizeMaxString="20";
		}
		fontSizeMax= Integer.parseInt(fontSizeMaxString);
		addComponentListener(new ComponentListener() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				int breite= getWidth()-10;
				System.out.println("Größe ist:"+ breite+"x"+getHeight());
				
				for (int i=1;i<fontSizeMax;i++){
					fontSize=i;
					FontMetrics fm=lblName.getFontMetrics(new Font("Sans Sarif", Font.BOLD, fontSize));
					System.out.println(lblName.getText());
					int fontWidth=fm.stringWidth(lblName.getText());
					System.out.println("Ist Wert: "+fontWidth+" Soll wert: "+breite);
					if (fontWidth>breite){
						break;
					}
				}	
				lblName.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize-1));
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
				// nothing to do
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {
				// nothing to do
				
			}

			@Override
			public void componentShown(ComponentEvent e) {
				// nothing to do				
			}
		});
		this.name = name;

		setBackground(new Color(0xBBBBBB));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		// Die Größe definieren, dabei die dynamische Entwicklung beachten
		setMinimumSize(new Dimension(600, 50));
		setPreferredSize(new Dimension(600, 50));
		setMaximumSize(new Dimension(1900, 100));
		
		// den Rest konfigurieren
		setBorder(new LineBorder(Color.LIGHT_GRAY));
		
		lblName = new JLabel(String.valueOf(this.name));
//		lblName.setMinimumSize(new Dimension(20, 40));
//		lblName.setPreferredSize(new Dimension(20, 40));
//		lblName.setMaximumSize(new Dimension(50, 40));

		//lblName.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
		add(Box.createHorizontalStrut(5));
		add(lblName);
		// einen kleinen Abstand zum Rand schaffen
//		add(Box.createHorizontalStrut(5));
//		lblVotes = new JLabel(String.valueOf(this.votes));
//		lblVotes.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
//		lblVotes.setAlignmentX(RIGHT_ALIGNMENT);
//		add(lblVotes);
		add(Box.createHorizontalStrut(5));
	}
	
	public void changeColor(Color c) {
		//setBackground(c);
	}

	/**
	 * Setzt die Farbe wieder auf die Ausgangsfarbe zurück, um so die Markierung
	 * aufzuheben.
	 */
	public void resetColor() {
		//setBackground(resetcolor);
	}

	/**
	 * Ändert die angezeigte Stimmenanzahl für dieses Ergebnisfeld
	 * 
	 * @param votes
	 *            int Anzahl an Stimmen
	 */
	public void changeVotes(int votes) {
		//lblVotes.setText(String.valueOf(votes));
	}

	/**
	 * Liest die Anzahl an Stimmen aus. Dies wird vom übergeordneten Fenster
	 * benötigt, um zu prüfen, ob sich was geändert hat.
	 * 
	 * @return String Anzahl der Stimmen, jedoch noch als Text!
	 */
	public String getVote() {
		//return lblVotes.getText();
		return "0";
	}

}
