package de.tud.vcd.votedevice.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Erzeugt einen Button speziell zur Anzeige links im Menü für die Parteienauswahl. Der Unterschied zum RoundedButton ist 
 * die Einbindung einer Grafik vor dem Text und dem Wissen über eine Id der Partei.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class JRoundedPartyButton extends JRoundedButton  {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Erzeugt den Button
	 * @param listId
	 * @param name
	 * @param foregroundColor
	 * @param backgroundColor
	 * @param icon
	 * @param fontsize
	 */
	public JRoundedPartyButton(int listId, String name, Color foregroundColor,
			Color backgroundColor, ImageIcon icon, int fontsize) {
		super(name, foregroundColor, backgroundColor);
		//remove old label, create new Label
		this.removeAll();
		String id="";
		if (listId!=-1){
			id=listId+"";
		}
		JLabel caption=new JLabel(id+" "+name, JLabel.LEFT);
		caption.setFont(new Font(caption.getFont().getFamily(),caption.getFont().getStyle(),fontsize ));
		caption.setForeground(foregroundColor);
		if(icon==null){
			//create empty image
			BufferedImage i = new BufferedImage(25,25,BufferedImage.TYPE_INT_ARGB);
			caption.setIcon(new ImageIcon(i));
		}else{
			caption.setIcon(icon);
		}
		add(caption);
	}
}
