package de.tud.vcd.votedevice.municipalElection.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.tud.vcd.votedevice.municipalElection.model.Candidate;

/**
 * PopUpMenü, welches grafisch gestaltet ist und daher keine Swingkomponente ist. Hierüber errfolgt die Stimmauswahl für einen 
 * Kandidaten.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class MunicipalElectionVotePopup extends JPanel {
	//public String feld;
	
	private Color foreground;
	private Color background;
	private Color red;
	private Color selected;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String prename;
	private int votes;
	private boolean crossed;
	private boolean autoDistributed;
	private int maxVotes;
	private int autoDistForecast;

	private int startHeight;
	private int elementHeight;
	
	
	private ArrayList<ActionListener> al;
	private JComponent comp;
	Image imgBallotChecked;
	Image imgBallotUnchecked ;
	Image imgBallotCheckedGray;
	String nextAction;

	/**
	 * @param foreground
	 * @param background
	 * @param red
	 * @param maxVotes
	 */
	public MunicipalElectionVotePopup(Color foreground, Color background,Color red, int maxVotes) {
		super();
		this.foreground=foreground;
		this.background=background;
		this.red=red;
		this.maxVotes=maxVotes;
		this.nextAction="";
		this.autoDistForecast=0;
		
		startHeight=30;
		elementHeight=50;

		setSize(250, 25+(maxVotes+2)*elementHeight+20);
		setOpaque(false);
		//calc selectedColor
		Color c= new Color((int)(background.getRed()*0.7), (int)(background.getGreen()*0.7), (int)(background.getBlue()*0.7));
		selected=c;
		
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
		
		comp=this;
		al=new ArrayList<ActionListener>();
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)
						&& contains(e.getPoint())) {
					//repaint();
				}
				//get the field which on which is clicked
				if (SwingUtilities.isLeftMouseButton(e) && MunicipalElectionVotePopup.this.isVisible()){
					System.out.println("Bin wohl leider noch sichtbar: "+MunicipalElectionVotePopup.this.isVisible());
					if(getCloseShape().contains(e.getPoint())){
						nextAction="close";
					}else if(getButtonShape(startHeight,elementHeight , 0).contains(e.getPoint())){
						//nextAction="autodist";
						nextAction="vote_"+(0);
					}else if(getButtonShape(startHeight,elementHeight , MunicipalElectionVotePopup.this.maxVotes+1).contains(e.getPoint())){
						nextAction="crossed";
					}else{
						for (int i=0;i<MunicipalElectionVotePopup.this.maxVotes;i++){
							if(getButtonShape(startHeight,elementHeight , i+1).contains(e.getPoint())){
								nextAction="vote_"+(i+1);
								break;
							}
						}
					}
				}
				
				
			}

			public void mouseReleased(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && !nextAction.equals("")&& MunicipalElectionVotePopup.this.isVisible() ) {
					//call all actionPerformed:
					for(ActionListener a:al){
						ActionEvent ae=new ActionEvent(comp, ActionEvent.ACTION_PERFORMED, nextAction);
						a.actionPerformed(ae);
					}
					nextAction="";
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
		
		//central state vars
		int picWidth=imgBallotChecked.getWidth(null);
		int picHeight=imgBallotChecked.getHeight(null);
		
		
		
		//Upcast --> more functions in Graphics2D
		Graphics2D g2d=(Graphics2D)g;
		
		// Antialiasing einschalten
		g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, 
			RenderingHints.VALUE_ANTIALIAS_ON );
		
		g2d.setColor(foreground);
		RoundRectangle2D rr = getButtonShape();
		g2d.fill(rr);
		g2d.setColor(foreground);
		//g2d.setPaint(foreground);
		g2d.draw(rr);
		
		
		//close button
		g2d.setColor(background);
		rr = getCloseShape();
		g2d.fill(rr);
		g2d.setPaint(foreground);
		g2d.draw(rr);
		
		Font font= new Font("SanfSerif", Font.BOLD,18 );
		g2d.setFont(font);
		g2d.setColor(foreground);
		g2d.drawString("x", getWidth()-16, 16);
		
		//Namen schreiben:
		int fontsize=18;
		font= new Font("SanfSerif", Font.PLAIN,fontsize );
		
		g2d.setFont(font);
				
		
		
		g2d.setPaint(background);
		
		g2d.drawString(id+" "+name+", "+prename, 5, fontsize+5);
		
		//Nun die einzelnen Felder zeichen:
		
		for (int i=0; i<(maxVotes+2);i++){
			Rectangle2D r=getButtonShape(startHeight, elementHeight, i);
			//g2d.draw(r);
			g2d.setPaint(background);
			g2d.fill(r);
			if (i==(maxVotes+2)-1){
				if (this.crossed){
					g2d.setPaint(selected);
					g2d.fill(r);
				}
				//paint crossed
				g2d.setPaint(foreground);
				g2d.drawString("Streichen", 10, (int)(r.getCenterY()+fontsize/2));
				
				for (int k=0;k<maxVotes;k++){
					g2d.drawImage(imgBallotUnchecked, (getWidth() - ((maxVotes - k)*picWidth)-10),(int)(r.getCenterY()-picHeight/2) , null);
					
				}
				
				g2d.setColor(red);
				g2d.fillRect((getWidth() - ((maxVotes)*picWidth)-10), (int)(r.getCenterY()), picWidth*maxVotes, 2);
				
				
			}else{
				//normal votes
				//if (!this.autoDistributed && votes==i){
				if (!crossed && votes==i){
					g2d.setPaint(selected);
					g2d.fill(r);
				}
				g2d.setPaint(foreground);
				g2d.drawString((i)+" Stimmen", 10, (int)(r.getCenterY()+fontsize/2));
				//autoDistForecast=3;
				for (int k=0;k<maxVotes;k++){
					if(k<i){
						g2d.drawImage(imgBallotChecked, (getWidth() - ((maxVotes - k)*picWidth)-10),(int)(r.getCenterY()-picHeight/2) , null);
					}else if (k<(autoDistForecast+i)){//Das i muss addiert werden, da dies die simulierten manuellen Stimmen sind
						g2d.drawImage(imgBallotCheckedGray, (getWidth() - ((maxVotes - k)*picWidth)-10),(int)(r.getCenterY()-picHeight/2) , null);
					}else{
						g2d.drawImage(imgBallotUnchecked, (getWidth() - ((maxVotes - k)*picWidth)-10),(int)(r.getCenterY()-picHeight/2) , null);
					}
						
						
						
				}
			}
		}
		
	}
	
	/**
	 * Liefert die Form zurück
	 * @return
	 */
	private RoundRectangle2D getButtonShape() {
		return new RoundRectangle2D.Double(0, 0, getWidth() - 1,
				getHeight() - 1, 25,25);
	}
	
	/**
	 * Liefert die Form einer bestimmte Position des Menüs zurück
	 * @param startHeight
	 * @param height
	 * @param i
	 * @return
	 */
	private Rectangle2D getButtonShape(int startHeight,int height, int i) {
		return new Rectangle2D.Double(0+2, startHeight+i*height+2, getWidth() - 1-3,
				height-2	);
	}
	
	/**
	 * Liefert die Form für die Schließenecke oben rechts zurück
	 * @return
	 */
	private RoundRectangle2D getCloseShape() {
		int groesse=20;
		return new RoundRectangle2D.Double(getWidth()-groesse, 0,groesse-1,
				groesse-1, 0,0);
	}
	
	/**
	 * @param al
	 */
	public void addActionListener(ActionListener al){
		this.al.add(al);
	}
	
	
	/**
	 * Setzt einen Kandidaten c und eine Vorschau wie viel Stimmen er bekommen würde
	 * @param c
	 * @param autoDistForecast
	 */
	public void setCandidate(Candidate c, int autoDistForecast){
		id=c.getId();
		name=c.getName();
		prename=c.getPrename();
		votes=c.getVotes();
		crossed=c.isCrossedOut();
		autoDistributed=c.isAutoDistribution();
		this.autoDistForecast=autoDistForecast;
		repaint();
	}
	

}
