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
package de.tud.vcd.votedevice.view;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Observable;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import de.tud.vcd.votedevice.multielection.VCDListener;
import de.tud.vcd.votedevice.multielection.VCDView;
import de.tud.vcd.votedevice.multielection.exceptions.CannotConvertListenerException;

/**
 * Abstrakte Klasse die die Bildschirmaufteilung festlegt. In dieser können dann
 * die Wahlen dargestellt werden. Damit wird gewährleistet dass die Aufteilung
 * gleichbleibend aussieht.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 */
public abstract class VCDElectionContent extends JPanel implements VCDView {

	private enum Design {
		ONEPARTS, TWOPARTS, THREEPARTS
	};

	JPanel oneParts;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel twoParts;
	protected JPanel threeParts;
	protected VotingDeviceGui vdg;
	JPanelGradient jmenuBig;
	protected JPanelGradient jmenuSmall;
	protected JStatusBar statusBar;
	protected JPanel contentSmallGiant;
	JPanel contentSmallBig;
	public JPanel contentSmallSmall;
	Design design;

	/**
	 * Erzeugt eine Oberfläche für die Darstellung der Wahl
	 * 
	 * @param vdg
	 */
	public VCDElectionContent(VotingDeviceGui vdg) {
		Dimension d = vdg.getContent().getSize();
		System.out.println(d.toString());
		this.setBounds(0, 0, d.width, d.height);
		this.setVisible(true);
		this.vdg = vdg;
		this.setLayout(null);
		// this.setBackground(Color.GREEN);
		oneParts = new JPanel();
		twoParts = new JPanel();
		threeParts = new JPanel();
		oneParts.setLayout(null);
		twoParts.setLayout(null);
		threeParts.setLayout(null);
		oneParts.setBounds(0, 0, d.width, d.height);
		twoParts.setBounds(0, 0, d.width, d.height);
		threeParts.setBounds(0, 0, d.width, d.height);
		threeParts.setBackground(Color.ORANGE);

		// initialize components
		int infoWidth = vdg.getScreenSize().width - d.width;

		contentSmallGiant = new JPanel();
		contentSmallGiant.setBounds(0, 0, d.width, d.height);
		contentSmallGiant.setBackground(Color.WHITE);
		contentSmallGiant.setLayout(null);
		jmenuBig = new JPanelGradient();
		jmenuBig.setBounds(0, 0, infoWidth, d.height);

		contentSmallBig = new JPanel();
		contentSmallBig.setBounds(infoWidth, 0, d.width - infoWidth, d.height);
		contentSmallBig.setBackground(Color.WHITE);

		// /////////////////
		int statusHeight = percentToPixel(4.5, vdg.getScreenSize().height);
		jmenuSmall = new JPanelGradient();
		jmenuSmall.setBounds(0, statusHeight, infoWidth, d.height
				- statusHeight);
		jmenuSmall.setLayout(new BoxLayout(jmenuSmall, BoxLayout.Y_AXIS));

		contentSmallSmall = new JPanel();
		contentSmallSmall.setBounds(infoWidth, statusHeight, d.width
				- infoWidth, d.height - statusHeight);
		contentSmallSmall.setBackground(Color.WHITE);
		contentSmallSmall.setLayout(null);

		statusBar = new JStatusBar();
		statusBar.setBounds(0, 0, d.width, statusHeight);
		statusBar.setText("Testinhalt aus der VCDElectionContent Erzeugung.");
		
		oneParts.add(contentSmallGiant);
		twoParts.add(jmenuBig);
		twoParts.add(contentSmallBig);
		threeParts.add(jmenuSmall);
		threeParts.add(contentSmallSmall);
		threeParts.add(statusBar);

		this.add(oneParts);
		oneParts.setVisible(false);
		this.add(twoParts);
		twoParts.setVisible(false);
		this.add(threeParts);
		vdg.setContent(this);
		threeParts.setVisible(false);
		
	}

	/**
	 * Es wird nur ein Bereich angezeigt
	 */
	public void setContentToOneParts() {
		oneParts.setVisible(true);
		twoParts.setVisible(false);
		threeParts.setVisible(false);
		design = Design.ONEPARTS;
	}

	/**
	 * Es werden zwei Bereiche angezeigt
	 */
	public void setContentToTwoParts() {
		oneParts.setVisible(false);
		twoParts.setVisible(true);
		threeParts.setVisible(false);
		design = Design.TWOPARTS;
	}

	/**
	 * Es werden drei Bereiche angezeigt
	 */
	public void setContentToThreeParts() {
		oneParts.setVisible(false);
		twoParts.setVisible(false);
		threeParts.setVisible(true);
		design = Design.THREEPARTS;
	}

	/**
	 * Rechnet den übergebenen Prozentwert in Pixel um
	 * @param percent
	 * @param absolutly100
	 * @return
	 */
	private int percentToPixel(double percent, int absolutly100) {
		return (int) percent * absolutly100 / 100;
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		
	}

	/* (non-Javadoc)
	 * @see de.tud.vcd.votedevice.multielection.VCDView#setListener(de.tud.vcd.votedevice.multielection.VCDListener)
	 */
	public void setListener(VCDListener l)
			throws CannotConvertListenerException {
		
	}

	/**
	 * @return the vdg
	 */
	public VotingDeviceGui getVdg() {
		return vdg;
	}

}
