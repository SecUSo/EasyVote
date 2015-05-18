package de.tud.vcd.votedevice.ampel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.tud.vcd.common.PropertyHandler;

/**
 * Stellt eine Ampel dar, um den Zustand des Wahlgeräts zu symbolisieren. Kann im VotingDeviceController statt der NoStatusSignaling
 * Klasse eingesetzt werden.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class Ampel extends JFrame implements StatusSignaling {

	Color redColor;
	Color greenColor;
	Color orangeColor;
	
	Color redDarkColor;
	Color greenDarkColor;
	Color orangeDarkColor;
	
	
	Color redActiveColor;
	Color greenActiveColor;
	Color orangeActiveColor;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Ampel() {
		PropertyHandler prop= new PropertyHandler("ampel.properties");
		redColor= new Color(Integer.parseInt(prop.getProperty("redColor","0"),16));
		greenColor= new Color(Integer.parseInt(prop.getProperty("greenColor","0"),16));
		orangeColor= new Color(Integer.parseInt(prop.getProperty("orangeColor","0"),16));
		
		int decrease=200;
		redDarkColor= new Color(Math.max(0,redColor.getRed()-decrease), Math.max(0,redColor.getGreen()-decrease), Math.max(0,redColor.getBlue()-decrease));
		greenDarkColor= new Color(Math.max(0,greenColor.getRed()-decrease), Math.max(0,greenColor.getGreen()-decrease), Math.max(0,greenColor.getBlue()-decrease));
		orangeDarkColor= new Color(Math.max(0,orangeColor.getRed()-decrease), Math.max(0,orangeColor.getGreen()-decrease), Math.max(0,orangeColor.getBlue()-decrease));
		
		redActiveColor=redColor;
		orangeActiveColor=orangeDarkColor;
		greenActiveColor=greenDarkColor;
		
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		
		// Maximalanzahl an Bildschirmen:
		int maxMonitore = gs.length;
		
		int sollMonitorResultGui;
		try {
			sollMonitorResultGui = Integer.parseInt(prop.getProperty("monitorId","1"),10);
		} catch (Exception e2) {
			sollMonitorResultGui = 1;
		}
		if (!(sollMonitorResultGui > -1 && sollMonitorResultGui < maxMonitore))
			sollMonitorResultGui = 0;
		System.out.println("Ampel ist auf Monitor: "+sollMonitorResultGui);
		
		GraphicsConfiguration monitor=gs[sollMonitorResultGui].getDefaultConfiguration();
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Standardfenster auf Fullscreen setzen und Elemente entfernen:
		setUndecorated(true);
		setBounds(monitor.getBounds().x + 0, monitor.getBounds().y + 0, monitor.getBounds().width,
				monitor.getBounds().height );

		// dem Hauptfenster ein Layoutmanager setzen, um die Statuszeile nicht
		// im Grid zu haben
		getContentPane().setLayout(
				new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		// headline im BoxLayout:
		JPanel headlinePanel = new JPanel();
		headlinePanel.setLayout(new BoxLayout(headlinePanel, BoxLayout.X_AXIS));
		add(headlinePanel);
		getContentPane().setBackground(Color.WHITE);

		//Anzeigen
		setVisible(true);
	}
	
	/**
	 * Liefert einen der drei Ampelkreise. Wird dabei aus der Anzahl der Kreise (max) und der Position (index) berechnet.
	 * @param index
	 * @param max
	 * @return Shape
	 */
	private Shape getShapee(int index, int max){
		int height = this.getBounds().height;
		int width = this.getBounds().width;
		
		int margin=10;
		
		int elementHeight=(height-max*margin-margin)/max;
		int startPositionX= (width-elementHeight)/2;
		int startPositionY= margin + index*(elementHeight+margin);
		return new Ellipse2D.Double(startPositionX, startPositionY, elementHeight, elementHeight);
	}

	/* (non-Javadoc)
	 * @see de.tud.vcd.votedevice.ampel.StatusSignaling#setRed()
	 */
	public void setRed(){
		redActiveColor=redColor;
		orangeActiveColor=orangeDarkColor;
		greenActiveColor=greenDarkColor;
		paintComponents(getGraphics());
	}
	
	/* (non-Javadoc)
	 * @see de.tud.vcd.votedevice.ampel.StatusSignaling#setGreen()
	 */
	public void setGreen(){
		redActiveColor=redDarkColor;
		orangeActiveColor=orangeDarkColor;
		greenActiveColor=greenColor;
		paintComponents(getGraphics());
	}
	
	/* (non-Javadoc)
	 * @see de.tud.vcd.votedevice.ampel.StatusSignaling#setOrange()
	 */
	public void setOrange(){
		redActiveColor=redDarkColor;
		orangeActiveColor=orangeColor;
		greenActiveColor=greenDarkColor;
		paintComponents(getGraphics());
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Container#paintComponents(java.awt.Graphics)
	 */
	public void paintComponents(Graphics g){
		super.paintComponents(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		
		g2d.setColor(redActiveColor);
		g2d.fill(getShapee(0, 3));
		g2d.setColor(orangeActiveColor);
		g2d.fill(getShapee(1, 3));
		g2d.setColor(greenActiveColor);
		g2d.fill(getShapee(2, 3));
		
		

		
	}

	

//	/* (non-Javadoc)
//	 * @see de.tud.vcd.votedevice.ampel.StatusSignaling#unlockAllowed()
//	 */
//	public boolean unlockAllowed() {
//		return false;
//	}

	/* (non-Javadoc)
	 * @see de.tud.vcd.votedevice.ampel.StatusSignaling#setInit()
	 */
	public void setInit() {
		setRed();
		
	}
	
}
