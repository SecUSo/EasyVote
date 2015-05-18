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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import de.tud.vcd.votedevice.municipalElection.model.BallotCard;
import de.tud.vcd.votedevice.municipalElection.model.Candidate;
import de.tud.vcd.votedevice.view.JRoundedButton;

/**
 * Bildschirmtastatur. Stellt die benötigten Eingaben für die Suchenfunktio bereit
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class OnScreenKeyboard extends JPanel {
	
	private Color foreground;
	private Color background;

	private static final long serialVersionUID = 1L;

	private ArrayList<ActionListener> al;
	Image imgBallotChecked;
	Image imgBallotUnchecked;
	Image imgBallotCheckedGray;
	String nextAction;

	//verfügbaren Tasten
	String[] keys = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "q",
			"w", "e", "r", "t", "z", "u", "i", "o", "p", "ü", "a", "s", "d",
			"f", "g", "h", "j", "k", "l", "ö", "ä", "y", "x", "c", "v", "b",
			"n", "m", "ß", " ", "-", "." };

	OnScreenKey[] buttons;

	HashSet<Character> validKeys;

	Searcher searcher;
	BallotCard bc;
	VCDListModel listModel;
	JTextField text;
	OnScreenKeyboard instance;
	
	int searchWidth = 700;
	int searchHeight = 550;
	
	int offsetX=200;
	int offsetY=60;

	/**
	 * Erzeugt die tastatur mit den vorgegebenen Daten und macht das Modell bekannt.
	 * @param foreground
	 * @param background
	 * @param bc
	 * @param w
	 * @param h
	 * @param screensize
	 */
	public OnScreenKeyboard(Color foreground, Color background, BallotCard bc, int w, int h, Dimension screensize) {
		super();
		validKeys = new HashSet<Character>();
		instance=this;
		al=new ArrayList<ActionListener>();
		// ActionListener für normale Zeicheneingabe
		ActionListener charAL = new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				text.setText(text.getText() + e.getActionCommand());
				performSearch();

			}
		};
		
		offsetX=(screensize.width-searchWidth)/2;
		offsetY=(screensize.height-searchHeight)/2;
		// ActionListener für Steuerbefehle
		ActionListener cmdAL = new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("SEARCH")) {
					performSearch();
				} else if (e.getActionCommand().equals("BACK")) {
					text.setText(text.getText().substring(0,
							Math.max(0, text.getText().length() - 1)));
					performSearch();
				}

			}
		};

		searcher = new Searcher();
		this.bc = bc;

		this.foreground = foreground;
		this.background = background;
		this.nextAction = "";

		

		setSize(w, h);
		setOpaque(false);

//		URL imageURLl = getClass().getClassLoader().getResource(
//				"basckButton.gif");
//		Image backButtonImage = null;
//		if (imageURLl != null) {
//			try {
//				backButtonImage = ImageIO.read(imageURLl);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			// Image image = imgBallotChecked.getImage(); // transform it
//			// image = image.getScaledInstance(20, 20, Image.SCALE_FAST); //
//			// scale it the smooth way
//			// imgBallotChecked = new ImageIcon(image); // transform it back
//		}

		this.setLayout(null);
		// Tasten einfügen
		int keyW = 57;
		int keyH = 45;
		int margin = 6;
		// 1.Reihe
		buttons = new OnScreenKey[keys.length];
		for (int i = 0; i < 10; i++) {
			buttons[i] = new OnScreenKey(keys[i], foreground, background);
			buttons[i].setBounds(offsetX+margin + i * (keyW + margin), offsetY+searchHeight - 4
					* (keyH + margin), keyW, keyH);
			buttons[i].setVisible(true);
			buttons[i].setActionCommand(keys[i]);
			buttons[i].addActionListener(charAL);
			this.add(buttons[i]);

		}
		//2.Reihe
		for (int i = 0; i < 11; i++) {
			buttons[i + 10] = new OnScreenKey(keys[i + 10], foreground,
					background);
			buttons[i + 10].setBounds(offsetX+margin + i * (keyW + margin),
					offsetY+searchHeight - 3 * (keyH + margin), keyW, keyH);
			buttons[i + 10].setVisible(true);
			buttons[i + 10].setActionCommand(keys[i + 10]);
			buttons[i + 10].addActionListener(charAL);
			this.add(buttons[i + 10]);

		}
		//3.Reihe
		for (int i = 0; i < 11; i++) {
			buttons[i + 21] = new OnScreenKey(keys[i + 21], foreground,
					background);
			buttons[i + 21].setBounds(offsetX+margin + i * (keyW + margin),
					offsetY+searchHeight - 2 * (keyH + margin), keyW, keyH);
			buttons[i + 21].setVisible(true);
			buttons[i + 21].setActionCommand(keys[i + 21]);
			buttons[i + 21].addActionListener(charAL);
			this.add(buttons[i + 21]);

		}
		//4. Reihe
		for (int i = 0; i < 11; i++) {
			buttons[i + 32] = new OnScreenKey(keys[i + 32], foreground,
					background);
			buttons[i + 32].setBounds(offsetX+margin + i * (keyW + margin),
					offsetY+searchHeight - 1 * (keyH + margin), keyW, keyH);
			buttons[i + 32].setVisible(true);
			buttons[i + 32].setActionCommand(keys[i + 32]);
			buttons[i + 32].addActionListener(charAL);
			this.add(buttons[i + 32]);

		}
		// Rücktaste
		OnScreenKey osk = new OnScreenKey("<", background, foreground);
		osk.setBounds(offsetX+margin + 10 * (keyW + margin), offsetY+searchHeight - 4
				* (keyH + margin), keyW, keyH);
		osk.setVisible(true);
		osk.setActionCommand("BACK");
		osk.addActionListener(cmdAL);
		this.add(osk);
		osk = new OnScreenKey("Suche", background, foreground);
		osk.setBounds(offsetX+margin + 9 * (keyW + margin), offsetY+searchHeight - 5
				* (keyH + margin), keyW * 2 + margin, keyH);
		osk.setVisible(true);
		osk.setActionCommand("SEARCH");
		osk.addActionListener(cmdAL);
		this.add(osk);

		text = new JTextField("");
		text.setBounds(offsetX+margin, offsetY+searchHeight - 5 * (keyH + margin),
				(keyW + margin) * 9 - margin, 30);
		text.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
		text.setVisible(true);
		text.setEditable(false);
		this.add(text);

		// Ausgabefeld:
		JList<VCDSearchable> resultList = new JList<VCDSearchable>(); // data has type
														// Object[]
		resultList
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		resultList.setCellRenderer(new VCDSearchableRenderer<VCDSearchable>());
		resultList.setLayoutOrientation(JList.VERTICAL);
		resultList.setVisibleRowCount(-1);
		resultList.setFont(new Font("Sans Serif", Font.PLAIN, 20));

		JScrollPane listScroller = new JScrollPane(resultList);
		//listScroller.setPreferredSize(new Dimension(250, 80));
		int marginTop = 40;
		listScroller.setBounds(offsetX+margin, offsetY+marginTop, searchWidth - 2 * margin,
				searchHeight - marginTop - 300);
		
		//Juri um eine breitere Fläche beim Touchscreen zu haben
		Component [] c = listScroller.getComponents();
		for(int i = 1; i<c.length; i++){
			c[i].setPreferredSize(new Dimension(40, 60));
		}
		listScroller
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.add(listScroller);

		listModel = new VCDListModel();
		// ListModel<String> lm= new List(5).

		resultList.setModel(listModel);

		// Action Listener für das Klicken eines Suchergebnisses. Löst dann
		// Ereignis in den eingetragenn externen Listenern aus
		resultList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getSource() instanceof JList<?>) {
					JList<?> list = (JList<?>) evt.getSource();
					if (evt.getClickCount() == 2) {
						Rectangle r = list.getCellBounds(0,	list.getLastVisibleIndex());
						Object listitem=list.getSelectedValue();
						if (listitem instanceof VCDSearchable && r != null && r.contains(evt.getPoint())) {
							String actionCmd="selectThisParty";
							if (listitem instanceof Candidate){
								actionCmd=((Candidate)listitem).getParty();
							}
							
							for (ActionListener a : al) {
								a.actionPerformed(new ActionEvent((VCDSearchable)listitem,
										ActionEvent.ACTION_PERFORMED,
										actionCmd));
							}
							System.out.println((VCDSearchable)listitem);
							instance.setVisible(false);
						}
					} 
				}
			}
			
		});
		//Listener zum Schließen des Fensters
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)
						&& contains(e.getPoint())) {
					// repaint();
				}
				if (getCloseShape().contains(e.getPoint())) {
					instance.setVisible(false);
				}
				
			}
			public void mouseReleased(MouseEvent e) {
				
			}
		});

		
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setVisible(boolean)
	 */
	public void setVisible(boolean aFlag){
		super.setVisible(aFlag);
		this.text.setText("");
		this.performSearch();
	}

	/**
	 * Weist der Tastatur ein anderes Modell zu
	 * @param bc
	 */
	public void setModel(BallotCard bc) {
		this.bc = bc;
	}

	/**
	 * Führt die Suche durch. Es wird dafür die durchsuchbaren Objekte im Modell angefragt und die positiven Funde eingetragen.
	 */
	private void performSearch() {
		listModel.clear();
		if (!text.getText().equals("")) {
			searcher = new Searcher();
			bc.search(searcher, text.getText());
			VCDSearchable[] results = searcher.getSearchedObjects();
			// Nun zur Liste hinzufügen

			for (VCDSearchable r : results) {
				listModel.addElement(r);
			}
			setValidButtons(searcher.nextSearchedCharacters());
		} else {
			setALLButtonsValid();
		}
	}

	/**
	 * Markiert die noch erlaubten Buttons als gültig und alle anderen als ungültig
	 * @param cHash
	 */
	private void setValidButtons(HashSet<Character> cHash) {
		for (OnScreenKey b : buttons) {
			if (cHash.contains(b.getActionCommmand().charAt(0))) {
				b.setEnabled(true);
			} else {
				b.setEnabled(false);
			}
		}
	}

	/**
	 * Erlaubt den Zugriff auf alle Buttons
	 */
	private void setALLButtonsValid() {
		for (JRoundedButton b : buttons) {
			b.setEnabled(true);

		}
	}

	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// central state vars

		// Upcast --> more functions in Graphics2D
		Graphics2D g2d = (Graphics2D) g;

		// Antialiasing einschalten
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.setColor(new Color(100, 100,100,128));
		Rectangle2D.Double bg=new Rectangle2D.Double(0,0,getWidth(), getHeight());
		g2d.fill(bg);
		
		g2d.setColor(foreground);
		RoundRectangle2D rr = getButtonShape();
		g2d.fill(rr);
		g2d.setColor(foreground);
		// g2d.setPaint(foreground);
		g2d.draw(rr);

		// close button
		g2d.setColor(background);
		rr = getCloseShape();
		g2d.fill(rr);
		g2d.setPaint(foreground);
		g2d.draw(rr);

		Font font = new Font("SanfSerif", Font.BOLD, 18);
		g2d.setFont(font);
		g2d.setColor(foreground);
		g2d.drawString("x",offsetX+ searchWidth - 16, offsetY+16);

		// Namen schreiben:
		int fontsize = 18;
		font = new Font("SanfSerif", Font.PLAIN, fontsize);

		g2d.setFont(font);

		g2d.setPaint(background);

		g2d.drawString("Suche:", offsetX+5, offsetY+fontsize + 5);

		g2d.drawLine(offsetX, offsetY+fontsize + 10, offsetX+searchWidth,offsetY+ fontsize + 10);

	}

	/**
	 * Gibt die Form einer taste zurück
	 * @return RoundRectangle2D
	 */
	private RoundRectangle2D getButtonShape() {
		return new RoundRectangle2D.Double(offsetX,offsetY, searchWidth - 1,
				searchHeight - 1, 25, 25);
	}

	/**
	 * Gibt die Form der Schließenschaltfläche zurück
	 * @return
	 */
	private RoundRectangle2D getCloseShape() {
		int groesse = 20;
		return new RoundRectangle2D.Double(offsetX+searchWidth - groesse,offsetY+ 0,
				groesse - 1, groesse - 1, 0, 0);
	}

	/**
	 * @param al
	 */
	public void addActionListener(ActionListener al) {
		this.al.add(al);
	}

	/**
	 * Ruft die Suche im Modell auf und sucht nach dem Parameter
	 * @param str
	 */
	public void search(String str) {
		// searcher.searchFor(bc, str);
		bc.search(searcher, str);
	}

	/**
	 * Liefert die zentrale Konfigurationsdatei zurück.
	 * @return
	 * @throws IOException
	 */
	public InputStream getBCD() throws IOException {
		return getClass().getClassLoader().getResource("wahlzettel.xml")
				.openStream();

	}

	/**
	 * @return Searcher
	 */
	public Searcher getSearcher() {
		return searcher;
	}


}
