package de.tud.vcd.votedevice.view;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import de.tud.vcd.common.PropertyHandler;

/**
 * Abgerundeter Button der selbst erstellt wird, um von dem Standard Look & Feel von Java anzuweichen.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class JRoundedButton extends JComponent {
	private Color shade;
	private Color innerShade;
	private boolean pressed ;
	private static final long serialVersionUID = 1L;
	
	private ArrayList<ActionListener> al;
	private String actionCommand;
	protected Color backgroundColor;
	private JComponent comp;

	
	/**
	 * Erzeugt den Button mit Schriftzug, schriftart und Farbgestaltung
	 * @param name
	 * @param foregroundColor
	 * @param backgroundColor
	 * @param fontSize
	 */
	public JRoundedButton(String name, Color foregroundColor, Color backgroundColor, int fontSize) {

		shade = new Color(215, 215, 215);
		innerShade = new Color(190, 190, 190);

		pressed = false;
		
		al= new ArrayList<ActionListener>();
		actionCommand="";
		this.backgroundColor=backgroundColor;
		comp= this;
		{
			setOpaque(false);
			setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));// Bewirkt
																		// den
																		// innenabstand
			setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));// Bewirkt
			// den
			// innenabstand
			setLayout(new BorderLayout(0, 0));
			
			JLabel caption=new JLabel(name, JLabel.CENTER);
			
			
			caption.setFont(new Font(caption.getFont().getFamily(),caption.getFont().getStyle(),fontSize ));
			caption.setForeground(foregroundColor);
			add(caption);
			//Mauslistener um auf einen Klick zu reagieren
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					if (SwingUtilities.isLeftMouseButton(e)
							&& getButtonShape().contains(e.getPoint())&& isEnabled()) {
						pressed = true;
						repaint();
					}
				}

				public void mouseReleased(MouseEvent e) {
					if (SwingUtilities.isLeftMouseButton(e) && pressed && isEnabled()) {
						//call all actionPerformed:
						for(ActionListener a:al){
							a.actionPerformed(new ActionEvent(comp, ActionEvent.ACTION_PERFORMED, actionCommand));
						}
						
						pressed = false;
						repaint();
					}
				}
			});
			
			//addComponentListener(l)
		}

	};
	
	/**
	 * Erzeugt den Button mit Schriftzug und Farbgestaltung
	 * @param name
	 * @param foregroundColor
	 * @param backgroundColor
	 */
	public JRoundedButton(String name, Color foregroundColor, Color backgroundColor) {

		shade = new Color(215, 215, 215);
		innerShade = new Color(190, 190, 190);

		pressed = false;
		
		al= new ArrayList<ActionListener>();
		actionCommand="";
		this.backgroundColor=backgroundColor;
		comp= this;
		{
			setOpaque(false);
			setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));// Bewirkt
																		// den
																		// innenabstand
			setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));// Bewirkt
			// den
			// innenabstand
			setLayout(new BorderLayout(0, 0));
			
			JLabel caption=new JLabel(name, JLabel.CENTER);
			
			PropertyHandler ph= new PropertyHandler("configuration.properties");
			int res_x=Integer.parseInt(ph.getProperty("RESOLUTION_X", "1920"));
			int res_y=Integer.parseInt(ph.getProperty("RESOLUTION_Y", "1080"));
			Dimension screenSize= new Dimension(res_x, res_y);
			//Juri
			//GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			//int width = gd.getDisplayMode().getWidth();
			//int height = gd.getDisplayMode().getHeight();
			//Dimension screenSize = new Dimension(width, height);
			
			caption.setFont(new Font(caption.getFont().getFamily(),caption.getFont().getStyle(),(int)((double)(screenSize.width)/90) ));
			caption.setForeground(foregroundColor);
			add(caption);

			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					if (SwingUtilities.isLeftMouseButton(e)
							&& getButtonShape().contains(e.getPoint())&& isEnabled()) {
						pressed = true;
						repaint();
					}
				}

				public void mouseReleased(MouseEvent e) {
					if (SwingUtilities.isLeftMouseButton(e) && pressed && isEnabled()) {
						//call all actionPerformed:
						for(ActionListener a:al){
							a.actionPerformed(new ActionEvent(comp, ActionEvent.ACTION_PERFORMED, actionCommand));
						}
						
						pressed = false;
						repaint();
					}
				}
			});
			
			//addComponentListener(l)
		}

	};
	
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
	}
	
	/**
	 * @return
	 */
	public String getActionCommmand(){
		return actionCommand;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled){
		super.setEnabled(enabled);
		backgroundColor=Color.RED;
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		
		//Composite comp = g2d.getComposite();
		RoundRectangle2D rr = getButtonShape();

		drawShade(g2d, rr, shade, 8);

		if (pressed) {
			g2d.setPaint(new GradientPaint(0, 5, new Color(Math.max(backgroundColor.getRed()-30,0), Math.max(backgroundColor.getGreen()-30,0),Math.max(backgroundColor.getBlue()-30,0)), 0,
					getHeight() - 5, new Color(Math.max(backgroundColor.getRed()-60,0), Math.max(backgroundColor.getGreen()-60,0),Math.max(backgroundColor.getBlue()-60,0))));
		} else {
			g2d.setPaint(new GradientPaint(0, 5, backgroundColor, 0,
					getHeight() - 5, new Color(Math.max(backgroundColor.getRed()-60,0), Math.max(backgroundColor.getGreen()-60,0),Math.max(backgroundColor.getBlue()-60,0))));
		}
		g2d.fill(rr);

		if (pressed) {
			Shape clip = g2d.getClip();
			g2d.setClip(rr);
			drawShade(g2d, rr, innerShade, 4);
			g2d.setClip(clip);
		}

		g2d.setPaint(new Color(0, 0, 0));
		g2d.draw(rr);
	}

	/**
	 * Malt den Schatten des Buttons
	 * @param g2d
	 * @param rr
	 * @param shadeColor
	 * @param width
	 */
	private void drawShade(Graphics2D g2d, RoundRectangle2D rr,
			Color shadeColor, int width) {
		Composite comp = g2d.getComposite();
		Stroke old = g2d.getStroke();
		width = width * 2;
		for (int i = width; i >= 2; i -= 2) {
			float opacity = (float) (width - i) / (width - 1);
			g2d.setColor(shadeColor);
			g2d.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, opacity));
			g2d.setStroke(new BasicStroke(i));
			g2d.draw(rr);
		}
		g2d.setStroke(old);
		g2d.setComposite(comp);
	}

	/**
	 * Liefert die Form des Buttons zurück. Einmal zum zeichnen und einmal, um den Klick innerhlab zu registrieren.
	 * @return
	 */
	private RoundRectangle2D getButtonShape() {
		return new RoundRectangle2D.Double(5, 5, getWidth() - 10,
				getHeight() - 10, (getHeight()) / 2, (getHeight()) / 2);
	}

	

}
