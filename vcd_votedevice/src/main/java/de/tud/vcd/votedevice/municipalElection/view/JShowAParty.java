package de.tud.vcd.votedevice.municipalElection.view;

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

/**
 * Zeigt eine Partei auf der Oberfläche an. Malt die Oberfläche selbst, da kein Standardswingobjekt in Frage kommt.
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class JShowAParty extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Color foreground;
	//private int id_maxSize;
	private String name;
	private boolean voted;
	private Font font;
	private Image imgBallotChecked;
	private Image imgBallotUnchecked;
	
	private ArrayList<ActionListener> al;
	private String actionCommand;
	private JComponent comp;
	private Shape ballotShape;
	
	/**
	 * Es wird ohne Layoutmanager gearbeitet, daher wird die Größe und die Schriftfarbe mit übergeben
	 * @param width
	 * @param height
	 * @param foreground
	 */
	public JShowAParty(int width, int height, Color foreground){
		this.foreground=foreground;
		setBackground(Color.ORANGE);
		setOpaque(false);
		setSize(width, height);
		//
		font= new Font("SanfSerif", Font.PLAIN,(int)(this.getSize().height*0.65) );
		System.out.println("Partyfeld:"+(int)(this.getSize().height));
		
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
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
		
		imageURLl =getClass().getClassLoader().getResource("ballotUnchecked50.gif");
		imgBallotUnchecked=null;
		if (imageURLl != null) {
			try {
				imgBallotUnchecked = ImageIO.read(imageURLl);
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
		
		//Listener zum Erkennen der Mausklicks auf den Kreis
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
				System.out.println("KLick aber gefunden");
				if (SwingUtilities.isLeftMouseButton(e) && ballotShape.contains(e.getPoint()) ) {
					//call all actionPerformed:
					for(ActionListener a:al){
						ActionEvent ae=new ActionEvent(comp, ActionEvent.ACTION_PERFORMED, actionCommand);
						a.actionPerformed(ae);
						System.out.println("KLick aber gefunden");
						
					}
					
					//pressed = false;
					//repaint();
				}
			}
		});
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
		//g2d.drawLine(0 , );
		g2d.fillRect(0, (int)(this.getSize().height*(1-0.06)) , this.getSize().width, this.getSize().height-1);
				
		g2d.setFont(font);
		g2d.drawString(name, 0, (int)(this.getSize().height*(0.80)));
		
		
		g2d.setFont(new Font(font.getFamily(), Font.BOLD, font.getSize()));
		//paint boxes
		int imgSize=(int)(this.getSize().height*0.80);	// 20/26
		int imgMarginTop=(int)(this.getSize().height*0.08); // 4/26
		ballotShape=new Ellipse2D.Double((int)(this.getSize().width*0.4) ,imgMarginTop, imgSize, imgSize);
		if (voted){
			g2d.drawImage(imgBallotChecked, (int)(this.getSize().width*0.4) , imgMarginTop, imgSize, imgSize, null, null);
		}else{
			g2d.drawImage(imgBallotUnchecked, (int)(this.getSize().width*0.4) , imgMarginTop, imgSize, imgSize, null, null);
		}
		

		
		
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
	
	/**
	 * Setzt in die Anzeige eine Partei und sorgt für das Neuzeichnen.
	 * @param p
	 */
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
	
	
}
