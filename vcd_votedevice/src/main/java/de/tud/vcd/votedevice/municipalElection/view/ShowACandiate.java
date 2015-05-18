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
package de.tud.vcd.votedevice.municipalElection.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.tud.vcd.votedevice.municipalElection.model.Candidate;

/**
 * Zeichnet einen Kandidaten auf die Oberfläche. Die Größe wird dabei auch wieder variabel berechnet.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class ShowACandiate extends JPanel {

	private static final long serialVersionUID = 1L;
	private Color foreground;
	private Color crossedColor;
	private String id;
	private int id_maxSize;
	private String name;
	private String prename;
	private boolean crossed;
	private int votes;
	private int manualVotes;
	private Font fontId;
	private Font fontName;
	private Font fontForename;
	private int maxVotes;
	private Image imgBallotChecked;
	private Image imgBallotUnchecked;
	private Image imgBallotCheckedGray;
	
	boolean pressed;
	private ArrayList<ActionListener> al;
	private String actionCommand;
	private JComponent comp;
	
	
	/**
	 * Erzeugt ein Zeichenobjekt
	 * @param width
	 * @param height
	 * @param foreground
	 * @param crossedColor
	 * @param maxVotes
	 */
	public ShowACandiate(int width, int height, Color foreground,Color crossedColor, int maxVotes) {
		this.foreground=foreground;
		this.crossedColor=crossedColor;
		setOpaque(false);
		setSize(width, height);
		//
		fontId= new Font("SanfSerif", Font.PLAIN,(int)(this.getSize().height*0.7) );
		fontName=new Font(fontId.getFamily(), Font.BOLD, (int)(this.getSize().height*0.577));
		fontForename=new Font(fontId.getFamily(), Font.PLAIN, (int)(this.getSize().height*0.5));
		
		id="0000";
		
		FontMetrics fm = getFontMetrics(fontId);
		id_maxSize=fm.stringWidth(id);
		
		name="Name";
		prename="Prename";
		crossed=false;
		votes=0;
		manualVotes=0;
		this.maxVotes=maxVotes;
		
		URL imageURLl =getClass().getClassLoader().getResource("ballotChecked.gif");
		imgBallotChecked=null;
		if (imageURLl != null) {
		    try {
				imgBallotChecked = ImageIO.read(imageURLl);
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
		
		imageURLl =getClass().getClassLoader().getResource("ballotUnchecked.gif");
		imgBallotUnchecked=null;
		if (imageURLl != null) {
			try {
				imgBallotUnchecked = ImageIO.read(imageURLl);
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
		
		imageURLl =getClass().getClassLoader().getResource("ballotCheckedGray.gif");
		imgBallotCheckedGray=null;
		if (imageURLl != null) {
			try {
				imgBallotCheckedGray = ImageIO.read(imageURLl);
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
		
		al=new ArrayList<ActionListener>();
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)
						&& contains(e.getPoint())) {
					pressed = true;
					//repaint();
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && pressed) {
					//call all actionPerformed:
					for(ActionListener a:al){
						a.actionPerformed(new ActionEvent(comp, ActionEvent.ACTION_PERFORMED, actionCommand));
					}
					
					pressed = false;
					//repaint();
				}
			}
		});
		
		
	}
	
	/**
	 * @param al
	 */
	public void addActionListener(ActionListener al){
		this.al.add(al);
	}
	
	/**
	 * @param actionCommand
	 */
	public void setActionCommand(String actionCommand){
		this.actionCommand=actionCommand;
		//.paramString= 
	}
	
	/**
	 * @return
	 */
	public String getActionCommmand(){
		return actionCommand;
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//Upcast --> more functions in Graphics2D
		Graphics2D g2d=(Graphics2D)g;
		
		// Antialiasing einschalten
		g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, 
			RenderingHints.VALUE_ANTIALIAS_ON );
		
		//g.drawString("FOO", 10, 5);
		g2d.setColor(foreground);
		g2d.drawLine(0 , this.getSize().height-1 , this.getSize().width, this.getSize().height-1);
		
		//print id:
		
		//fontId=new Font(fontId.getFamily(), Font.BOLD, fontId.getSize()-2);
		
		g2d.setFont(fontId);
		g2d.drawString(id, 0, this.getSize().height-5);
		
		
		g2d.setFont(fontName);
		
		g2d.drawString(name, id_maxSize+5, this.getSize().height-5);
		
		
		FontMetrics fm = getFontMetrics(fontName);
		int name_Size=fm.stringWidth(name);
		
		g2d.setFont(fontForename);
		
		g2d.drawString(", "+prename, id_maxSize+5+name_Size, this.getSize().height-5);
		
		//paint boxes
		int imgSize=(int)(this.getSize().height*0.77);	// 20/26
		int imgMarginTop=(int)(this.getSize().height*0.15); // 4/26
		int imgPlace=(int)(this.getSize().height*0.846); // (20+2)/26
		
		for (int i=0;i<maxVotes;i++){
			if (i<votes){
				if (manualVotes<=i){
					g2d.drawImage(imgBallotCheckedGray, this.getSize().width-(maxVotes*(imgPlace) -i*imgPlace) , imgMarginTop, imgSize, imgSize, null, null);
				}else{
					g2d.drawImage(imgBallotChecked, this.getSize().width-(maxVotes*(imgPlace) -i*imgPlace) , imgMarginTop, imgSize, imgSize, null, null);
				}
			}else{
				g2d.drawImage(imgBallotUnchecked, this.getSize().width-(maxVotes*(imgPlace) -i*imgPlace), imgMarginTop, imgSize, imgSize, null, null);
			}
		}
		if (crossed){
			g2d.setColor(crossedColor);
			g2d.drawLine(0 , this.getSize().height/2 , this.getSize().width, this.getSize().height/2);
			g2d.drawLine(0 , this.getSize().height/2+1 , this.getSize().width, this.getSize().height/2+1);
		}
		
	}

	/**
	 * @param c
	 */
	public void setCandidate(Candidate c) {
		if (!(c == null)) {
			id = c.getId() + "";
			actionCommand = c.getId() + "";
			name=c.getName();
			prename=c.getPrename();
			votes=c.getCountedVotes();
			manualVotes=c.getVotes();
			crossed=c.isCrossedOut();
			setVisible(true);
		} else {
			id="0";
			name="";
			prename="";
			crossed=false;
			votes=0;
			manualVotes=0;
			setVisible(false);
		}
		repaint();
	}

	/**
	 * @return
	 */
	public int getCandidateId(){
		return Integer.parseInt(this.id);
	}
	
	
}
