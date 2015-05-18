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
package de.tud.vcd.votedevice.tuTestElection;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.tud.vcd.votedevice.municipalElection.model.Party;
import de.tud.vcd.votedevice.tuTestElection.TUTestElectionModel.VoteState;

public class TUTestShowOption extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Color foreground;
	//private int id_maxSize;
	private String name;
	private boolean voted;
	private VoteState voteState;
	private Font font;
	private Image imgBallotChecked;
	private Image imgBallotUnchecked;
	
	private ArrayList<ActionListener> al;
	private String actionCommand;
	private JComponent comp;
	private Shape ballotShape;
	
	public TUTestShowOption(int x, int y, int width, int height, Color foreground, VoteState vs){
		this.voteState=vs;
		this.foreground=foreground;
		setBackground(Color.ORANGE);
		setOpaque(false);
		setSize(width, height);
		setLocation(x, y);
		//
		font= new Font("SanfSerif", Font.PLAIN,24 );
		
		
		actionCommand="";
		comp=this;
//		
//		FontMetrics fm = getFontMetrics(font);
//		id_maxSize=fm.stringWidth(id);
		
		name="Name";
		voted=false;
		
		URL imageURLl =getClass().getClassLoader().getResource("ballotChecked50.gif");
		imgBallotChecked=null;
		if (imageURLl != null) {
		    try {
				imgBallotChecked = ImageIO.read(imageURLl);
				imgBallotChecked= imgBallotChecked.getScaledInstance(50, 50,Image.SCALE_SMOOTH);
			} catch (IOException e) {
				e.printStackTrace();
			}
		    //Image image = imgBallotChecked.getImage(); // transform it
		    //image = image.getScaledInstance(20, 20,  Image.SCALE_FAST); // scale it the smooth way 
		    //imgBallotChecked = new ImageIcon(image);  // transform it back
		}
		
		imageURLl =getClass().getClassLoader().getResource("ballotUnchecked50.gif");
		imgBallotUnchecked=null;
		if (imageURLl != null) {
			try {
				imgBallotUnchecked = ImageIO.read(imageURLl);
				imgBallotUnchecked= imgBallotUnchecked.getScaledInstance(50, 50,Image.SCALE_SMOOTH);
			} catch (IOException e) {
				e.printStackTrace();
			}
//		    Image image = imgBallotUnchecked.getImage(); // transform it
//		    image = image.getScaledInstance(20, 20,  Image.SCALE_FAST); // scale it the smooth way 
//		    imgBallotUnchecked = new ImageIcon(image);  // transform it back
		}
		
		
		al=new ArrayList<ActionListener>();
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)
						&& contains(e.getPoint())) {
					//pressed = true;
					//repaint();
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && ballotShape.contains(e.getPoint()) ) {
					//call all actionPerformed:
					for(ActionListener a:al){
						ActionEvent ae=new ActionEvent(comp, ActionEvent.ACTION_PERFORMED, actionCommand);
						a.actionPerformed(ae);
					}
					//pressed = false;
					//repaint();
				}
			}
		});
	}
	
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//Upcast --> more functions in Graphics2D
		Graphics2D g2d=(Graphics2D)g;
		
		// Antialiasing einschalten
		g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, 
			RenderingHints.VALUE_ANTIALIAS_ON );
		
		//g.drawString("FOO", 10, 5);
		g2d.setColor(foreground);
		//g2d.drawLine(0 , );
		g2d.drawRect(0, 0 , this.getSize().width-1, this.getSize().height-1);
				
		g2d.setFont(font);
		g2d.drawString(name, 10, (int)(this.getSize().height*(0.70)));
		
		
		g2d.setFont(new Font(font.getFamily(), Font.BOLD, font.getSize()));
		//paint boxes
		ballotShape=new Ellipse2D.Double((int)(this.getSize().width-60) , 15, 50, 50);
		if (voted){
			g2d.drawImage(imgBallotChecked, (int)(this.getSize().width-60) , 15, 50, 50, null, null);
		}else{
			g2d.drawImage(imgBallotUnchecked, (int)(this.getSize().width-60) ,15, 50, 50, null, null);
		}
		

		
		
	}
	
	public void addActionListener(ActionListener al){
		this.al.add(al);
	}
	
	public void setActionCommand(String actionCommand){
		this.actionCommand=actionCommand;
		//.paramString= 
	}
	
	public String getActionCommmand(){
		return actionCommand;
	}
	
	public void setParty(Party p) {
		if (!(p == null)) {
			actionCommand = p.getName();
			name=p.getName();
			if (name.equals("invalid")){
				name="Ungültig wählen";
			}
			voted=p.isVoted();
			
			setVisible(true);
		} else {
			name="";
			actionCommand = "";
			voted=false;
			setVisible(false);
		}
		repaint();
	}
	
	
	public void setVoteState(VoteState vs, String text, boolean selected){
		//System.out.println("SET:"+vs.toString()+": "+selected);
		name=text;
		actionCommand=vs.toString();
		voted=selected;
		repaint();
	}
	
	public VoteState getVoteState(){
		return voteState;
	}
	
}
