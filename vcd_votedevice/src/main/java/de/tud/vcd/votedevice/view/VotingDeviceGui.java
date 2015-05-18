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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.tud.vcd.common.PropertyHandler;

/**
 * Erzeugt die grafische Oberfläche für die Anzeige des Wahlsystems. Bindet dann das konkrete Wahlsystem ein.
 * Stellt sozusagen das Grundgerüst bereit.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class VotingDeviceGui extends JFrame implements Observer {

	private static final long serialVersionUID = 1L;
	private JPanelGradient jsearch;
	private JPanel content;
	private JPanelGradient jinfo;
	private JPanel searchOverlay;

	private JLabel infoHead;
	private JLabel infoBody;

	JRoundedButton searchButton;

	/**
	 * Erzeugt das Fenster und bindet es ohne Programmleiste im Vollbildschirm ein. Wenn in der Konfiguration eine Auflösung übergeben wird, so 
	 * wird diese verwendet.
	 */
	public VotingDeviceGui() {
		super("Vote Casting Device");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Undecorated window and set to fullscreen
		setUndecorated(true);
		//Juri
        Dimension screenSize = getScreenSize();
				
//	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//	    PropertyHandler ph = new PropertyHandler("configuration.properties");
//	    int res_x = Integer.parseInt(ph.getProperty("RESOLUTION_X", ""+screenSize.width));
//	    int res_y = Integer.parseInt(ph.getProperty("RESOLUTION_Y", ""+screenSize.height));
//		screenSize = new Dimension(res_x, res_y);
		
		setBounds(getBounds().x + 0, getBounds().y + 0, screenSize.width,
				screenSize.height);

		//
		this.setLayout(null);

		int statusBarHeight = percentToPixel(4.5, screenSize.height);

		content = new JPanel();
		content.setBounds(0, 0, percentToPixel(81, screenSize.width),
				screenSize.height);
		content.setBackground(Color.WHITE);
		content.setLayout(null);
		add(content);

		jsearch = new JPanelGradient();
		jsearch.setBounds(percentToPixel(81, screenSize.width), 0,
				percentToPixel(19, screenSize.width), statusBarHeight);
		add(jsearch);

		// ///////////////////////////////////////////////
		searchButton = new JRoundedButton("Suche", Color.WHITE, Color.GRAY);
		searchButton.setBounds(5, 5, jsearch.getWidth() - 10,
				jsearch.getHeight() - 10);
		searchButton.setVisible(false);
		jsearch.setLayout(null);
		jsearch.add(searchButton);
		// //////////////////////////////////////////////////

		// info section:
		jinfo = new JPanelGradient();
		jinfo.setBounds(percentToPixel(81, screenSize.width), statusBarHeight,
				percentToPixel(19, screenSize.width),
				percentToPixel(100, screenSize.height) - statusBarHeight);
		add(jinfo);
		infoHead = new JLabel("");
		infoHead.setPreferredSize(new Dimension(jinfo.getWidth() - 30, 60));

		infoHead.setFont(new Font("SansSerif", Font.BOLD,
				(int) ((double) (screenSize.width) / 64)));
		jinfo.add(infoHead);

		infoBody = new JLabel("");
		infoBody.setPreferredSize(new Dimension(jinfo.getWidth() - 30, jinfo
				.getHeight() - 100));
		infoBody.setFont(new Font("SansSerif", Font.PLAIN,
				(int) ((double) (screenSize.width) / 80)));
		infoBody.setVerticalAlignment(SwingConstants.TOP);
		jinfo.add(infoBody);
		//Suchenoverlay einbauen, welches später gesetzt werden kann
		searchOverlay = new JPanel();
		searchOverlay.setBounds(percentToPixel(5, screenSize.width),
				percentToPixel(20, screenSize.height),
				percentToPixel(90, screenSize.width),
				percentToPixel(70, screenSize.height));
		searchOverlay.setBackground(Color.YELLOW);
		searchOverlay.setVisible(false);
		add(searchOverlay, 0);

		// ////////////////////////

	}

	/**
	 * Setzt die Schriftfarbe im Infobereich
	 * @param c
	 */
	public void setInfoFontColor(Color c) {
		infoHead.setForeground(c);
		infoBody.setForeground(c);
	}

	/**
	 * Zeigt die Oberfläche an-
	 */
	public void showVCD() {
		this.setVisible(true);

	}
	//Juri 
	/**
	 * Liest die Auflösung aus der Konfiguration aus
	 * @return
	 */
	public Dimension getScreenSize() {
		PropertyHandler ph = new PropertyHandler("configuration.properties");
		int res_x = Integer.parseInt(ph.getProperty("RESOLUTION_X", "1920"));
		int res_y = Integer.parseInt(ph.getProperty("RESOLUTION_Y", "1080"));
		return new Dimension(res_x, res_y);
		
       // GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		//int width = gd.getDisplayMode().getWidth();
		//int height = gd.getDisplayMode().getHeight();
		//return new Dimension(width, height);
	}

	/**
	 * Weist der Anzeige eine neue Darstellung zu. Diese wird übergeben. Vorherige werden zuvor gelöscht
	 * @param panel
	 */
	public void setContent(JPanel panel) {
		this.content.removeAll();
		this.content.add(panel);
		repaint();
	}

	/**
	 * liefert das momentan angezeigte Objekt zurück
	 * @return
	 */
	public JPanel getContent() {
		return content;
	}

	/**
	 * Weist dem Suchenbutton einen Listener zu, der sich um die Suche kümmert.
	 * @param al
	 */
	public void setSearch(ActionListener al) {
		searchButton.addActionListener(al);
	}

	/**
	 * Zeigt die Suche an oder blendet sie aus.
	 * @param aFlag
	 */
	public void setSearchVisible(boolean aFlag) {
		searchButton.setVisible(aFlag);
	}

	/**
	 * Blendet die Info auf der rechten Seite ein bzw. legt den Inhalt fest.
	 * @param headline
	 * @param content
	 */
	public void setInfo(String headline, String content) {
		FontMetrics fm = getFontMetrics(infoHead.getFont());
		if (fm.stringWidth(headline) > infoHead.getPreferredSize().width) {
			//System.out.println("WARNING: Info Headline not fitted. ");
			
		}
		infoHead.setText(headline);

		fm = getFontMetrics(infoBody.getFont());
		if (fm.stringWidth(content) > infoBody.getPreferredSize().width) {
			//System.out.println("WARNING: Info Content not fitted. ");
			
		}
		infoBody.setText(content);
	}

	/**
	 * Rechnet % in Pixel um
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

}
