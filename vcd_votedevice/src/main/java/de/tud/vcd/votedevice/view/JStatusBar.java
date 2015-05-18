package de.tud.vcd.votedevice.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import de.tud.vcd.common.PropertyHandler;

/**
 * Stellt die Statusleiste zur Verfügung. Basiert auf einem JPanel und wird angepasst.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class JStatusBar extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Color neutralColor;
	Color validColor;
	Color invalidColor;
	Color warningColor;
	Color statusBarTextColor;
	JLabel content;
	
	/**
	 * Erzeugt die Statusanzeige
	 */
	public JStatusBar() {
		super();
		
		try{
			validColor= new Color(Integer.parseInt(propertyHandler("validColor","0"),16));
			invalidColor= new Color(Integer.parseInt(propertyHandler("invalidColor","0"),16));
			neutralColor= new Color(Integer.parseInt(propertyHandler("neutralColor","0"),16));
			warningColor= new Color(Integer.parseInt(propertyHandler("warningColor","0"),16));
			statusBarTextColor= new Color(Integer.parseInt(propertyHandler("statusBarTextColor","0"),16));
		}catch(NumberFormatException e){
			
		}
       
		
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS) );
		setBackground(neutralColor);
		content = new JLabel("HELLO WORLD",SwingConstants.LEFT);
		
		PropertyHandler ph= new PropertyHandler("configuration.properties");
		int res_x=Integer.parseInt(ph.getProperty("RESOLUTION_X", "1920"));
		int res_y=Integer.parseInt(ph.getProperty("RESOLUTION_Y", "1080"));
		Dimension screenSize= new Dimension(res_x, res_y);
		
		content.setFont(new Font("SansSerif",Font.PLAIN,(int)((double)(screenSize.width)/80)));
		content.setForeground(statusBarTextColor);
		content.setBorder(new EmptyBorder(5,40,5,20));
		content.setSize(new Dimension(this.getWidth(), this.getHeight()));
		this.add(content);
		this.add(Box.createHorizontalGlue());
		
		
	}
	
	/**
	 * Setzt die Statusbar auf ungültig
	 */
	public void setInvalidColor(){
		setBackground(invalidColor);
	}
	
	/**
	 * Setzt die Statusbar auf gültig
	 */
	public void setValidColor(){
		setBackground(validColor);
	}
	
	/**
	 * Setzt die Farbe für heilbare Stimmzettel
	 */
	public void setWarningColor(){
		setBackground(warningColor);
	}
	
	/**
	 * Startfarbe, möglichst neutral
	 */
	public void setNeutralColor(){
		setBackground(neutralColor);
	}

	/**
	 * Setzt den Text der auf der Statusleiste angezeigt wird.
	 * @param text
	 */
	public void setText(String text){
		
		content.setSize(new Dimension(this.getWidth(), this.getHeight()));
		
		FontMetrics fm = getFontMetrics(content.getFont());
		if (fm.stringWidth(text)>this.getSize().width){
			System.out.println("WARNING: StatusBar: not fitting content!");
		}
		content.setText(text);
		
	}

	/**
	 * Liest Parameter aus der Konfiguration aus.
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	private String propertyHandler(String key, String defaultValue){
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
