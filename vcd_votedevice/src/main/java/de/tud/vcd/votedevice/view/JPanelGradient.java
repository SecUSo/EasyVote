package de.tud.vcd.votedevice.view;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JPanel;

/**
 * Erzeugt ein Panel mit Farbverlauf. Dieser wird über die Konfiguration eingelesen.
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class JPanelGradient extends JPanel {
	private Color color1;
	private Color color2;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Erstellt das Panel und liest die Farben aus der Konfig
	 */
	public JPanelGradient() {
		super();
		
		int bgcolor1_r = Integer.parseInt(propertyHandler("bgcolor1_r","0"));
        int bgcolor1_g = Integer.parseInt(propertyHandler("bgcolor1_g","0"));
        int bgcolor1_b = Integer.parseInt(propertyHandler("bgcolor1_b","0"));
        
        int bgcolor2_r = Integer.parseInt(propertyHandler("bgcolor2_r","0"));
        int bgcolor2_g = Integer.parseInt(propertyHandler("bgcolor2_g","0"));
        int bgcolor2_b = Integer.parseInt(propertyHandler("bgcolor2_b","0"));
        
        this.color1 = new Color(bgcolor1_r, bgcolor1_g, bgcolor1_b);
		this.color2 = new Color(bgcolor2_r, bgcolor2_g, bgcolor2_b);
	}
	
	
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int panelHeight = getHeight();
		int panelWidth = getWidth();
		GradientPaint gradientPaint = new GradientPaint(0, 0, color1,0, panelHeight, color2);
		if (g instanceof Graphics2D) {
			Graphics2D graphics2D = (Graphics2D) g;
			graphics2D.setPaint(gradientPaint);
			graphics2D.fillRect(0, 0, panelWidth, panelHeight);
		}
	}
	
	/**
	 * Liest eine Property aus.
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String propertyHandler(String key, String defaultValue){
		Properties properties = new Properties();
		BufferedInputStream stream;
		try {
			InputStream filename=getClass().getClassLoader().getResource("configuration.properties").openStream();
			stream = new BufferedInputStream(filename);
			properties.loadFromXML(stream);
			stream.close();
			return properties.getProperty(key, defaultValue);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return defaultValue;
		
	}

}
